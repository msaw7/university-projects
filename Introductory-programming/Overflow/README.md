# Overflow

You are given `n` glasses, numbered from `1` to `n`, with capacities `x₁, x₂, …, xₙ`. Initially all glasses are empty. You may perform the following actions:

* **fill** a chosen glass completely from the tap,
* **empty** a chosen glass completely to the sink,
* **pour** water from one glass to another — if all the water fits, you pour it all; otherwise you pour until the destination glass is full.

Your goal is to reach a configuration where the amount of water in each glass equals the specified quantities `y₁, y₂, …, yₙ`.

Write a program that, given the numbers `x₁, x₂, …, xₙ` and `y₁, y₂, …, yₙ`, determines the minimal number of actions required to obtain the target configuration. If it is impossible, the correct result is `-1`.

### Input format (read from standard input)

```
n
x1 y1
x2 y2
...
xn yn
```

You may assume `0 ≤ n` and `0 ≤ yi ≤ xi` for `i = 1, 2, …, n`. All numbers in the input are integers. The values `n` and the sums `x1 + x2 + … + xn` and `y1 + y2 + … + yn` fit in type `int`.
