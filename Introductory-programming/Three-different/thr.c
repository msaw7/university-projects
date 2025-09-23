// Author: Mateusz Sawicki

/*
Usage:
Compile - gcc @opcje thr.c -o thr
Run on example - ./thr < example.in
*/

/*
For each motel, we will check what is the nearest and farthest motel of another chain
on the left and right side.
To do this, we will use values computed on the prefix and suffix.
We will only remember the 3 "best" motels of different chains, because only they can affect the result.
*/

#include <stdio.h>
#include <stdlib.h>
#include <assert.h>

#define T 1000009
#define INF 1000000000000000009


void arrAlloc(int **a, int n) {
    *a = (int *) malloc(sizeof(int) * (long unsigned int) n);
}

/*
Here we will store the 3 farthest and nearest motels, with certain invariants:
- values in maxVal and minVal are arranged monotonically
- each motel chain will appear in minId and maxId at most once
*/
typedef struct best3{
    long long minVal[3], maxVal[3];
    int minId[3], maxId[3];
} best3;

// Diagnostic function.
void printBest(best3 *a) {
	for(int i = 0; i < 3; i ++) printf("%lld ", a->minVal[i]);
	printf("\n");
	for(int i = 0; i < 3; i ++) printf("%d ", a->minId[i]);
	printf("\n");
	for(int i = 0; i < 3; i ++) printf("%lld ", a->maxVal[i]);
	printf("\n");
	for(int i = 0; i < 3; i ++) printf("%d ", a->maxId[i]);
	printf("\n");
}

void iniBest3(best3 *a) {
	for(int i = 0; i < 3; i ++) {
		a->minVal[i] = INF;
		a->maxVal[i] = -INF;
		a->minId[i] = -1;
		a->maxId[i] = -1;
	}
	return;
}

// Helper function to compute new values of prefix and suffix.
void moveBest(best3 *a, long long d) {
	for(int i = 0; i < 3; i ++) {
		if(a->minId[i] != -1) a->minVal[i] += d;
		if(a->maxId[i] != -1) a->maxVal[i] += d;
	}
	return;
}

void addElement(best3 *a, int chainId) {
	for(int i = 0; i < 3; i ++) {
		if(a->maxId[i] == chainId) break; // each chain only once
		if(a->maxId[i] == -1) {
			a->maxVal[i] = 0;
			a->maxId[i] = chainId;
			break;
		}
	}
	/*
	Ugly casework: we always want to put our added element at the beginning, 
	because it has distance = 0, but we must check if this motel chain 
	was already present.
	We remove it, and shift the rest to the right accordingly.
	*/
	if(a->minId[0] == chainId) {
        a->minVal[0] = 0;
        a->minId[0] = chainId;
	}
	else if(a->minId[1] == chainId) {
        a->minVal[1] = a->minVal[0];
        a->minId[1] = a->minId[0];
        a->minVal[0] = 0;
        a->minId[0] = chainId;
	}
	else { // if this motel chain does not appear or is at the last position, we drop the last element and shift the rest to the right
        a->minVal[2] = a->minVal[1];
        a->minId[2] = a->minId[1];
        a->minVal[1] = a->minVal[0];
        a->minId[1] = a->minId[0];
        a->minVal[0] = 0;
        a->minId[0] = chainId;
	}
	return;
}

long long max(long long a, long long b) {
    if(a >= b) return a;
    return b;
}

long long min(long long a, long long b) {
    if(a <= b) return a;
    return b;
}

int main() {
    int n;
    assert(scanf("%d", &n) == 1);
    int *chain, *dis;
    arrAlloc(&chain, n);
    arrAlloc(&dis, n);
    for(int i = 0; i < n; i ++) {
        assert(scanf("%d", &chain[i]));
        assert(scanf("%d", &dis[i]));
    }
    int prev = -1, currPtr = 0;
    int cnt = 0;
    int **motels, *mDis, *mSize;
    arrAlloc(&mDis, n);
    arrAlloc(&mSize, n);
    motels = (int **) malloc(sizeof(int *) * (long unsigned int) n);

    // We transform the input so that motels at the same location are processed together.
    // motels[i] will store the list of motels at a given place on the highway.
    // mDis[i] will store the distance of these motels.
    // mSize[i] will store the size of motels[i].
    for(int i = 0; i < n; i ++) {
        if(prev != dis[i]) {
            if(cnt != 0) {
                arrAlloc(&motels[currPtr], cnt);
                for(int j = 0; j < cnt; j ++) motels[currPtr][j] = chain[i - j - 1];
                mSize[currPtr] = cnt;
                mDis[currPtr] = dis[i - 1];
                currPtr ++;
            }
            cnt = 1;
            prev = dis[i];
        }
        else cnt ++;
    }
    if(cnt != 0) {
        arrAlloc(&motels[currPtr], cnt);
        for(int j = 0; j < cnt; j ++) motels[currPtr][j] = chain[n - j - 1];
        mSize[currPtr] = cnt;
        mDis[currPtr] = dis[n - 1];
        currPtr ++;
    }

    n = currPtr;
	best3 *pref, *suf, currBest;
	pref = (best3 *) malloc(sizeof(best3) * (long unsigned int) n);
	suf = (best3 *) malloc(sizeof(best3) * (long unsigned int) n);
	iniBest3(&currBest);
	for(int i = 0; i < n; i ++) {
		for(int j = 0; j < mSize[i]; j ++) addElement(&currBest, motels[i][j]);
		pref[i] = currBest;
		if(i < n - 1) moveBest(&currBest, mDis[i + 1] - mDis[i]);
	}
    iniBest3(&currBest);
    for(int i = n - 1; i >= 0; i --) {
		for(int j = 0; j < mSize[i]; j ++) addElement(&currBest, motels[i][j]);
		suf[i] = currBest;
		if(i > 0) moveBest(&currBest, mDis[i] - mDis[i - 1]);
	}

	long long ansmax = -INF, ansmin = INF;
	// Recover the result by iterating over all possible "middle" motels.
	for(int i = 0; i < n; i ++) {
        for(int j = 0; j < mSize[i]; j ++) {
            for(int a = 0; a < 3; a ++) {
                for(int b = 0; b < 3; b ++) {
                    // we only take results that involve different motel chains
                    if((pref[i].minId[a] != suf[i].minId[b]) && (pref[i].minId[a] != motels[i][j]) && (suf[i].minId[b] != motels[i][j])) {
                        ansmin = min(ansmin, max(pref[i].minVal[a], suf[i].minVal[b]));
                    }
                    if((pref[i].maxId[a] != suf[i].maxId[b]) && (pref[i].maxId[a] != motels[i][j]) && (suf[i].maxId[b] != motels[i][j])) {
                        ansmax = max(ansmax, min(pref[i].maxVal[a], suf[i].maxVal[b]));
                    }
                }
            }
        }
	}
	if(ansmin != INF) printf("%lld %lld\n", ansmin, ansmax);
	else printf("0 0\n");
    return 0;
}