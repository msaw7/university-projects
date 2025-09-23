# Queues

**Note:** From this task on, programs must be written in **C++ (standard C++17)**. In practice, solving this problem requires only a set of C instructions and `vector` from C++ (but you may use more).

Your job is to efficiently simulate events occurring during a working day of an office. The office has `m` service windows (counters), numbered from `0` to `m-1`. Each window has a queue of customers (`interesant`s). When a clerk is free they serve the first customer in that window’s queue; after being served the customer leaves the office and does not return that day. In addition, various other events happen in the office, described by the functions below. Your task is to implement these functions.

---

## Initialization

```cpp
void otwarcie_urzedu(int m)
```

This function is called exactly once, and it is the first call. It informs your library that the office has `m` windows (and one special window — described later). Initially there are no customers in the office.

---

## Customer creation and operations

```cpp
interesant *nowy_interesant(int k)
```

A new customer enters the office and immediately joins the end of the queue at window number `k`. The function should create a new element of type `interesant` and return a pointer to that element.

```cpp
int numerek(interesant* i)
```

Returns the ticket number of customer `i`. Each customer receives a ticket number immediately upon entering the office. Ticket numbers start at `0` and are consecutive integers.

```cpp
interesant *obsluz(int k)
```

The clerk serving window `k` tries to serve the next customer. If the queue at window `k` is non-empty, the first customer in the queue is served and leaves the office; the function should return a pointer to that customer. Otherwise no one is served and the function should return `NULL`.

```cpp
void zmiana_okienka(interesant *i, int k)
```

Customer `i` realizes they are standing in the wrong queue. They leave their current queue and join the end of the queue at window `k`. You may assume that immediately before this function is called, customer `i` was standing in some queue and it was **not** the queue for window `k`.

```cpp
void zamkniecie_okienka(int k1, int k2)
```

The clerk at window `k1` goes on a break. All customers who were standing in the queue for `k1` are redirected to window `k2` and join the end of the queue at `k2` **in the same order** they had in the queue for `k1`. We do not know how long the break will last; it can happen that additional customers join the queue for `k1` later that day and those may still get served at `k1`.

```cpp
std::vector<interesant*> fast_track(interesant *i1, interesant *i2)
```

A clerk briefly opens a special fast window where a group of customers can be processed immediately. A contiguous group of customers who were standing consecutively in one queue realize what is happening and immediately move to the special window, where they are served right away in the same order they were standing, then leave the office and the special window closes. The group is specified by the first (`i1`) and last (`i2`) customer in the group; if `i1 == i2`, only customer `i1` is served. The function should return a `vector` of pointers to the `interesant`s served at the special window, in the order they were served.

```cpp
void naczelnik(int k)
```

From time to time the head of the office looks out of their office and, bored, rearranges people standing in a queue. Specifically, they choose one window numbered `k` and order all customers in that window’s queue to **reverse** their order. Customers obey immediately. If there were at most one customer in the queue, this operation has no effect.

```cpp
std::vector<interesant *> zamkniecie_urzedu()
```

This function is called once, at the end of interaction with the library. It marks the end of the working day. All remaining customers are quickly served and the office closes. The function should return a `vector` of all customers who are still in the office at that moment, in the following order: first all those standing in queue for window `0` (in service order), then those in queue for window `1`, and so on up to window `m-1`.

---

## Memory management

Your library does **not** free memory for any `interesant`. That responsibility belongs to the user of the library. The user frees memory for a customer only when the customer leaves the office. **Important:** the user will free customers using the `free` function, therefore your library **must** allocate memory with `malloc`.

The declarations of the functions above are in the file `kol.h`. Your task is to fill in the definition of `struct interesant` in `kol.h` (without changing anything else in that file) and to implement the functions in `kol.cpp`.

---

## Compilation

Use the following compilation command:

```bash
g++ @opcjeCpp main.cpp kol.cpp -o main.e
```

Differences in `opcjeCpp` vs. the C `opcje` file are: options `-Wvla` (which treated VLAs as an error), `-Wjump-misses-init` (C-only), and `-std=c17` were removed; instead the option `-std=c++17` was added.

---

## Complexity requirements

To receive full points, the time cost of each function must be proportional to the size of its parameters and its output. Exceptions: `otwarcie_urzedu` and `zamkniecie_urzedu` may also use `O(m)` time.

---

## Valgrind / memory-leak note

Your solution will also be run under `valgrind` (which can detect memory leaks). We assume memory of customers who left the office will be freed by the user. If `valgrind --tool=memcheck --leak-check=yes ./main.e` detects leaks, you may lose 1–2 points.
