# Arithmetic Sets

---

### Notes

* **Note 1:** `-2 % 5 == -2`
* **Note 2:** The file **ocen.c** has been supplemented with example calls to the function `nalezy`.

---

### Task Overview

You need to implement in **C** the basic set-theoretical operations on sets of integers.
Sets in this task are constructed using specific **arithmetic sequences**.

Sets will be represented using the type:

```c
struct zbior_ary;
```

---

### Set Constructors

```c
zbior_ary ciag_arytmetyczny(int a, int q, int b);
```

* Creates a set that is a finite arithmetic sequence with difference `q > 0`, with a given starting element `a` and ending element `b`.
* Constraint: `a mod q == b mod q`.
* The difference `q` will be the same in all calls to this constructor in a given test.

```c
zbior_ary singleton(int a);
```

* Creates a set consisting of only the integer `a`.

---

### Set Operations

```c
zbior_ary suma(zbior_ary A, zbior_ary B);
```

* Returns the **union** of sets `A` and `B`.

```c
zbior_ary iloczyn(zbior_ary A, zbior_ary B);
```

* Returns the **intersection** of sets `A` and `B`.

```c
zbior_ary roznica(zbior_ary A, zbior_ary B);
```

* Returns the **difference** of sets `A` and `B`.

---

### Additional Functions

```c
bool nalezy(zbior_ary A, int x);
```

* Checks whether `x` belongs to set `A`.

```c
unsigned moc(zbior_ary A);
```

* Returns the number of elements in set `A`.

---

### The Aryq Concept

For a given set `A` and number `q`, **Aryq(A)** denotes the minimal number of pairwise disjoint arithmetic sequences with difference `q` whose union equals `A`.

**Example:**
`Ary5({1,2,4,7,9,12,19}) = 4`, since the set can be represented as a union of 4 sequences:

* `{1}`
* `{2,7,12,17}`
* `{4,9}`
* `{19}`

Implement:

```c
unsigned ary(zbior_ary A);
```

* Returns `Aryq(A)` for the given set `A` and the parameter `q` used in the test.

---

### Complexity Requirements

* `suma(A, B)`, `iloczyn(A, B)`, `roznica(A, B)` → **O(Aryq(A) + Aryq(B))**
* `nalezy(A, x)` → **O(log(Aryq(A)))**
* `moc(A)` and `ary(A)` → **O(Aryq(A))** (worst case)
* Memory usage for set `A` → **O(Aryq(A))**

Non-optimal (but correct) solutions may still receive partial credit.

---

### Additional Constraints

* Function arguments are of type `int`.
* The parameter `q` is positive and the same in all calls to `ciag_arytmetyczny` in a given test.
* `ciag_arytmetyczny` will be called first in each test, so your library can learn `q` immediately.

---

### Implementation Details

* The described functions are declared in **zbior\_ary.h**.
* Provide:

  * A representation of sets as `struct zbior_ary`.
  * Efficient implementations of the required operations.
* Place your solution in **zbior\_ary.c**, matching the interface in **zbior\_ary.h**.
* Include **zbior\_ary.h** with the definition of `struct zbior_ary`.

---

### Compilation

A few sample operations on sets can be found in **ocen.c**. To compile:

```bash
gcc @opcje ocen.c zbior_ary.c -o ary -lm
```

* The `-lm` option links **math.h** and **must be at the end** of the command.
* Using **math.h** is optional.

---

### Memory Management

You do **not** need to handle memory deallocation in this task.

---

### Files

* **ocen.c** — October 29, 2024, 12:32
* **opcje** — October 1, 2024, 18:38
* **zbior\_ary.h** — October 21, 2024, 22:25
