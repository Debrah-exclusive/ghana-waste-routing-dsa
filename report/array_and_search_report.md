# Dynamic Array and Searching Report

This document covers the implementation and trace analysis of the `DynamicArray`, `LinearSearch`, and `BinarySearch` algorithms.

## Overview

The `DynamicArray<T>` structure was implemented from scratch using a primitive generic array `T[] data` in Java. When the initial capacity (default 2) is exhausted, the array undergoes an $O(N)$ reallocation and copy operation to double its capacity. This ensures amortized $O(1)$ insertions.

The `LinearSearch` algorithm has an $O(N)$ time complexity and can process both sorted and unsorted sequences.
The `BinarySearch` algorithm has an $O(\log N)$ time complexity. However, it requires a precondition: the array must be sorted, and elements must implement `Comparable`. The violation of this precondition can lead to unexpected results without triggering standard exceptions (as proven by `testPreconditionUnsorted` in our test suite).

---

## Resize Trace Table

The following trace table outlines the internal state of `DynamicArray` when resizing from capacity `2` to `4` (simulating the insertion sequence `10`, `20`, `30`).

| Operation / Event      | `size` | `capacity` (`data.length`) | Memory / Elements in Array (`data`) | Complexity |
|------------------------|--------|----------------------------|-------------------------------------|------------|
| `new DynamicArray(2)`  | `0`    | `2`                        | `[null, null]`                      | $O(1)$     |
| `insert(10)`           | `1`    | `2`                        | `[10, null]`                        | $O(1)$     |
| `insert(20)`           | `2`    | `2`                        | `[10, 20]`                          | $O(1)$     |
| `insert(30)` (start)   | `2`    | `2`                        | `[10, 20]`                          | -          |
| `resize()` triggered   | `2`    | `4` (new capacity)         | `[10, 20, null, null]` (new array)  | $O(N)$     |
| `insert(30)` (finish)  | `3`    | `4`                        | `[10, 20, 30, null]`                | $O(1)$     |

---

## Binary Search Trace Table

The following trace table outlines the execution of binary search looking for `target = 40` in the sorted array `[10, 20, 30, 40, 50]`.

**Inputs**: 
- `array`: `[10, 20, 30, 40, 50]` (size: 5)
- `target`: `40`

| Iteration | `left` | `right` | `mid` | `array[mid]` | Condition `array[mid] ? target` | Action |
|-----------|--------|---------|-------|--------------|---------------------------------|--------|
| Initial   | `0`    | `4`     | -     | -            | -                               | Start loop |
| 1         | `0`    | `4`     | `2`   | `30`         | `30 < 40`                       | Target is in the right half, update `left = mid + 1` |
| 2         | `3`    | `4`     | `3`   | `40`         | `40 == 40`                      | Target found, `return 3` |

### Unsorted Precondition Violation

If the data was unsorted (e.g. `[50, 10, 30, 20, 40]`) and the target was `10`:
1. `mid = 2` (`30`). `30 > 10`, so update `right = 1`.
2. `mid = 0` (`50`). `50 > 10`, so update `right = -1`.
3. Loop breaks, returns `-1` (Not Found). 
The algorithm incorrectly asserts `10` is not in the array, demonstrating why sorted data is a strict precondition.

---

## Readiness 
Code and test drivers are complete. The implementation satisfies boundary test requirements, limits exception cases safely, and is ready for live defense.
