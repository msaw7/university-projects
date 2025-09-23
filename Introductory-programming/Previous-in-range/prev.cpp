// Author: Mateusz Sawicki
// Implements a persistent segment tree utilizing shared_ptr for easy cleanup.

#include <bits/stdc++.h>
using namespace std;

typedef struct Vertex {
    shared_ptr <Vertex> l, r; // Pointers to children.
    int lo, hi; // Segment corresponding to the vertex.
    int val; // Maximum value in [lo, hi].
} Ver;

vector <int> X;
vector <shared_ptr <Ver> > roots; // Vector containing the roots of the different states of the tree in time.

shared_ptr <Ver> newVertex(int Lo, int Hi, int Val) {
    shared_ptr <Ver> a = make_shared<Ver>();
    a->lo = Lo;
    a->hi = Hi;
    a->l = nullptr;
    a->r = nullptr;
    a->val = Val;
    return a;
}


// Returns the middle point of a segment (lo+hi/2 does not properly work for negative values).
int getMid(int lo, int hi) {
    long long c = (long long) lo + (long long) hi;
    if(c >= 0) return (int) (c / 2);
    else {
        if(c % 2 == 0) return (int) (c / 2);
        else return (int) (c / 2 - 1);
    }
}

// Creates children for a node if necessary, used to traverse the implicit tree.
void PPush(shared_ptr <Ver> a) {
    int mid = getMid(a->lo, a->hi);
    if(a->l == nullptr) a->l = newVertex(a->lo, mid, -1);
    if(a->r == nullptr) a->r = newVertex(mid + 1, a->hi, -1);
    return;
}

/*
Recurses down the path leading to [v, v], sets the leaf's value to id, then updates the nodes going back up.
Keeps "shadow" as the image of the current vertex in the previous state of the tree to create the new one.
*/
void UUpdate(shared_ptr <Ver> curr, shared_ptr <Ver> shadow, int v, int id) {
    if(curr->lo == curr->hi) {
        curr->val = id - 1; // -1 accounts for the empty root added in function init.
        return;
    }
    PPush(shadow);
    // Attach the branch which will not be modified.
    int mid = getMid(curr->lo, curr->hi);
    if(v > mid) curr->l = shadow->l;
    if(v <= mid) curr->r = shadow->r;

    PPush(curr);
    if(v > mid) UUpdate(curr->r, shadow->r, v, id);
    if(v <= mid) UUpdate(curr->l, shadow->l, v, id);
    curr->val = max(curr->l->val, curr->r->val);
    return;
}

// Creates a new state of the persistent segment tree.
void pushBack(int v) {
    X.push_back(v);
    roots.push_back(newVertex(INT_MIN, INT_MAX, -1));
    int id = (int) roots.size() - 1;
    UUpdate(roots[id], roots[id - 1], v, id);
    return;
}


// Returns maximum value at segment [a, b].
int FFetch(shared_ptr <Ver> curr, int a, int b) {
    if((curr->lo > b) || (curr->hi < a)) return -1;
    if((curr->lo >= a) && (curr->hi <= b)) return curr->val;
    int ans = -1;
    // Omits the branches where no changes occured.
    if(curr->l != nullptr) ans = max(ans, FFetch(curr->l, a, b));
    if(curr->r != nullptr) ans = max(ans, FFetch(curr->r, a, b));
    return ans;
}

// Returns the answer to the task's query.
int prevInRange(int i, int lo, int hi) {
    return FFetch(roots[i + 1], lo, hi);
}

// Initializes an empty root as the first state of the tree and adds all elements of x.
void init(const vector<int> &x) {
    shared_ptr <Ver> blank = newVertex(INT_MIN, INT_MAX, -1);
    roots.push_back(blank);
    for(int i = 0; i < (int) x.size(); i ++) pushBack(x[i]);
    return;
}

// Frees all the memory by removing the only existing pointer to each of the roots.
void done() {
    X.clear();
    roots.clear();
    return;
}




