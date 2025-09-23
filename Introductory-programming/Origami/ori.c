// Author: Mateusz Sawicki

/*
Usage:
Compile - gcc ori.c -o ori
Run on example - ./ori < example.in
*/

#include <stdio.h>
#include <stdlib.h>
#include <assert.h>

const long double eps = 1e-6;

typedef struct point{
    long double x, y;
} point;

point createPoint(long double x, long double y) {
    point ans;
    ans.x = x;
    ans.y = y;
    return ans;
}

long double pointDistance(point a, point b) {
    long double x = a.x - b.x, y = a.y - b.y;
    return x * x + y * y;

}

point add(point a, point b) {
    return createPoint(a.x + b.x, a.y + b.y);
}

point subtract(point a, point b) {
    return createPoint(a.x - b.x, a.y - b.y);
}

long double dotProduct(point A, point B) { // returns the dot product
    return A.x * B.x + A.y * B.y;
}

point multiply(point a, long double t) { // multiplies a point/vector by a scalar
    return createPoint(a.x * t, a.y * t);

}

point vectorProjection(point A, point B) { // takes two vectors and returns the projection of A onto B
    return multiply(B, dotProduct(A, B) / dotProduct(B, B));
}

point lineSymmetry(point a, point b, point c) { // takes three points a, b, c and returns the reflection of c with respect to segment ab
    // project ac onto ab
    point B = subtract(b, a);
    point C = subtract(c, a);
    point vpC = vectorProjection(C, B);
    // convert vpC into the perpendicular projection of c onto ab
    vpC = add(vpC, a);
    return subtract(multiply(vpC, 2), c); // return the reflection by calculating it from the projection of c onto ab
}

int pointOrientation(point a, point b, point c) { // checks where c lies relative to the ray ab
    long double val = (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
    if(val > eps) return 0; // point c is to the left
    if(val < -eps) return 1; // point c is to the right
    return 2; // point c is on the ray
}

typedef struct sheet {
    int shape; // 0 = rectangle, 1 = circle

    // data of our base sheet
    point center;
    long double radius;
    point p1, p2; // bottom-left and top-right corners of the rectangle

    point fold1, fold2; // ray representing the fold
    int previousFoldId; // this is a "pointer" to the previous state of the sheet

} sheet;

/*
By going recursively backwards over the folds, we generate all points after unfolding the sheet.
In the end, we check how many of them lie on our initial sheet.
The recursion will have depth at most O(n).
*/
int query(point a, int sheetId, sheet *arr) {
    sheet curr = arr[sheetId];
    if(curr.previousFoldId == -1) { // this means we are on the initial sheet
        if(curr.shape == 0) {
            if(a.x + eps < curr.p1.x) return 0;
            if(a.x - eps > curr.p2.x) return 0;
            if(a.y + eps < curr.p1.y) return 0;
            if(a.y - eps > curr.p2.y) return 0;
            return 1;
        }
        if(curr.shape == 1) {
            if(pointDistance(curr.center, a) < (curr.radius + eps) * (curr.radius + eps)) return 1;
            return 0;
        }
    }
    int ans = query(a, curr.previousFoldId, arr);
    if(pointOrientation(curr.fold1, curr.fold2, a) == 0) { // point a is on the left side of the fold
        return ans + query(lineSymmetry(curr.fold1, curr.fold2, a), curr.previousFoldId, arr);
    }
    if(pointOrientation(curr.fold1, curr.fold2, a) == 1) return 0;  // we know that everything on the right side of the ray moved to the left, so a on the right generates nothing
    if(pointOrientation(curr.fold1, curr.fold2, a) == 2) return ans; // point a lies on the fold
    return -999; // guard
}

int main() {
    int n, q;
    assert(scanf("%d %d", &n, &q) == 2);
    sheet *arr = (sheet *) malloc(sizeof(sheet) * (long unsigned int) n);
    for(int i = 0; i < n; i ++) {
        char c;
        assert(scanf(" %c", &c) == 1);
        if(c == 'P') {
            long double x1, y1, x2, y2;
            assert(scanf("%Lf %Lf %Lf %Lf", &x1, &y1, &x2, &y2) == 4);
            arr[i].shape = 0;
            arr[i].center = createPoint(0, 0);
            arr[i].radius = 0;
            arr[i].p1 = createPoint(x1, y1);
            arr[i].p2 = createPoint(x2, y2);
            arr[i].previousFoldId = -1;
        }
        if(c == 'K') {
            long double x, y, r;
            assert(scanf("%Lf %Lf %Lf", &x, &y, &r) == 3);
            arr[i].shape = 1;
            arr[i].center = createPoint(x, y);
            arr[i].radius = r;
            arr[i].p1 = createPoint(0, 0);
            arr[i].p2 = createPoint(0, 0);
            arr[i].previousFoldId = -1;
        }
        if(c == 'Z') {
            int k;
            long double x1, y1, x2, y2;
            assert(scanf("%d %Lf %Lf %Lf %Lf", &k, &x1, &y1, &x2, &y2) == 5);;
            k --;
            arr[i] = arr[k];
            arr[i].previousFoldId = k;
            arr[i].fold1 = createPoint(x1, y1);
            arr[i].fold2 = createPoint(x2, y2);

        }
    }
    for(int i = 0; i < q; i ++) {
        int k;
        long double x, y;
        assert(scanf("%d %Lf %Lf", &k, &x, &y) == 3);
        k --;
        point a = createPoint(x, y);
        printf("%d\n", query(a, k, arr));
    }
    return 0;
}