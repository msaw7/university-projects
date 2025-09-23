#ifndef _ZBIOR_ARY_H_
#define _ZBIOR_ARY_H_

#include <stdbool.h>

typedef struct zbior_ary {
  int remNum; // The number of different remainders mod Q that appear in our sequence.
  int *remainders; // List of remainders mentioned above.
  int *remainderSize; // size of the left and right arrays for the remainder at a specific index
  int **left; // for each remainder: array with the left endpoints of the intervals of the arithmetic sequence
  int **right; // for each remainder: array with the right endpoints of the intervals of the arithmetic sequence
} zbior_ary;

/* It is best not to modify anything below. */

// The function returns a set representing an arithmetic sequence with starting element a, ending element b, and difference q>0, i.e. {a, a+q, ..., b}.
// You may assume that in all queries in a given test the value of q will be the same.
// You may assume that this function will be called first.
zbior_ary ciag_arytmetyczny(int a, int q, int b);

// Returns a set consisting of only the element a, i.e. {a}.
zbior_ary singleton(int a);

// Returns a set representing the set-theoretic sum (union) of sets A and B.
zbior_ary suma(zbior_ary A, zbior_ary B);

// Returns a set representing the intersection of sets A and B.
zbior_ary iloczyn(zbior_ary A, zbior_ary B);

// Returns the set A \ B.
zbior_ary roznica(zbior_ary A, zbior_ary B);

// Returns true iff the number b belongs to set A.
bool nalezy(zbior_ary A, int b);

// The result of the function is the number of elements in set A.
unsigned moc(zbior_ary A);

// The result of the function is Ary_q(A), i.e. the minimal number of pairwise disjoint arithmetic sequences with difference q whose union is the set A.
unsigned ary(zbior_ary A);

#endif