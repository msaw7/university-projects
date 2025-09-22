; Author: Mateusz Sawicki
; Program: freverse.asm
;
; Description:
;  Opens a single file given as argv[1], mmaps it and reverses its content.
;  Verifies that the given file is a regular file.
;
; Usage:
;  Compile with: nasm -f elf64 -w+all -w+error -o freverse.o freverse.asm
;  Create an executable: ld freverse.o -o freverse
;  ./freverse <filename>
;  Exits with code 0 on success, 1 on error

%define SYS_OPEN 2
%define SYS_CLOSE 3
%define SYS_FSTAT 5
%define SYS_MMAP 9
%define SYS_MUNMAP 11
%define SYS_SYNC 26
%define SYS_EXIT 60

%define redzone_size 128
%define st_size_offset 48
%define st_mode_offset 24
%define S_IFMT 0xF000  ; mask used to extract file type mask in st_mode
%define S_IFREG 0x8000 ; "regular" file mask in struct stat st_mode

%define MAP_SHARED 0x01
%define MMAP_ERROR 0xffffffffffffffff
%define MAP_RDWR 3

%define OPEN_RDWR 2

%define MS_SYNC 0x6

section .text
  global _start

_start:
  ; [rsp]     = argc     number of arguments
  ; [rsp+8]   = argv[0]
  ; [rsp+16]  = argv[1]  file name

.checkArguments:
  mov  rax, [rsp]       ; rax = argc
  cmp  rax, 2
  jne  .exitErr         ; If argc != 2, exit with error.
  mov  rdi, [rsp + 16]  ; rdi = pointer to file

.openFile:
  mov  rax, SYS_OPEN
  mov  rsi, OPEN_RDWR
  xor  rdx, rdx       ; mode = 0
  syscall

  ; failure check
  cmp  rax, 0
  jl   .exitErr  ; do not have to close file

.getFileInfo:
  mov r12, rax       ; save fd for later use
  mov rdi, rax
  ; we overwrite arguments to fit in redzone
  lea rsi, [rsp - redzone_size]
  mov rax, SYS_FSTAT
  syscall

  ; failure check
  test rax, rax
  jnz .closeAndExitErr

  ; check for improper file type
  mov eax, [rsp - redzone_size + st_mode_offset]
  and eax, S_IFMT
  cmp eax, S_IFREG
  jne .closeAndExitErr

  mov r13, [rsp - redzone_size + st_size_offset] ; r13 = file size in bytes

  ; check for empty file passed as argument
  ; (interferes with mmap)
  test r13, r13;
  jz .closeAndExit

.mapFile:
  xor rdi, rdi        ; no address suggestion
  mov rsi, r13        ; filesize
  mov rdx, MAP_RDWR   ; PROT = READWRITE
  mov r10, MAP_SHARED
  mov r8, r12         ; pass fd
  xor r9, r9          ; no offset
  mov rax, SYS_MMAP
  syscall
  cmp rax, 0          ; only negative values imply error
  jl .closeAndExitErr
  mov r14, rax        ; r14 = mapped area address

; We will start with two pointers to the qwords at the beginning and end of the file,
; then proceed to swap and reverse them, but finish before the qwords overlap.
; This will leave us with up to 15 bytes to reverse byte-by-byte.
.fileReversal:
  mov r9, r13          ; r9 = byte size of file
  shr r9, 4            ; r9 = qword size of file / 2
                       ; (This is rounded down, so it is the exact number of iterations
                       ; we wish to carry out.)
  lea rcx, [r14 + r13] ; rcx = address + n / 2 (points just past the end)
  lea rdx, [r14 - 8]   ; rdx = address - 1 (points just below the start)

.loopQwords:
  sub rcx, 8
  add rdx, 8
  test r9, r9
  jz .remainingBytes
  ; swap and reverse the qwords mapped at rcx and rdx
  mov rsi, [rcx]
  mov rdi, [rdx]
  bswap rsi
  bswap rdi
  mov [rcx], rdi
  mov [rdx], rsi
  dec r9;
  jmp .loopQwords

.remainingBytes:
  ; Notice how the first two lines of loopQwords execute even when r9 = 0.

  ; After finishing the qword loop, the lower pointer is in the right place.
  ; The upper pointed needs to be adjusted (so that it only moves one byte down total).
  add rcx, 7

.loopBytes:
  cmp rdx, rcx ; we finish when the two pointers meet
  jge .unmapFile
  ; swap and reverse the bytes mapped at rcx and rdx
  mov sil, [rcx]
  mov dil, [rdx]
  mov [rcx], dil
  mov [rdx], sil;
  inc rdx;
  dec rcx;
  jmp .loopBytes

; explicit unmapping instead of relying on sys_exit
.unmapFile:
  mov rdi, r14        ; mapped area address
  mov rsi, r13        ; file size
  mov rax, SYS_MUNMAP
  syscall

  ; failure check
  test rax, rax;
  jnz .closeAndExitErr

; close file and exit normally
.closeAndExit:
  mov  rax, SYS_CLOSE
  mov  rdi, r12      ; r12 = fd
  syscall

  ; if closing failed, indicate the failure
  test rax, rax
  jnz .exitErr

  mov  rax, SYS_EXIT
  xor  rdi, rdi      ; exitcode = 0
  syscall

; close file and exit with error
.closeAndExitErr:
  mov  rax, SYS_CLOSE
  mov  rdi, r12      ; r12 = fd
  syscall
  ; Note how this proceeds to the code below, ending the program with 1.

; exit with error but no file to close
.exitErr:
  mov  rax, SYS_EXIT
  mov  rdi, 1        ; exitcode = 1
  syscall
