# File reversal (`freverse`)

## Problem

Implement in assembly a program `freverse` that reverses the contents of a file. The program is invoked as:

```

./freverse file

````

where `file` is the name of the file to reverse. If the file is shorter than two bytes, reversing it does not change its contents.

The program must not embed any artificial limit on file size — in particular it must work on files larger than 4 GiB. The implementation should be efficient.

The program must use Linux system calls only: `sys_read`, `sys_write`, `sys_open`, `sys_close`, `sys_stat`, `sys_fstat`, `sys_lseek`, `sys_mmap`, `sys_munmap`, `sys_msync`, `sys_exit`. It may use some subset of these (it does not need to use them all), but it must **not** use other system calls.

The program must validate its invocation and the return values of system calls (except `sys_exit`). If no parameter is given, more than one parameter is given, the given parameter is invalid, or a system call fails, the program must exit with code `1`. In every case the program must explicitly call `sys_close` for any file it opened before exiting.

The program must not print anything to the terminal.

---

## Submission

Submit a single file named `freverse.asm` on Moodle.

---

## Compilation

The solution will be assembled and linked with:

```bash
nasm -f elf64 -w+all -w+error -w-unknown-warning -w-reloc-rel -o freverse.o freverse.asm
ld --fatal-warnings -o freverse freverse.o
````

The program must assemble and run on the laboratory machines.

---

## Notes / requirements summary

* Invocation: exactly one command-line argument — the path to the file to reverse. Any other invocation is an error.
* If the file size < 2 bytes, do nothing (but still treat as success unless a syscall error occurs).
* Must handle very large files (no 32-bit size limits).
* Use only the listed Linux syscalls; do not call other syscalls.
* Must check system call return values and handle errors: on any error (invalid args, failed syscall) exit with status `1`.
* Must always explicitly `sys_close` any opened file descriptor before exiting (even on error).
* Must not write any output to stdout/stderr.
* Should be efficient (e.g., consider `mmap` and block-wise swapping for large files).

