# Square root

## Problem statement

For a given non-negative \(2n\)-bit integer \(X\) we want to find a non-negative \(n\)-bit integer \(Q\) such that

\[
Q^2 \le X < (Q+1)^2.
\]

## Task

Implement in assembly (called from C) a function with the following declaration:

```c
void nsqrt(uint64_t *Q, uint64_t *X, unsigned n);
````

Parameters `Q` and `X` are pointers to the binary representations of `Q` and `X`, respectively. Numbers are stored in standard unsigned binary, in **little-endian** word order (least-significant 64-bit word first), 64 bits per `uint64_t` word. The parameter `n` is the number of result bits; it is a multiple of 64 and lies in the range `64` to `256000`.

The memory pointed to by `X` is writable and may be used as working memory.

## Suggested algorithm

Compute the result iteratively. Let

$$
Q_j = \sum_{i=1}^{j} q_i\,2^{\,n-i},\qquad q_i\in\{0,1\},
$$

be the partial result after $j$ iterations, and let $R_j$ be the remainder after $j$ iterations. Initialize $Q_0 = 0$ and $R_0 = X$. In iteration $j$ we determine the bit $q_j$ of the result. Define

$$
T_{j-1} = 2^{\,n-j+1}Q_{j-1} + 4^{\,n-j},\qquad j=1,2,\dots,n.
$$

If $R_{j-1} \ge T_{j-1}$, then set $q_j = 1$ and $R_j = R_{j-1} - T_{j-1}$; otherwise set $q_j = 0$ and $R_j = R_{j-1}$. Equivalently,

$$
R_j = R_{j-1} - q_j\bigl(2^{\,n-j+1}Q_{j-1} + 4^{\,n-j}\bigr).
$$

After $n$ iterations we obtain $R_n = X - Q_n^2$. (As an exercise you may prove that $0 \le R_n \le 2Q_n$.)

## Submission

Submit a file named `nsqrt.asm` on Moodle as your solution.

## Compilation

The solution will be assembled with:

```bash
nasm -f elf64 -w+all -w+error -o nsqrt.o nsqrt.asm
```

The solution must assemble and run in the laboratory environment.

## Usage examples

Example usage files `nsqrt_example.c` and `nsqrt_example.cpp` are provided. They are compiled and linked with your solution using the following commands:

```bash
gcc -c -Wall -Wextra -std=c17 -O2 -o nsqrt_example_64.o nsqrt_example.c
gcc -z noexecstack -o nsqrt_example_64 nsqrt_example_64.o nsqrt.o

g++ -c -Wall -Wextra -std=c++20 -O2 -o nsqrt_example_cpp.o nsqrt_example.cpp
g++ -z noexecstack -o nsqrt_example_cpp nsqrt_example_cpp.o nsqrt.o -lgmp
```
