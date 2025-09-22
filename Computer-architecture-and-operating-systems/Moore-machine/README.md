# Moore machines

## Task

Implement in C a dynamically loadable library that simulates Moore machines.

A Moore machine is a kind of deterministic finite automaton used in synchronous digital circuits. A Moore machine is represented as an ordered 6-tuple ⟨X, Y, Q, t, y, q⟩ where:

- `X` is the set of input signal values,
- `Y` is the set of output signal values,
- `Q` is the set of internal states,
- `t : X × Q → Q` is the transition function,
- `y : Q → Y` is the output function,
- `q ∈ Q` is the initial state.

We consider only binary machines: there are `n` one-bit input signals, `m` one-bit output signals, and the state has `s` bits. Formally: `X = {0,1}^n`, `Y = {0,1}^m`, `Q = {0,1}^s`.

At each step the transition function `t` computes a new state based on the input signals and the current state. The output function `y` computes the output signals based on the state.

---

## Library interface

The library interface is given in the provided header file `ma.h`. Additional details of the library behaviour must be inferred from the supplied example `ma_example.c`, which is an integral part of the specification.

Bit sequences and signal values are stored in arrays of `uint64_t`. Each array element stores 64 consecutive bits, starting from the least significant bit position. If the sequence length is not a multiple of 64, the more significant bits of the last element are unused.

The structural type `moore_t` represents a machine. You must define (implement) this type in your solution:

```c
typedef struct moore moore_t;
````

The machine state and signals are bit sequences.

`transition_function_t` represents the transition function. It computes the next state from the input signals and the current state:

```c
typedef void (*transition_function_t)(
    uint64_t *next_state,
    uint64_t const *input,
    uint64_t const *state,
    size_t n,
    size_t s
);
```

Parameters:

* `next_state` – pointer to bit sequence where the next state must be written;
* `input` – pointer to the bit sequence containing input signal values;
* `state` – pointer to the bit sequence containing the current state;
* `n` – number of input signals (bits);
* `s` – number of bits in the internal state.

`output_function_t` represents the output function. It computes the output signals from the state:

```c
typedef void (*output_function_t)(
    uint64_t *output,
    uint64_t const *state,
    size_t m,
    size_t s
);
```

Parameters:

* `output` – pointer to bit sequence where the output signals must be written;
* `state` – pointer to bit sequence containing the state;
* `m` – number of output signals (bits);
* `s` – number of bits in the internal state.

---

## Functions to implement

### `ma_create_full`

Create a new machine:

```c
moore_t * ma_create_full(
    size_t n,
    size_t m,
    size_t s,
    transition_function_t t,
    output_function_t y,
    uint64_t const *q
);
```

Parameters:

* `n` – number of input signals (bits);
* `m` – number of output signals (bits);
* `s` – number of bits of internal state;
* `t` – transition function;
* `y` – output function;
* `q` – pointer to bit sequence representing the initial state.

Return value:

* pointer to the `moore_t` structure on success;
* `NULL` if `m == 0` or `s == 0` or any of the pointers `t`, `y`, `q` is `NULL`, or if memory allocation fails. The function must set `errno` appropriately to `EINVAL` (invalid argument) or `ENOMEM` (allocation failure).

---

### `ma_create_simple`

Create a new machine where the number of outputs equals the number of state bits, the output function is the identity, and the initial state is zero. Unused bits of the state must be initialized to zero.

```c
moore_t * ma_create_simple(size_t n, size_t s, transition_function_t t);
```

Parameters:

* `n` – number of input signals (bits);
* `s` – number of bits of internal state (and also the number of output signals);
* `t` – transition function.

Return value:

* pointer to the `moore_t` structure on success;
* `NULL` if `s == 0` or `t == NULL` or memory allocation fails. The function must set `errno` to `EINVAL` or `ENOMEM` accordingly.

---

### `ma_delete`

Delete the machine and free all memory it uses. Does nothing if called with `NULL`. After this call the pointer becomes invalid.

```c
void ma_delete(moore_t *a);
```

Parameter:

* `a` – pointer to the machine.

---

### `ma_connect`

Connect consecutive `num` input signals of machine `a_in` (starting at input `in`) to consecutive output signals of machine `a_out` (starting at output `out`). If inputs were previously connected, reconnect them to the new outputs.

```c
int ma_connect(
    moore_t *a_in,
    size_t in,
    moore_t *a_out,
    size_t out,
    size_t num
);
```

Parameters:

* `a_in` – pointer to the destination machine (whose inputs are connected);
* `in` – first input index in `a_in`;
* `a_out` – pointer to the source machine (whose outputs are used);
* `out` – first output index in `a_out`;
* `num` – number of signals to connect.

Return value:

* `0` on success;
* `-1` on error (if any pointer is `NULL`, `num == 0`, the specified input/output ranges are invalid, or memory allocation fails). The function must set `errno` to `EINVAL` or `ENOMEM` appropriately.

---

### `ma_disconnect`

Disconnect consecutive `num` input signals of machine `a_in`, starting at input `in`. If an input was not connected, it remains unconnected.

```c
int ma_disconnect(moore_t *a_in, size_t in, size_t num);
```

Parameters:

* `a_in` – pointer to the machine;
* `in` – first input index to disconnect;
* `num` – number of signals to disconnect.

Return value:

* `0` on success;
* `-1` on error (if `a_in == NULL`, `num == 0`, or the input range is invalid). The function must set `errno = EINVAL`.

---

### `ma_set_input`

Set values of signals on inputs that are not connected. Bits in the supplied `input` array that correspond to connected inputs must be ignored.

```c
int ma_set_input(moore_t *a, uint64_t const *input);
```

Parameters:

* `a` – pointer to the machine;
* `input` – pointer to a bit sequence of length `n` (number of inputs).

Return value:

* `0` on success;
* `-1` on error (if the machine has no inputs or any pointer is `NULL`). The function must set `errno = EINVAL`.

---

### `ma_set_state`

Set the state of the machine.

```c
int ma_set_state(moore_t *a, uint64_t const *state);
```

Parameters:

* `a` – pointer to the machine;
* `state` – pointer to bit sequence with the new state.

Return value:

* `0` on success;
* `-1` on error (if any pointer is `NULL`). The function must set `errno = EINVAL`.

---

### `ma_get_output`

Return pointer to the bit sequence that contains the machine’s output signals. The pointer must remain valid until `ma_delete` is called on that machine.

```c
uint64_t const * ma_get_output(moore_t const *a);
```

Parameter:

* `a` – pointer to the machine.

Return value:

* pointer to the output bit sequence on success;
* `NULL` if `a == NULL` (and set `errno = EINVAL`).

---

### `ma_step`

Perform one computation step for the given machines. All machines operate in parallel and synchronously: the states and outputs after `ma_step` must depend only on the states, inputs and outputs before the call.

```c
int ma_step(moore_t *at[], size_t num);
```

Parameters:

* `at` – array of pointers to machines;
* `num` – number of machines in the array.

Return value:

* `0` on success;
* `-1` on error (if `at == NULL`, any pointer in the array is `NULL`, `num == 0`, or memory allocation fails). The function must set `errno` to `EINVAL` or `ENOMEM` as appropriate.

---

## Functional requirements

* An input’s value is set either via `ma_set_input` or by connecting that input to an output of another machine. Until an input is set it is *undefined*. After disconnecting an input its state becomes undefined again. A signal in undefined state means its value is unknown.
* When deleting or connecting machines ensure proper disconnection so that no dangling connections remain.
* The library must provide a weak guarantee with respect to allocation failures: it must not leak memory and all data structures must remain in a consistent state after an allocation failure.

---

## Formal requirements (submission and build)

Provide an archive uploaded to Moodle that contains:

* `ma.c` (the library implementation),
* optionally other `.c` and `.h` files used by the implementation,
* a `Makefile` or `makefile`.

The archive must not include any other files or subdirectories and must not contain binary files. The archive must be compressed using `zip`, `7z`, or `rar`, or as a `tar` + `gzip` pair. After unpacking all files must be placed in a common directory.

The provided `Makefile` must include a target `libma.so` so that `make libma.so` builds the library and produces `libma.so` in the current directory. This target must also compile and link the provided file `memory_tests.c` into the library. You must describe file dependencies and ensure that only changed files (and files depending on them) are rebuilt. `make clean` must remove all files created by `make`. The `Makefile` must include the phony target `.PHONY`. You may add other targets, for example to build an example using `ma_example.c` or to run tests.

Use `gcc` for compilation. The library must compile under Linux in the laboratory environment. Compile the implementation files with the options:

```
-Wall -Wextra -Wno-implicit-fallthrough -std=gnu17 -fPIC -O2
```

Link the library with the options:

```
-shared -Wl,--wrap=malloc -Wl,--wrap=calloc -Wl,--wrap=realloc \
-Wl,--wrap=reallocarray -Wl,--wrap=free -Wl,--wrap=strdup -Wl,--wrap=strndup
```

The `-Wl,--wrap=` options cause calls to `malloc`, `calloc`, etc. to be redirected to `__wrap_malloc`, `__wrap_calloc`, etc. The wrapper functions are implemented in the supplied `memory_tests.c`.

The implementation must not leak memory nor leave data structures in an inconsistent state even when allocation fails. Correctness will be checked with `valgrind`.

The implementation must not impose artificial limits on the sizes of stored data — the only limits are available memory and the machine word size.

---

