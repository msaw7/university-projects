// Author: Mateusz Sawicki

/*
We'll use a doubly linked list (here clientQueue) to handle the required queries.
Each client will store pointers to its neighbors in the queue (without knowing which is before and which is after).

If a client is at the beginning or end of the queue, they become its representative -
 - they hold a pointer to the queue they are in and mark whether they are at the front or at the end
*/

#include <cassert>
#include <cstdio>
#include <cstdlib>
#include <vector>
#include <iostream>
#include "kol.h"

int M, cnt = 0;

typedef struct interesant interesant;
typedef struct clientQueue { // these will be our queues, head is the first client to be served, tail is the last
    interesant *head, *tail;
} clientQueue;

clientQueue **Qs;

void otwarcie_urzedu(int m) {
    M = m;
    Qs = (clientQueue **) malloc(sizeof(clientQueue *) * (M + 1));
    for(int i = 0; i <= M; i ++) {
        clientQueue *a = (clientQueue *) malloc(sizeof(clientQueue));
        a->head = NULL;
        a->tail = NULL;
        Qs[i] = a;
    }
    return;
}

/*
Tool for traversing the queue.
Knowing that we came from b and are at a, returns what the next element of the queue is.
Returns NULL if there is no next element.
Works for b=NULL; then if a neighbor exists it will return any of them.
*/
interesant *moveForward(interesant *a, interesant *b) {
    if(a->i1 == b) return a->i2;
    if(a->i2 == b) return a->i1;
    return NULL;
}

// Tools to set a new client as the head or tail of a queue while preserving invariants.
void makeHead(interesant *a, clientQueue *nq) {
    if(nq->head != NULL) nq->head->isHead = 0;
    a->isHead = 1;
    a->q = nq;
    nq->head = a;
}

void makeTail(interesant *a, clientQueue *nq) {
    if(nq->tail != NULL) nq->tail->isTail = 0;
    a->isTail = 1;
    a->q = nq;
    nq->tail = a;
}

// Adds an (already existing) client to the end of the queue.
void addToQ(interesant *a, clientQueue *nq) {
    a->i1 = NULL;
    a->i2 = NULL;
    a->isHead = 0;
    a->isTail = 0;
    a->q = nq;
    if(nq->head == NULL) {
        makeHead(a, nq);
        makeTail(a, nq);
        return;
    }
    else {
        a->i1 = nq->tail;
        if(nq->tail->i1 == NULL) nq->tail->i1 = a;
        else if(nq->tail->i2 == NULL) nq->tail->i2 = a;
        makeTail(a, nq);
    }
    return;
}

interesant *nowy_interesant(int k) {
    interesant *a = (interesant *) malloc(sizeof(interesant));
    a->number = cnt;
    cnt ++;
    a->i1 = NULL;
    a->i2 = NULL;
    addToQ(a, Qs[k]);
    return a;
}

int numerek(interesant *a) {
    return a->number;
}

interesant *obsluz(int k) {
    interesant *ans = Qs[k]->head;
    if(ans == NULL) return NULL;
    interesant *prev = moveForward(ans, NULL);
    if(prev == NULL) {
        Qs[k]->head = NULL;
        Qs[k]->tail = NULL;
        return ans;
    }
    makeHead(prev, Qs[k]);
    if(prev->i1 == ans) prev->i1 = NULL;
    if(prev->i2 == ans) prev->i2 = NULL;
    return ans;
}

/*
Analyzes the two neighbors (b, c) of the removed client (a) from a given queue.
Attaches adjacency edges between b and c.
Assigns isHead or isTail status if needed when a was the beginning or end.
Adds a to queue k.
*/
void zmiana_okienka(interesant *a, int k) {
    interesant *b = a->i1, *c = a->i2;
    if((b == NULL) && (c == NULL)) {
        a->q->head = NULL;
        a->q->tail = NULL;
    }
    else if(b == NULL) {
        if(c->i1 == a) c->i1 = NULL;
        if(c->i2 == a) c->i2 = NULL;
        if(a->isHead) makeHead(c, a->q);
        if(a->isTail) makeTail(c, a->q);
    }
    else if(c == NULL) {
        if(b->i1 == a) b->i1 = NULL;
        if(b->i2 == a) b->i2 = NULL;
        if(a->isHead) makeHead(b, a->q);
        if(a->isTail) makeTail(b, a->q);
    }
    else {
        if(b->i1 == a) b->i1 = c;
        if(b->i2 == a) b->i2 = c;
        if(c->i1 == a) c->i1 = b;
        if(c->i2 == a) c->i2 = b;
    }
    addToQ(a, Qs[k]);
    return;
}

/*
Sets the head and tail of the removed queue (k1) to NULL.
Sets the tail of the queue (k2) to which the clients are moving to the tail of k1.
Attaches the appropriate adjacency edges between head of k1 and tail of k2.
*/
void zamkniecie_okienka(int k1, int k2) {
    if(k1 == k2) return;
    clientQueue *q1 = Qs[k1], *q2 = Qs[k2];
    if(q1->head == NULL) return;
    if(q2->head == NULL) {
        makeHead(q1->head, q2);
        makeTail(q1->tail, q2);
        q1->head = NULL;
        q1->tail = NULL;
        return;
    }
    q1->head->isHead = 0;
    q2->tail->isTail = 0;

    if(q1->head->i1 == NULL) q1->head->i1 = q2->tail;
    else if(q1->head->i2 == NULL) q1->head->i2 = q2->tail;

    if(q2->tail->i1 == NULL) q2->tail->i1 = q1->head;
    else if(q2->tail->i2 == NULL) q2->tail->i2 = q1->head;

    q1->tail->q = q2;
    q2->tail = q1->tail;
    q1->head = NULL;
    q1->tail = NULL;
    return;
}

/*
We iterate in both directions to determine where i2 is relative to i1.
We will use the queue with index M to conveniently simulate a fast track.
*/
std::vector<interesant *> fast_track(interesant *a1, interesant *a2) {
    interesant *curr1 = a1->i1, *curr2 = a1->i2, *prev1 = a1, *prev2 = a1, *new1, *new2;
    std::vector<interesant *> ans;
    if(a1 == a2) {
        ans.push_back(a1);
        zmiana_okienka(a1, M);
        obsluz(M);
        return ans;
    }
    int whichWay = 0;
    while((curr1 != NULL) && (curr2 != NULL)) {
        if(curr1 == a2) {
            whichWay = 1;
            break;
        }
        if(curr2 == a2) {
            whichWay = 2;
            break;
        }
        new1 = moveForward(curr1, prev1);
        new2 = moveForward(curr2, prev2);
        prev1 = curr1;
        prev2 = curr2;
        curr1 = new1;
        curr2 = new2;
    }
    // if we reach the end of the queue before encountering i2, then we went the wrong way
    if(whichWay == 0) {
        if(curr1 == NULL) whichWay = 2;
        if(curr2 == NULL) whichWay = 1;
    }
    curr1 = a1;
    if(whichWay == 1) prev1 = a1->i2;
    else prev1 = a1->i1;
    while(curr1 != a2) {
        ans.push_back(curr1);
        new1 = moveForward(curr1, prev1);
        prev1 = curr1;
        curr1 = new1;
    }
    ans.push_back(curr1);
    for(int i = 0; i < (int) ans.size(); i ++) {
        zmiana_okienka(ans[i], M);
        obsluz(M);
    }
    return ans;
}

// We swap the head and the tail.
void naczelnik(int k) {
    interesant *a = Qs[k]->head, *b = Qs[k]->tail;
    if(a == NULL || a == b) return;
    a->isHead = 0;
    b->isTail = 0;
    makeHead(b, Qs[k]);
    makeTail(a, Qs[k]);
    return;
}


// We again use the queue with index M to handle the office closure
std::vector<interesant *> zamkniecie_urzedu() {
    std::vector<interesant *> ans;
    for(int i = 0; i < M; i ++) zamkniecie_okienka(i, M);
    while(Qs[M]->head != NULL) {
        ans.push_back(obsluz(M));
    }
    for(int i = 0; i <= M; i ++) free(Qs[i]);
    return ans;
}


// Debugging functions:

void qDebug(clientQueue *q) {
    interesant *curr1 = q->head, *new1, *prev1 = NULL;
    while(curr1 != NULL) {
        std::cout << curr1->number << ' ';
        new1 = moveForward(curr1, prev1);
        prev1 = curr1;
        curr1 = new1;
    }
    std::cout << '\n';
}

void fullDebug() {
    for(int i = 0; i < M; i ++) {
        std::cout << i << ":  ";
        clientQueue *q = Qs[i];
        if(q->head == NULL) std::cout << "NULL ";
        else std::cout << q->head->number << " ";
        if(q->tail == NULL) std::cout << "NULL ";
        else std::cout << q->tail->number << " ";
        interesant *curr1 = q->head, *new1, *prev1 = NULL;
        while(curr1 != NULL) {
            std::cout << "<" << curr1->number << ' ';
            if(curr1->i1 == NULL) std::cout << "NULL ";
            else std::cout << curr1->i1->number << ' ';
            if(curr1->i2 == NULL) std::cout << "NULL";
            else std::cout << curr1->i2->number;
            std::cout << "> ";
            new1 = moveForward(curr1, prev1);
            prev1 = curr1;
            curr1 = new1;
        }
        std::cout << '\n';
    }
    std::cout << '\n';
}