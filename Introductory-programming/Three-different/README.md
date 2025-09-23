# Three Motels

---

### Problem statement

Along a highway, which we can imagine as a straight line, there are $n$ motels. The motels are numbered $1$ to $n$ in order along the highway. Each motel belongs to some motel network, described by an integer in the range $1$ to $n$.

Bajtek told Bitek that during a trip on the highway he stopped for the night three times, and each time he stayed at a motel belonging to a different network. Bitek wonders how far apart those three motels must have been. He is interested in the *nearest* and *farthest* triple of motels.

Formally, Bitek wants to choose three motels $A,B,C$ located in this order along the highway (increasing positions) and belonging to three different networks, such that:

* **nearest triple:** the value $\max(\text{dist}(A,B),\ \text{dist}(B,C))$ is as small as possible,
* **farthest triple:** the value $\min(\text{dist}(A,B),\ \text{dist}(B,C))$ is as large as possible.

Write a program that reads from `stdin` the number $n$ of motels and then $n$ descriptions of motels in order along the highway — for each motel: its network id and its distance from the start of the highway — and writes to `stdout two integers` — the results described above: the minimum possible maximum distance (for the nearest triple) and the maximum possible minimum distance (for the farthest triple). If Bajtek was mistaken and no triple of motels from three different networks occurs along the highway, both numbers should be zero. Exact input/output formatting is shown in the example below (line endings at the ends of lines).

You may assume $1 \le n \le 10^6$. All numbers on input are nonnegative and fit in type `int`. Several motels may lie at the same point on the highway, and it is possible that Bajtek stayed in two or three motels at the same position.

A solution that produces only one of the two required numbers correctly may receive half of the points.

---

### Compilation

```
gcc @opcje thr.c -o thr.e
```

---

### Input

First line: integer $n$.
Next $n$ lines: two integers per line — `network_id distance` — in the order of motels along the highway.

---

### Output

Two integers:

* first — the minimum possible value of $\max(\text{dist}(A,B),\ \text{dist}(B,C))$ over all triples of motels $A<B<C$ from three different networks,
* second — the maximum possible value of $\min(\text{dist}(A,B),\ \text{dist}(B,C))$ over all such triples.

If no valid triple exists, output `0 0`.

---

### Example

**Input**

```
9
1 2
2 6
2 9
1 13
1 17
3 20
1 26
3 27
1 30
```

**Output**

```
7 10
```

**Explanation**

* Nearest triple: motels 3, 4 and 6 (positions 9, 13 and 20; networks 2, 1 and 3). For these three $\max(\,13-9=4,\ 20-13=7\,)=7$ and this value is minimal possible.
* Farthest triple: motels 2, 6 and 9 (positions 6, 20 and 30; networks 2, 3 and 1). For these three $\min(\,20-6=14,\ 30-20=10\,)=10$ and this value is maximal possible.
