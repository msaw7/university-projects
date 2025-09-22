// Author: Mateusz Sawicki

#include "ma.h"
#include <assert.h>
#include <errno.h>
#include <stdbool.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>

typedef struct moore moore;

// Returns ceil of x / 64 (for the purpose of handling bit arrays).
static size_t block(size_t x) {
    if(x % 64 == 0) return x / 64;
    return (x / 64) + 1;
}

// Extracts k-th bit from an array as described in the task.
static bool getBit(uint64_t *arr, uint64_t k) {
    uint64_t blockId = k / 64;
    uint64_t temp = 1ULL << (k % 64);
    if(arr[blockId] & temp) return 1;
    return 0;
}

// Sets k-th bit of an array as described in the task.
static void setBit(uint64_t *arr, uint64_t k, bool val) {
    uint64_t blockId = k / 64;
    uint64_t temp = 1ULL << (k % 64);
    if(val == 1) {
        arr[blockId] = arr[blockId] | temp;
    }
    if(val == 0) {
        arr[blockId] = arr[blockId] & (~temp);
    }
}

// Struct used to implement linked list.
typedef struct Node {
    moore_t *machine;
    uint64_t id;
    struct Node* next;
} Node;

static Node* createNode(moore_t *m, uint64_t i) {
    Node* node = (Node *) malloc(sizeof(Node));
    if(node == NULL) {
        errno = ENOMEM;
        return NULL;
    }
    node->machine = m;
    node->id = i;
    node->next = NULL;
    return node;
}

/*
Adds an element to the end of a list.
Returns NULL upon failure to allocate and a newly created node otherwise.
*/
static Node* addToList(Node **head, moore_t *m, int i) {
    Node* newNode = createNode(m, i);
    if(newNode == NULL) {
        return NULL;
    }
    if(*head == NULL) {
        *head = newNode;
        return newNode;
    }
    Node *curr = *head;
    while(curr->next != NULL) {
        curr = curr->next;
    }
    curr->next = newNode;
    return newNode;
}

/*
Searches the list for an element matching the target machine and ID.
If found, deletes that element.
*/
static void deleteFromList(Node **head, moore_t *targetMachine,
uint64_t targetId) {
    if((head == NULL) || (*head == NULL)) {
        return;
    }
    Node *curr = *head;
    Node *prev = NULL;

    // Is head the target?
    if((curr->machine == targetMachine) && (curr->id == targetId)) {
        *head = curr->next;
        free(curr);
        return;
    }

    // Look for target until found.
    while(curr->next != NULL) {
        // Go to next node.
        prev = curr;
        curr = curr->next;

        if((curr->machine == targetMachine) && (curr->id == targetId)) {
            // Reattach the link, omitting the created gap.
            prev->next = curr->next;
            // Free the deleted node.
            free(curr);
            return;
        }
    }
}

// Empties and frees the entire list.
static void freeList(Node **head) {
    Node *curr = *head;
    while(curr != NULL) {
        Node *prev = curr;
        curr = curr->next;
        free(prev);
    }
    *head = NULL;
}

struct moore {
    // in size, out size, state size
    uint64_t n, m, s;

    uint64_t *state;

    /*
    isLinked stores boolean values (bit-by-bit) in the uint64_t type the same 
    way as described in the task.
    isLinked size is equal to the size of input array.
    If there exists a connection from the k-th input slot, the k-th bit 
    is set to 1.
    It is set to zero otherwise.
    */
    uint64_t *isLinked;

    /*
    inputLinkIds and inputLinkMachines sizes are equal to n (true input size)
    If there exists a connection from the k-th input slot, inputLinkMachines and
    inputLinkIds store a pair representing the machine and output slot that it 
    links to.
    Otherwise those two values remain undefined.
    */
    moore_t **inputLinkMachines;
    uint64_t *inputLinkIds;

    /*
    outputLink size is equal to m
    outputLink[k] stores a linked list of Nodes (machine, idx) representing all 
    input slots that the k-th output slot is connected to.
    Note that if this points to a NULL, that list is simply empty.
    */
    Node **outputLink;

    /*
    If an input or output slot is unlinked, its value is given 
    in the arrays below.
    The boolean values of the slots are stored in the uint64_t type bit-by-bit
    as described in the task.
    */
    uint64_t *input, *output;

    transition_function_t tFunction;
    output_function_t outFunction;
};

/*
Frees a moore struct and all of its dynamically allocated members.
Does not handle dangling connections that may be left behind.
Can be used to free a partially initialized moore struct when allocation fails.
*/
static void freeMachine(moore_t *machine) {
    if(machine == NULL) {
        return;
    }
    free(machine->state);
    free(machine->input);
    free(machine->output);
    free(machine->isLinked);
    free(machine->inputLinkMachines);
    free(machine->inputLinkIds);

    if(machine->outputLink != NULL) {
        for(size_t i = 0; i < machine->m; i ++) {
            freeList(&machine->outputLink[i]);
        }
    }
    free(machine->outputLink);

    free(machine);
}

typedef void (*transition_function_t)(uint64_t *next_state, uint64_t
const *input, uint64_t const *state, size_t n, size_t s);

typedef void (*output_function_t)(uint64_t *output, uint64_t const
*state, size_t m, size_t s);

moore_t *ma_create_full(size_t n, size_t m, size_t s, transition_function_t t,
     output_function_t y, uint64_t const *q) {
    if((m == 0) || (s == 0) || (t == NULL) || (y == NULL) || (q == NULL)) {
        errno = EINVAL;
        return NULL;
    }

    moore_t *machine = malloc(sizeof(moore_t));
    if(machine == NULL) {
        errno = ENOMEM;
        freeMachine(machine);
        return NULL;
    }

    machine->tFunction = t;
    machine->outFunction = y;
    machine->n = n;
    machine->m = m;
    machine->s = s;

    machine->state = calloc(block(s), sizeof(uint64_t));
    machine->input = calloc(block(n), sizeof(uint64_t));
    machine->output = calloc(block(m), sizeof(uint64_t));
    machine->isLinked = calloc(block(n), sizeof(uint64_t));
    machine->inputLinkMachines = calloc(n, sizeof(moore_t *));
    machine->inputLinkIds = calloc(n, sizeof(uint64_t));
    machine->outputLink = calloc(m, sizeof(Node *));

    bool memFail = false;
    if(machine->state == NULL) memFail = 1;
    if(machine->input == NULL) memFail = 1;
    if(machine->output == NULL) memFail = 1;
    if(machine->isLinked == NULL) memFail = 1;
    if(machine->inputLinkMachines == NULL) memFail = 1;
    if(machine->inputLinkIds == NULL) memFail = 1;
    if(machine->outputLink == NULL) memFail = 1;
    if(memFail) {
        errno = ENOMEM;
        freeMachine(machine);
        return NULL;
    }

    ma_set_state(machine, q);
    return machine;
}

// ID function for the purposes of ma_create_simple
static void identityFunction(uint64_t *output, uint64_t const *state, size_t m,
     size_t s) {
    if(m != s) {
        errno = EINVAL;
        return;
    }
    size_t M = block(m);
    for(size_t i = 0; i < M; i ++) output[i] = state[i];
    return;
}

moore_t * ma_create_simple(size_t n, size_t s, transition_function_t t) {
    if(s == 0 || t == NULL) {
        errno = EINVAL;
        return NULL;
    }
    uint64_t *tab = calloc(s, sizeof(uint64_t));
    if(tab == NULL) {
        errno = ENOMEM;
        return NULL;
    }
    // Note that tab is initialized with zeroes.
    const uint64_t *q = tab;
    moore_t * machine = ma_create_full(n, s, s, t, identityFunction, q);
    free(tab);
    return machine;
}

int ma_set_state(moore_t *a, uint64_t const *state) {
    if((a == NULL) || (state == NULL)) {
        errno = EINVAL;
        return -1;
    }
    size_t S = block(a->s);
    for(size_t i = 0; i < S; i ++) {
        a->state[i] = state[i];
    }
    a->outFunction(a->output, a->state, a->m, a->s);
    return 0;
}

int ma_set_input(moore_t *a, uint64_t const *input) {
    if((a == NULL) || (input == NULL)) {
        errno = EINVAL;
        return -1;
    }
    size_t N = block(a->n);
    if(N == 0) {
        errno = EINVAL;
        return -1;
    }
    for(size_t i = 0; i < N; i ++) {
        a->input[i] = input[i];
    }
    return 0;
}

uint64_t const * ma_get_output(moore_t const *a) {
    if(a == NULL) {
        errno = EINVAL;
        return NULL;
    }
    return a->output;
}

// Unplug a single connection in the x-th slot of a_in's input.
static void unplug(moore_t *a_in, uint64_t x) {
    // Check if there exists a connection in the input slot.
    if(getBit(a_in->isLinked, x) == 0) return;

    // Extract the other machine and the index of the output slot it is
    // connected to.
    moore_t *a_out = a_in->inputLinkMachines[x];
    uint64_t y = a_in->inputLinkIds[x];

    // Disconnect on input side.
    setBit(a_in->isLinked, x, 0);
    a_in->inputLinkMachines[x] = NULL;
    a_in->inputLinkIds[x] = 0;

    // Disconnect on output side.
    deleteFromList(&(a_out->outputLink[y]), a_in, x);
}

// Overflow-proof check if a + b > c
static bool overflowInequality(size_t a, size_t b, size_t c) {
    if(b > SIZE_MAX - a) return true;
    return a + b > c;
}

// Unplugs the slots one by one.
int ma_disconnect(moore_t *a_in, size_t in, size_t num) {
    if((a_in == NULL) || (num == 0)) {
        errno = EINVAL;
        return -1;
    }
    if(overflowInequality(in, num, a_in->n)) {
        errno = EINVAL;
        return -1;
    }
    for(size_t i = in; i < in + num; i ++) {
        unplug(a_in, (uint64_t) i);
    }
    return 0;
}

/*
Plug a single connection from the x-th slot of a_in's input into y-th
slot of a_out's output.
Returns -1 on memory failure, 0 otherwise.
*/
static int plug(moore_t *a_in, uint64_t x, moore_t *a_out, uint64_t y) {
    // (Possibly) Unplug existing connection in from the input slot.
    unplug(a_in, x);

    // Establish connection in the output machine.
    Node* newNode = addToList(&(a_out->outputLink[y]), a_in, x);

    // Check for memory failure.
    if(newNode == NULL) {
        errno = ENOMEM;
        return -1;
    }

    // Establish connection in the input machine.
    a_in->inputLinkMachines[x] = a_out;
    a_in->inputLinkIds[x] = y;
    setBit(a_in->isLinked, x, 1);
    return 0;
}

// Connects the slots one by one
int ma_connect(moore_t *a_in, size_t in, moore_t *a_out, size_t out,
size_t num) {
    if((a_in == NULL) || (a_out == NULL) || (num == 0)) {
        errno = EINVAL;
        return -1;
    }
    if(overflowInequality(in, num, a_in->n)) {
        errno = EINVAL;
        return -1;
    }
    if(overflowInequality(out, num, a_out->m)) {
        errno = EINVAL;
        return -1;
    }
    for(size_t i = 0; i < num; i ++) {
        // Connect a singular slot and check for memory failure.
        if(plug(a_in, in + i, a_out, out + i) == -1) {
            // If memory failed, begin rollback.
            for(size_t j = 0; j <= i; j ++) {
                unplug(a_in, in + j);
            }
            return -1;
        }
    }
    return 0;
}

/*
For each machine at[i] calculates a trueInput array based on other
machines feeding the input.
Afterwards applies the "true" input to the transition function of each machine.
This simulates the step being done in parallel, rather than sequentially.
*/
int ma_step(moore_t *at[], size_t num) {
    if(at == NULL || num == 0) {
        errno = EINVAL;
        return -1;
    }
    for(size_t i = 0; i < num; i ++) {
        if(at[i] == NULL) {
            errno = EINVAL;
            return -1;
        }
    }
    uint64_t *newStates[num];

    for(size_t i = 0; i < num; i ++) {
        size_t N = at[i]->n;
        uint64_t *trueInput = calloc(block(N), sizeof(uint64_t));
        newStates[i] = calloc(block(at[i]->s), sizeof(uint64_t));

        /*
        If allocation failed, begin rollback.
        Note that the states of the machines have not been changed yet.
        The machines remain valid structurally.
        */
        if(trueInput == NULL || newStates[i] == NULL) {
            free(trueInput);
            for(size_t j = 0; j <= i; j ++) free(newStates[j]);
            errno = ENOMEM;
            return -1;
        }

        // Set the true input bit-by-bit.
        for(size_t j = 0; j < N; j ++) {
            if(getBit(at[i]->isLinked, j) == 0) {
                setBit(trueInput, j, getBit(at[i]->input, j));
            }
            else {
                moore_t *a_out = at[i]->inputLinkMachines[j];
                uint64_t id = at[i]->inputLinkIds[j];
                setBit(trueInput, j, getBit(a_out->output, id));
            }
        }

        // Apply the transition function to our input.
        at[i]->tFunction(newStates[i], trueInput, at[i]->state, at[i]->n,
             at[i]->s);
        free(trueInput);
    }

    // Copy the calculated states into the machines.
    for(size_t i = 0; i < num; i ++) {
        ma_set_state(at[i], newStates[i]);
        free(newStates[i]);
    }
    return 0;
}

/*
Disconnects all the slots in the input and output and frees all memory
used by the struct.
Removes dangling connections plugged into other machines.
*/
void ma_delete(moore_t *a) {
    if(a == NULL) return;

    ma_disconnect(a, 0, a->n);

    // Other machines are still connected to a's output and their slots need to 
    // be marked invalid.
    for(size_t i = 0; i < a->m; i ++) {
        Node *curr = a->outputLink[i];
        while(curr != NULL) {
            moore_t *a_in = curr->machine;
            uint64_t x = curr->id;
            setBit(a_in->isLinked, x, 0);
            curr = curr->next;
        }
    }
    freeMachine(a);
    return;
}