; Author: Mateusz Sawicki
; Program: nsqrt.asm
;
; Description:
;  This is a function meant to be linked to a C program. 
;  It takes pointers to two arrays Q and X.
;  X represents an unsigned little-endian integer with 2n bits. 
;  n has to be a number divisible by 64.
;  This program calculates the square root of X and writes down the answer  
;  (an n-bit little-endian unsigned integer) in the Q array.
;  It uses no additional memory (except for 4 stack pushes).
;
; Usage:
;  Compile with: nasm -f elf64 -w+all -w+error -o nsqrt.o nsqrt.asm


section .text
  global nsqrt
  ; Input:
  ; rdi = *Q
  ; rsi = *X
  ; rdx = n

  ; Naming convention:
  ; r8 = N = n / 64
  ; r9 = j = iterator (for the main loop)
  ; r10 = i = iterator (for smaller loops)

nsqrt:

.pushNonVolatile: ; Preserve registers.
  push    r12
  push    r13
  push    r14
  push    r15

.prepare:
  ; r8 = N = n / 64
  mov r8, rdx
  shr r8, 6

  ; Set all values of Q[i] to zero.
  mov r11, rdi ; r11 = Q*
  xor rax, rax ; rax = 0
  mov rcx, r8  ; rcx = N
  rep stosq    ; Q[0, ... , N - 1] = 0
  mov rdi, r11 ; rdi = Q*

  mov r9, 1 ; j = 1

.mainloop: ; for(int j = 1; j <= n; j ++)
  cmp r9, rdx
  jg .end ; j > n

  ; a = 2(n - j) / 64 (r12)
  ; b = 2(n - j) % 64 (lowest 6 bits of rcx)
  mov r12, rdx ; r12 = n
  sub r12, r9  ; r12 = n - j
  shl r12, 1   ; r12 = 2(n - j)
  mov rcx, r12 ; rcx = 2(n - j)
  shr r12, 6   ; r12 = 2(n - j) / 64

  lea r10, [r8*2 - 1] ; r10 = i = 2N - 1
  mov rax, 1
  shl rax, cl         ; rax = 1 << b

; Evaluate if X>4^(n-j).
; First we check if the most significant blocks of X are non-zero.
.loopCheckSub: ; for(int i = 2N - 1; i > a; i --)
  cmp r10, r12
  jle .checkSubDone ; i <= a
  cmp qword [rsi + r10*8], 0
  jnz .geThanFour   ; X[i] >= 0
  dec r10           ; i --
  jmp .loopCheckSub
.checkSubDone:
  cmp [rsi + r12*8], rax ; X[a] > 2^b ?
  jb .maindone           ; If not, then q=0 and we are done.

.geThanFour:             ; X -= 4^(n-j)
  sub [rsi + r12*8], rax ; X[a] -= 2^b
  mov r10, r12           ; i = a

.whileCLCNZ: ; Propagate the CF of the subtraction upwards.
  jnc .compare
  inc r10
  sbb qword [rsi + r10*8], 0 ; X[i] -= CF
  jmp .whileCLCNZ

; Check if Q*2^(n-j+1) <= X.
; First we check for nonzero X blocks such that
; Q*2^(n-j+1) will always be smaller than the powers of two
; that the block represents.
.compare:
  lea r12, [rdx + 1]  ; r12 = n + 1
  sub r12, r9         ; r12 = n - j + 1
  mov rcx, r12        ; rcx = n - j + 1
  shr r12, 6          ; r12 = a = (n - j + 1) / 64
  lea r10, [2*r8 - 1] ; r10 = 2N-1

; We check for nonzero X blocks, going from i=2N-1 to N+a+1.
.checkHighBlocks:
  lea rax, [r8 + r12]        ; rax = N + a
  cmp r10, rax               ; i <= N + a ?
  jle .HighBlocksDone
  cmp qword [rsi + r10*8], 0 ; X[i] >= 0 ?
  jnz .XGreaterEqualThanQ
  dec r10                    ; i --
  jmp .checkHighBlocks
.HighBlocksDone:
  lea r11, [r8 + r12]     ; r11 = i + a
  mov rax, [rsi + r11*8]  ; rax = X[i + a]
  shr rax, cl             ; rax = most significant bits of X[i + a]
  test rax, rax           ; Are those nonzero?
  jnz .XGreaterEqualThanQ ; If so, then Q2<=X
; Otherwise, we manually compare the rest of the blocks with Q

; We know that we have to "virtually" shift Q by 2^(64*a+b) places to the left.
; We will take two blocks from X and mend them together in a way that allows
; the new block to be compared directly with a block from Q.
  lea r10, [r8 - 1]            ; r10 = N - 1
.compareBlocks:                  ; for(int i = N - 1; i >= 0; i --)
  test r10, r10                ; r10 < 0 ?
  jl .XGreaterEqualThanQ
  lea r11, [r10 + r12]         ; r11 = i + a
  mov r14, [rsi + r11*8]       ; r14 = X[i+a]
  mov r15, [rsi + r11*8 + 8]   ; r15 = X[i+a+1]

  ; Combination of r14s 64-b most significant bits
  ; and r15s b least significant bits.
  ; Note that this also works for b=0,
  ; which otherwise can be problematic.
  shrd r14, r15, cl

  cmp r14, [rdi + r10*8] ; r14 > Q[i] ?
  ja .XGreaterEqualThanQ
  jb .XLessThanQ
  dec r10
  jmp .compareBlocks
.XGreaterEqualThanQ:
  xor r10, r10 ; i = 0
  clc
.subtractQ: ; for(int i = 0; i < N; i ++)
; The approach for actually subtracting Q from X is different.
; We know that each block from Q impacts (at most) two other blocks from X.
; We split that block so it corresponds to the two blocks from X and subtract.
  mov rax, 0
  setc al                 ; al = CF from previous iteration of loop
  cmp r10, r8
  jge .whileCLCNZ2
  mov r14, [rdi + r10*8]  ; r14 = Q[i]
  mov r15, r14            ; r15 = Q[i]
  mov r13, rcx            ; (preserve rcx)
  shl r14, cl             ; r14 = lowest b bits of Q[i]
  xor rcx, 63
  inc rcx                 ; lowest 6 bits of rcx = 64 - b
  shr r15, cl             ; r15 = highest 64 - b bits of Q[i]
  and rcx, 63             ; Cut down rcx to lowest 6 bits.
  test cl, cl             ; ? special case: b = 0
; When b = 0 we expect r15 to be pushed by 64 bits,
; but with the way shr is coded, when b = 0,
; then 64 - b is equivalent to zero mod 64
; and shr instead does nothing.
; We use a conditional move to force desired behaviour.
  cmovz r15, rcx          ; If b=0, then r15 is set to 0.
  lea r11, [r10 + r12]    ; r11 = i + a
  test al, al             ; Is there a carry from previous loop iteration?
  jnz .case2
.case1: ; al = 0, no carry
  sub [rsi + r11*8], r14     ; X[i + a] -= r14
  sbb [rsi + r11*8 + 8], r15 ; X[i + a + 1] -= r15 + CF
  jmp .caseworkFinished
.case2: ; al = 1, carry
; We know that the bit indicated in al
; has overflown during the operation: sbb [rsi + r11*8 + 8], r15
; so we want to propagate it to X[i + a + 1], not X[i + a].
; Moreover, we know that sub [rsi + r11 * 8], r14
; will not overflow in this case.
; Thus we can manually set the CF to 1.
  sub [rsi + r11 * 8], r14   ; X[i + a] -= r14
  stc                        ; CF = 1
  sbb [rsi + r11*8 + 8], r15 ; X[i + a + 1] -= r15 + 1 (from previous loop)
.caseworkFinished:
  mov rcx, r13 ; (restore rcx)
  inc r10 ; i ++
  jmp .subtractQ
.whileCLCNZ2: ; Propagate the last bit upwards.
  jnc .add2Power
  inc r10                    ; i ++
  lea r11, [r10 + r12]       ; r11 = i + a
  sbb qword [rsi + r11*8], 0 ; X[i + a] -= CF
  jmp .whileCLCNZ2
.add2Power:
; We subtracted 4^(n-j) and Q*2^(n-j+1) from X.
; Now we need to add 2^(n-j) to Q, so that the recursive property
; of the algorithm is upheld.
  mov r12, rdx           ; r12 = n
  sub r12, r9            ; r12 = n - j
  mov rcx, r12           ; rcx = n - j
  shr r12, 6             ; r12 = a = (n - j) / 64
  and rcx, 63            ; rcx = (n - j) % 64
  bts [rdi + r12*8], rcx ; Q[a] += 2^b  <=>  Q += 2^(n-j)
  jmp .maindone
.XLessThanQ:
; In this case, we already subtracted 4^(n-j) from X,
; but concluded that Q*2^(n-j+1) >= X-4^(n-j).
; This means that q = 0.
; We have to restore X to its previous value before this loop's iteration.
  mov r12, rdx ; r12 = n
  sub r12, r9  ; r12 = n - j
  shl r12, 1   ; r12 = 2(n - j)
  mov rcx, r12 ; rcx = 2(n - j)
  shr r12, 6   ; r12 = a =  2(n - j) / 64
  mov rax, 1   ; rax = 1
  shl rax, cl  ; rax = 1 << b = 1 << (2(n - j) % 64)
  add [rsi + r12*8], rax ; X[a] -= (1 << b)  <=>  X -= 4^(n-j)
  mov r10, r12
.whileCLCNZ3: ; Propagate the last bit upwards.
  jnc .maindone
  inc r10                    ; i ++
  adc qword [rsi + r10*8], 0 ; X[i] += CF
  jmp .whileCLCNZ3
.maindone:
  inc r9 ; j ++
  jmp .mainloop

.end: ; Restore registers.
  pop     r15
  pop     r14
  pop     r13
  pop     r12
  ret
