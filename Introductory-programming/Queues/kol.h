#ifndef KOL_H
#define KOL_H

#include <vector>

// Everywhere in the task you can assume that pointers passed to the functions are
// pointers to structures of type interesant that were at some point returned by
// the function "nowy_interesant".

// NOTE: the function "numerek" should also work for interesants
// that are not standing in any queue, and even after calling "zamkniecie_urzedu"

// To be filled in:
struct interesant {
    int number; // Ticket number.
    bool isHead, isTail; // True iff the interesant is respectively at the beginning or at the end of their queue (or has left the office).
    struct clientQueue *q; // This pointer is guaranteed to be valid only when isHead or isTail is true.
    struct interesant *i1, *i2; // Pointers to neighbours.
    /*
    A few invariants:
    If an interesant is the head or the tail, then at least one of the pointers i1 and i2 is NULL.
    i1 and i2 are both NULL iff the interesant is in a single-element queue or has left the office.
    When neither i1 nor i2 is NULL, then i1 != i2.
    */
};

/**
 * @brief Initializes the library
 *
 * @param m number of service windows
 */
void otwarcie_urzedu(int m);

/**
 * @brief A new client arrives at the office
 *
 * @param k the index of the window whose queue the new client joins
 * @return interesant* pointer to the newly created client structure
 */

interesant *nowy_interesant(int k);

/**
 * @brief Returns the client's number
 *
 * Clients receive numbers that are consecutive integers starting from 0
 *
 * NOTE: must work for clients not standing in any queue and after
 * calling "zamkniecie_urzedu"
 *
 * @param i pointer to the client
 * @return int the client's number
 */

int numerek(interesant *i);

/**
 * @brief Serves one client
 *
 * @param k index of the window at which a client is being served
 * @return interesant* the served client, or NULL if the queue was empty
 */

interesant *obsluz(int k);

/**
 * @brief Client i changes to the queue of window k
 *
 * You may assume the client previously stood in the queue of another window
 *
 * @param i the client changing windows
 * @param k the index of the window to which client i moves
 */

void zmiana_okienka(interesant *i, int k);

/**
 * @brief Window k1 is closed, and clients standing in its queue move to
 * window k2, preserving the order they had in the queue
 *
 * @param k1 the window being closed
 * @param k2 the window to which clients move
 */

void zamkniecie_okienka(int k1, int k2);

/**
 * @brief Clients from i1 to i2 move to a special window and are served immediately
 *
 * We assume i1 and i2 stand in the same queue and that i1 is before i2 in that queue,
 * unless i1 == i2.
 *
 * @param i1 first client to be served via fast track
 * @param i2 last client to be served via fast track
 * @return std::vector<interesant *> All fast-tracked clients in the order they stood in the queue, starting from i1 and ending at i2
 */

std::vector<interesant *> fast_track(interesant *i1, interesant *i2);

/**
 * @brief The boss reverses the order of a queue
 *
 * @param k the index of the window whose queue the boss reverses
 */

void naczelnik(int k);

/**
 * @brief Closes the office
 *
 * @return std::vector<interesant *> all clients who were still standing in queues, ordered by window index and then by queue order
 */

std::vector<interesant *> zamkniecie_urzedu();

#endif