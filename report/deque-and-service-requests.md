# Deque and Service Requests - Able Mwintuma Gambo

## Contribution summary

This module implements a generic double-ended queue from scratch and contributes
100 service-request records (`SR101`-`SR200`). Ordinary jobs join at the rear,
while a verified sanitation emergency can be placed at the front.

## Design and complexity

`MyDeque<E>` uses a circular `Object[]`, a `front` index, and a `size` counter.
Logical offset `i` is stored at `(front + i) % capacity`, avoiding element shifts.
When full, capacity doubles and live elements are copied in front-to-rear order.
All four end operations are O(1) normally; insertion is O(1) amortized and O(n)
only during resizing. Space complexity is O(n).

Empty removal/peek operations throw `NoSuchElementException`. Null insertions
and a negative initial capacity throw `IllegalArgumentException`.

## Urgent-request insertion trace

| Step | Operation | front | size | Logical contents |
|---:|---|---:|---:|---|
| 0 | Create deque | 0 | 0 | `[]` |
| 1 | `addRear(SR145)` | 0 | 1 | `[SR145]` |
| 2 | `addRear(SR146)` | 0 | 2 | `[SR145, SR146]` |
| 3 | `addFront(SR147 URGENT)` | 7 | 3 | `[SR147, SR145, SR146]` |
| 4 | `removeFront()` | 0 | 2 | `[SR145, SR146]` |

The urgent request is dispatched next without changing routine-job order.
Urgency must be validated before front insertion to prevent unfair bypassing.

## Correctness evidence

Invariant: exactly `size` live elements occupy logical offsets `0..size-1` in
order from `front`. Each add writes at one new boundary before increasing size;
each removal clears exactly one boundary before decreasing size. Resizing copies
each logical offset to the same offset and resets `front` to zero, preserving order.

`MyDequeTest` covers normal mixed-end order; empty, one-element, growth and
wrap-around boundaries; and invalid empty operations, capacity, and null values.

## Dataset contribution

The 100 records use existing `L001`-style location identifiers and categories
suited to campus waste operations. IDs `SR101`-`SR200` leave `SR001`-`SR100` and
`SR201`-`SR300` for the other assigned contributors.

## Live-defense notes

1. A deque differs from a queue because both ends support insertion and removal.
2. Circular indexing reuses freed slots and prevents O(n) shifting.
3. The rear item is at logical offset `size - 1` from `front`.
4. Resizing preserves logical order even after physical wrap-around.
5. Removed cells are cleared so obsolete references are not retained.
6. Run `UrgentRequestDequeDemo`, then `MyDequeTest` during the defense.
