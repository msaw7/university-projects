# Previous in range

Your task is to implement a data structure that answers queries about a sequence of integers `X` efficiently. Additionally, elements of the sequence may be revealed *online* (appended over time).

For a fixed sequence $X = x_0, x_1, \dots, x_{n-1}$ we are interested in the function

```
prevInRange(i, [lo, hi])
```

which returns the largest index `j` such that `0 ≤ j ≤ i` and `x_j ∈ [lo, hi]`. If no such index exists, the result should be `-1`.

Equivalently:

```
prevInRange(i, [lo, hi]) = max{ 0 ≤ j ≤ i : x_j ∈ [lo, hi] } 
```

or `-1` if that set is empty.

---

## Required interface (functions you must provide)

Implement the following functions (declarations are in `prev.h`; implement them in `prev.cpp`):

```cpp
void init(const std::vector<int> &x);
// Initialize the initial sequence X with the values from vector x.
// Note: elements of x may be any int values.

int prevInRange(int i, int lo, int hi);
// Compute prevInRange(i, [lo, hi]).
// You may assume 0 ≤ i < |X| and INT_MIN ≤ lo ≤ hi ≤ INT_MAX.

void pushBack(int v);
// Append value v to the end of the current sequence X.

void done();
// Free all memory used to support the sequence X.
```

An example interaction is provided in the attached `main.cpp`.

Compilation command:

```
g++ @opcjeCpp main.cpp prev.cpp -o main.e
```

---

## Complexity requirements

To get full points:

* The amortized time cost of `prevInRange` and `pushBack` must be `O(log z)`.
* The time complexity of `init` should be `O(|X| log z)` in the worst case,
  where `z` denotes the range of the `int` type. (Losing one extra logarithmic factor can cost 1 point.)
* A brute-force solution receives 0 points.

---

## Memory / Valgrind note

Your solution will be run under `valgrind` to detect memory leaks. If `valgrind --tool=memcheck --leak-check=yes ./main.e` detects leaks, you may lose 1–2 points.

---

## Implementation hint / lab note

On the lab this time a data structure that can be used to solve the problem will be discussed. Your task will be to implement that structure. In your implementation you **may** use `shared_ptr`.
