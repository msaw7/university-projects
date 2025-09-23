// Author: Mateusz Sawicki

#include "zbior_ary.h"
#include <stdio.h>
#include <assert.h>
#include <malloc.h>
#include <stdlib.h>

int Q;

// Diagnostic function.
void ary_debug(zbior_ary C) {
    printf("%d\n", C.remNum);
    for(int i = 0; i < C.remNum; i ++) {
        printf("%d size: %d\n", C.remainders[i], C.remainderSize[i]);
        for(int j = 0; j < C.remainderSize[i]; j ++) {
            printf("%d ", C.left[i][j]);
            printf("%d  ", C.right[i][j]);
        }
        printf("\n");
    }
    printf("\n");
    return;
}

// Returns x % q with proper behaviour for negative numbers
int getRemainder(int x) {
    long long x2 = x;
    long long q2 = Q;
    return (int) ((x2 % q2 + q2) % q2);
}

// The function returns a set representing an arithmetic sequence with starting element a, ending element b, and difference q>0, i.e. {a, a+q, ..., b}.
// You may assume that in all queries in a given test the value of q will be the same.
// You may assume that this function will be called first.
zbior_ary ciag_arytmetyczny(int a, int q, int b) {
    Q = q;
    struct zbior_ary z;
    z.remNum = 1;

    z.remainders = (int *) malloc(sizeof(int) * (long unsigned int) z.remNum);
    z.remainders[0] = getRemainder(a);

    z.remainderSize = (int *) malloc(sizeof(int) * (long unsigned int) z.remNum);
    z.remainderSize[0] = 1;

    z.left = (int **) malloc(sizeof (int *) * 1);
    z.left[0] = (int *) malloc(sizeof(int) * 1);
    if(a >= 0) z.left[0][0] = a / q;
    else z.left[0][0] = (a + 1) / q - 1;

    z.right = (int **) malloc(sizeof (int *) * 1);
    z.right[0] = (int *) malloc(sizeof(int) * 1);
    if(b >= 0) z.right[0][0] = b / q;
    else z.right[0][0] = (b + 1) / q - 1;
    return z;

}

// Returns a singleton set with element a
zbior_ary singleton(int a) {
    return ciag_arytmetyczny(a, Q, a);
}

/*
Adds segments in A and B and writes it down in C.
Takes only A and B with non-empty columns indexed remIdA and remIdB respectively.
*/
void mergeColumns(zbior_ary *A, zbior_ary *B, zbior_ary *C, int remIdA, int remIdB, int remIdC) {
    int aPointer = 0, bPointer = 0, cPointer = 0;

    int asize = A->remainderSize[remIdA];
    int bsize = B->remainderSize[remIdB];
    int csize = asize + bsize;

    int *helperL = (int *) malloc(sizeof(int) * (long unsigned int) csize);
    int *helperR = (int *) malloc(sizeof(int) * (long unsigned int) csize);

    int lCurr = 1, rCurr = 0;

    while((aPointer < asize) || (bPointer < bsize)) {
        int caseNum = 0;
        if(aPointer == asize) caseNum = 1;
        else if(bPointer == bsize) caseNum = 2;
        else if((A->left[remIdA][aPointer]) <= (B->left[remIdB][bPointer])) caseNum = 2;
        else caseNum = 1;

        int l, r;
        if(caseNum == 1) {
            l = B->left[remIdB][bPointer];
            r = B->right[remIdB][bPointer];
            bPointer ++;
        }
        if(caseNum == 2) {
            l = A->left[remIdA][aPointer];
            r = A->right[remIdA][aPointer];
            aPointer ++;
        }
        if(lCurr > rCurr) { // check if the current segment is non-degenerate.
            lCurr = l;
            rCurr = r;
        }
        if((long long) rCurr + 1 < (long long) l) {
            if(lCurr <= rCurr) {
                helperL[cPointer] = lCurr;
                helperR[cPointer] = rCurr;
                cPointer ++;
            }
            lCurr = l;
            rCurr = r;
        }
        else if(r <= rCurr); // we do nothing because the entire segment is contained in lCurr rCurr
        else rCurr = r; // expand the segment
    }
    if(lCurr <= rCurr) {
        helperL[cPointer] = lCurr;
        helperR[cPointer] = rCurr;
        cPointer ++;
    }
    C->remainderSize[remIdC] = cPointer;
    C->left[remIdC] = (int *) malloc(sizeof(int) * (long unsigned int) cPointer);
    C->right[remIdC] = (int *) malloc(sizeof(int) * (long unsigned int) cPointer);
    for(int i = 0; i < cPointer; i ++) {
        C->left[remIdC][i] = helperL[i];
        C->right[remIdC][i] = helperR[i];
    }
    return;
}


// Returns a "set theory" sum of sets A and B.
zbior_ary suma(zbior_ary A, zbior_ary B) {
    struct zbior_ary C;
    int aPointer = 0, bPointer = 0, cPointer = 0;
    int *helper = (int *) malloc(sizeof(int) * (long unsigned int) (A.remNum + B.remNum));

    while((aPointer < A.remNum) || (bPointer < B.remNum)) { // first we need to determine the size of the remainders table.
        int caseNum = 0;
        if(aPointer == A.remNum) caseNum = 1;
        else if(bPointer == B.remNum) caseNum = 2;
        else if(A.remainders[aPointer] == B.remainders[bPointer]) caseNum = 3;
        else if(A.remainders[aPointer] < B.remainders[bPointer]) caseNum = 2;
        else if(A.remainders[aPointer] > B.remainders[bPointer]) caseNum = 1;
        if(caseNum == 1) {
            helper[cPointer] = B.remainders[bPointer];
            cPointer ++;
            bPointer ++;
        }
        if(caseNum == 2) {
            helper[cPointer] = A.remainders[aPointer];
            cPointer ++;
            aPointer ++;
        }
        if(caseNum == 3) {
            helper[cPointer] = A.remainders[aPointer];
            aPointer ++;
            bPointer ++;
            cPointer ++;
        }
    }

    C.remNum = cPointer;
    C.remainders = (int *) malloc(sizeof(int) * (long unsigned int) cPointer);
    C.remainderSize = (int *) malloc(sizeof(int) * (long unsigned int) cPointer);
    C.left = (int **) malloc(sizeof(int *) * (long unsigned int) cPointer);
    C.right = (int **) malloc(sizeof(int *) * (long unsigned int) cPointer);

    aPointer = 0; bPointer = 0; cPointer = 0;
    // Now we visit the remainders in A and B in the same order, this time using mergeColumns where necessary.
    while((aPointer < A.remNum) || (bPointer < B.remNum)) { 
        int caseNum = 0;
        if(aPointer == A.remNum) caseNum = 1;
        else if(bPointer == B.remNum) caseNum = 2;
        else if(A.remainders[aPointer] == B.remainders[bPointer]) caseNum = 3;
        else if(A.remainders[aPointer] < B.remainders[bPointer]) caseNum = 2;
        else if(A.remainders[aPointer] > B.remainders[bPointer]) caseNum = 1;

        if(caseNum == 1) { // this remainder does not occur in A, so we copy values from B into C
            C.remainders[cPointer] = B.remainders[bPointer];
            C.remainderSize[cPointer] = B.remainderSize[bPointer];
            C.left[cPointer] = (int *) malloc(sizeof(int) * (long unsigned int) B.remainderSize[bPointer]);
            C.right[cPointer] = (int *) malloc(sizeof(int) * (long unsigned int) B.remainderSize[bPointer]);
            for(int i = 0; i < C.remainderSize[cPointer]; i ++) {
                C.left[cPointer][i] = B.left[bPointer][i];
                C.right[cPointer][i] = B.right[bPointer][i];
            }
            bPointer ++;
        }

        if(caseNum == 2) { // this remainder does not occur in B, so we copy values from A into C
            C.remainders[cPointer] = A.remainders[aPointer];
            C.remainderSize[cPointer] = A.remainderSize[aPointer];
            C.left[cPointer] = (int *) malloc(sizeof(int) * (long unsigned int) A.remainderSize[aPointer]);
            C.right[cPointer] = (int *) malloc(sizeof(int) * (long unsigned int) A.remainderSize[aPointer]);
            for(int i = 0; i < C.remainderSize[cPointer]; i ++) {
                C.left[cPointer][i] = A.left[aPointer][i];
                C.right[cPointer][i] = A.right[aPointer][i];
            }
            aPointer ++;
        }

        if(caseNum == 3) { // remainder occurs in both A and B, we use mergeColumns
            C.remainders[cPointer] = A.remainders[aPointer];
            mergeColumns(&A, &B, &C, aPointer, bPointer, cPointer);
            aPointer ++;
            bPointer ++;
        }
        cPointer ++;
    }
    return C;
}

// Subtracts two columns and returns the length of the newly created one, takes A and B with non-empty columns remIdA and remIdB.
int subtractColumns(zbior_ary *A, zbior_ary *B, int *cL, int *cR, int remIdA, int remIdB) {
    int bPointer = 0, cPointer = 0;

    int asize = A->remainderSize[remIdA];
    int bsize = B->remainderSize[remIdB];

    for(int aPointer = 0; aPointer < asize; aPointer ++) {
        int lA = A->left[remIdA][aPointer];
        int rA = A->right[remIdA][aPointer];
        while(bPointer < bsize) {
            if(lA > rA) break;
            int lB = B->left[remIdB][bPointer];
            int rB = B->right[remIdB][bPointer];
            if(lB > rA) { // interval B is to the right of A
                if(lA <= rA) {
                    cL[cPointer] = lA;
                    cR[cPointer] = rA;
                    cPointer ++;
                }
                break;
            }
            if(rB < lA) { // interval B is to the left of A
                bPointer ++;
                continue;
            }
            if((lB <= lA) && (rB >= rA)) break; // interval from A is contained in interval from B
            if(lB < lA) { // prefix of the considered interval from A is contained in B
                lA = rB + 1;
                bPointer ++;
                continue;
            }
            if((long long) lA <= (long long) lB - 1) { // interval from B is contained in interval from A
                cL[cPointer] = lA;
                cR[cPointer] = lB - 1;
                cPointer ++;
            }
            lA = rB + 1; // shorten our current interval from A accordingly
        }
        if(bPointer == bsize) { // we have run out of reasonable intervals from B to analyze
            if(lA <= rA) {
                cL[cPointer] = lA;
                cR[cPointer] = rA;
                cPointer ++;
            }
            continue;
        }
    }
    return cPointer;
}

// Returns the set A \ B.
zbior_ary roznica(zbior_ary A, zbior_ary B) {
    struct zbior_ary C;
    int aPointer = 0, bPointer = 0, cPointer = 0;
    int **helperL = (int **) malloc(sizeof(int *) * (long unsigned int)(A.remNum + B.remNum));
    int **helperR = (int **) malloc(sizeof(int *) * (long unsigned int)(A.remNum + B.remNum));
    int *helperRem = (int *) malloc(sizeof(int) * (long unsigned int) (A.remNum + B.remNum));
    int *arSiz = (int *) malloc(sizeof(int) * (long unsigned int) (A.remNum + B.remNum));

    while((aPointer < A.remNum) || (bPointer < B.remNum)) {
        int caseNum = 0;
        if(aPointer == A.remNum) break;
        else if(bPointer == B.remNum) caseNum = 2;
        else if(A.remainders[aPointer] == B.remainders[bPointer]) caseNum = 3;
        else if(A.remainders[aPointer] < B.remainders[bPointer]) caseNum = 2;
        else if(A.remainders[aPointer] > B.remainders[bPointer]) caseNum = 1;
        if(caseNum == 1) { // column in A is empty, we do nothing
            bPointer ++;
        }
        if(caseNum == 2) { // column in B is empty, so we copy the entire contents from the column in A
            helperRem[cPointer] = A.remainders[aPointer];
            helperL[cPointer] = (int *) malloc(sizeof(int *) * (long unsigned int) A.remainderSize[aPointer]);
            helperR[cPointer] = (int *) malloc(sizeof(int *) * (long unsigned int) A.remainderSize[aPointer]);
            for(int i = 0; i < A.remainderSize[aPointer]; i ++) {
                helperL[cPointer][i] = A.left[aPointer][i];
                helperR[cPointer][i] = A.right[aPointer][i];
            }
            arSiz[cPointer] = A.remainderSize[aPointer];
            cPointer ++;
            aPointer ++;
        }
        if(caseNum == 3) { // columns in A and B are non-empty, we use subtractColumns
            helperRem[cPointer] = A.remainders[aPointer];
            int csize = A.remainderSize[aPointer] + B.remainderSize[bPointer];
            helperL[cPointer] = (int *) malloc(sizeof(int) * (long unsigned int) csize);
            helperR[cPointer] = (int *) malloc(sizeof(int) * (long unsigned int) csize);
            arSiz[cPointer] = subtractColumns(&A, &B, helperL[cPointer], helperR[cPointer], aPointer, bPointer);
            if(arSiz[cPointer] != 0) cPointer ++;
            aPointer ++;
            bPointer ++;
        }
    }

    // copy values from helpers into C
    C.remNum = cPointer;
    C.left = (int **) malloc(sizeof(int *) * (long unsigned int) cPointer);
    C.right = (int **) malloc(sizeof(int *) * (long unsigned int) cPointer);
    C.remainders = (int *) malloc(sizeof(int) * (long unsigned int) cPointer);
    C.remainderSize = (int *) malloc(sizeof(int) * (long unsigned int) cPointer);

    for(int i = 0; i < cPointer; i ++) {
        C.remainders[i] = helperRem[i];
        C.remainderSize[i] = arSiz[i];
        C.left[i] = (int *) malloc(sizeof(int) * (long unsigned int) arSiz[i]);
        C.right[i] = (int *) malloc(sizeof(int) * (long unsigned int) arSiz[i]);
        for(int j = 0; j < arSiz[i]; j ++) {
            C.left[i][j] = helperL[i][j];
            C.right[i][j] = helperR[i][j];
        }
    }
    return C;
}

// Returns a set representing the intersection of sets A and B.
zbior_ary iloczyn(zbior_ary A, zbior_ary B) {
    return roznica(roznica(suma(A, B), roznica(A, B)), roznica(B, A)); // epic set theory
}

// Returns true iff number b belongs to set A.
bool nalezy(zbior_ary A, int b) {
    int remainderId = -1;
    int lo = 0, hi = A.remNum - 1, mid;
    while(lo <= hi) { // first check if there exist any elements with the searched remainder
        mid = (lo + hi) / 2;
        if(A.remainders[mid] == getRemainder(b)) {
            remainderId = mid;
            break;
        }
        if(A.remainders[mid] < getRemainder(b)) lo ++;
        else hi --;
    }
    if(remainderId == -1) return 0;
    lo = 0; hi = A.remainderSize[remainderId] - 1;
    int bval;
    if(b >= 0) bval = b / Q;
    else bval =  (b + 1) / Q - 1;
    while(lo <= hi) { // check whether b belongs to any of our intervals
        mid = (lo + hi) / 2;
        if((A.left[remainderId][mid] <= bval) && ((A.right[remainderId][mid] >= bval))) return 1;
        if(A.left[remainderId][mid] > bval) hi --;
        else lo ++;
    }
    return 0;
}

// The result of the function is the number of elements in set A.
unsigned moc(zbior_ary A) {
    unsigned ans = 0;
    for(int i = 0; i < A.remNum; i ++) {
        for(int j = 0; j < A.remainderSize[i]; j ++) {
            ans += (unsigned) ((long long) A.right[i][j] - (long long) A.left[i][j] + 1);
        }
    }
    return ans;
}

// The result of the function is Ary_q(A), i.e. the minimal number of pairwise disjoint arithmetic sequences with difference q whose union is the set A.
unsigned ary(zbior_ary A) {
    unsigned ans = 0;
    for(int i = 0; i < A.remNum; i ++) {
        ans += (unsigned) A.remainderSize[i];
    }
    return ans;
}


