# Origami layers

---

### Note

* Compilation options as usual. `-lm` is allowed.

---

### Task

Write a program for origami fans to determine how many layers are present at a given point of a cleverly folded sheet of paper. The program should read from `stdin` a description of the construction of successive `parchment` sheets and queries about the number of layers at given points of given `parchment` sheets. The first line of input contains two integers `n` and `q` — the number of `parchment` sheets and the number of queries. Then follow `n` lines describing the successive `parchment` sheets and `q` lines with queries about the number of layers of the given `parchment` at given points.

The description of the i-th `parchment` (1 ≤ i ≤ n) is on line i+1. Each description has one of three forms:

```
P x1 y1 x2 y2
K x y r
Z k x1 y1 x2 y2
```

* `P x1 y1 x2 y2` represents a closed rectangle with sides parallel to the coordinate axes, with the lower-left corner at `P1 = (x1, y1)` and the upper-right corner at `P2 = (x2, y2)`. Thus `P1` must be weakly left and down from `P2`. If a pin is pushed into this `parchment` inside (or on the edges of) the rectangle, the sheet is pierced once (1), otherwise zero (0).

* `K x y r` represents the closed disk with center at `(x, y)` and radius `r`.

* `Z k x1 y1 x2 y2` represents the `parchment` obtained by folding the k-th `parchment` (1 ≤ k < i) along the line through points `P1 = (x1, y1)` and `P2 = (x2, y2)` (the two points must be distinct). The paper is folded so that the right side of the line (when looking from `P1` to `P2`) is folded over to the left. For such a folded `parchment`:

  * a pin on the **right** side of the line yields **0** layers;
  * a pin **exactly on** the line yields the same result as piercing the `parchment` **before** folding;
  * a pin on the **left** side yields the number of layers equal to the number of layers before folding at that point **plus** the number of layers of the unfolded sheet at the point that is folded onto the pierced point.

---

### Queries

The j-th query (1 ≤ j ≤ q) is on line `n + j + 1` and has the form:

```
k x y
```

where `k` is the index of the `parchment` (1 ≤ k ≤ n) and `(x, y)` is the point where we push a pin to determine the number of layers of the k-th `parchment` at that point.

---

### Output

Print `q` lines to `stdout` — the answers to the queries: the number of layers at the specified point of the specified `parchment`.

---

### Notes

* Coordinates and radii (`x`, `y`, `x1`, `y1`, `x2`, `y2`, `r`) are real (floating-point) numbers.
* There is **no restriction** on the allowed time complexity of the solution; exponential solutions are permitted. The judges' tests are not adversarial with respect to floating-point precision.

---

### Example

**Input**

```
4 5
P 0 0 2.5 1
Z 1 0.5 0 0.5 1
K 0 1 5
Z 3 0 1 -1 0
1 1.5 0.5
2 1.5 0.5
2 0 0.5
3 4 4
4 4 4
```

**Output**

```
1
0
2
1
2
```
