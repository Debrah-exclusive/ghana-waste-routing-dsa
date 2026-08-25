# Weekly development log

Log progress, challenges, and decisions here each week. Every member should add
their own entries under their name.

## Week 1
- 

## Week 2

### Able Mwintuma Gambo - Deque + Service Requests (2026-08-17)
- Implemented a generic circular-array deque with front/rear add, remove and peek operations.
- Added normal, boundary and invalid-input tests, including wrap-around and resizing.
- Added service-request records SR101-SR200 and an urgent-request insertion demo.
- Drafted the module report and live-defense notes in `report/deque-and-service-requests.md`.

### Emmanuel Thisara Otoo — Linked List + Selection/Insertion Sort (2026-08-12)
- Implemented `structures.MyLinkedList`: doubly linked, built from scratch, no
  built-in collections. Head/tail pointers, `O(min(i, n-i))` index walk, in-place
  `reverse()`, forward and descending iterators with fail-fast `modCount`
  detection, and an `invariantsHold()` self-check used by the tests.
- Implemented `SelectionSort` and `InsertionSort` over `int[]`, `Comparable[]`
  and `MyLinkedList` itself, plus `SortMetrics` (comparisons / moves / nanos) so
  the report can quote operation counts, not just noisy wall-clock times.
- Wrote 175 unit assertions (100 list, 36 selection, 39 insertion) covering
  normal, boundary and invalid input, including stability and the exact
  comparison counts predicted by the analysis. All passing.
- Added `demo.LinkedListSortDemo` (pointer diagrams, iterator demos, both trace
  tables) as the single entry point for this module — integration lead can call
  `LinkedListSortDemo.run()` from `ConsoleMenu`.
- Ran the performance experiment (`demo.SortBenchmark`): 4 distributions × 6
  sizes × 4 repeats = 192 timed runs, warm-up discarded, every run verified
  sorted. Exported to `results/csv/`, including 48 rows in `algorithm_runs`
  format for the DB owner.
- Decision worth recording: benchmark parameters are derived from index number
  22146178 as the brief requires — seed 22146178, 4 repeats per configuration,
  base input size 350 (doubling to 11 200), and 4 % positional disorder for the
  nearly-sorted distribution. The random data comes from a hand-written LCG
  seeded from the index number, so the whole experiment is reproducible on any
  machine.
- Report section drafted in `report/linked-list-and-sorts.md`.
- Still to do: export the three graphs listed in §5.4 to `results/graphs/`. 

### Abdul-Aziz Naeem — Dynamic Array + Linear Search + Binary Search (2026-08-25)
- Implemented `structures.DynamicArray<T>` from scratch using a primitive generic
  Object array. Initial capacity defaults to 2; doubles on every resize (amortised
  O(1) insert). No built-in Java collections were used.
  Supported operations: `insert`, `insertAt`, `get`, `set`, `remove(index)`,
  `remove(value)`, `resize`, `size`, `capacity`, `isEmpty`, `clear`, plus a
  fail-safe `Iterator<T>`.
- Implemented `algorithms.search.LinearSearch` — O(n) sequential scan, handles
  null elements and null arrays gracefully.
- Implemented `algorithms.search.BinarySearch` — O(log n) iterative divide-and-
  conquer, requires `T extends Comparable<T>` and a sorted input array.
  A null target or null array throws `IllegalArgumentException`; a null element
  encountered during the scan throws `IllegalStateException`.
- Wrote 26 unit tests across three test classes (11 DynamicArray, 6 LinearSearch,
  9 BinarySearch) covering normal, boundary, and invalid-input cases. All passing.
- Added `testPreconditionUnsorted` to BinarySearchTest to prove that binary search
  returns -1 on unsorted data even when the element exists — concrete evidence that
  the sorted-input precondition is mandatory.
- Ran `demo.SearchBenchmark`: 4 distributions × 6 sizes × 2 algorithms × 4 repeats
  = 192 timed runs, warm-up discarded. Exported to `results/csv/naeem_search_raw.csv`,
  `naeem_search_summary.csv`, and `naeem_algorithm_runs.csv` (48 rows, ready to
  load into the algorithm_runs table).
- Parameters derived from member index 7 (as required by the brief):
    - RNG seed = 7 (reproducible random arrays)
    - Repeats per configuration = 4 (3 + 7 % 3)
    - Base input size = 450 (100 + 7 × 50), doubling to 14 400
    - Nearly-sorted disorder = 7% of positions swapped
- Key decision: `DynamicArray` is generic (`<T>`) to match the project's existing
  pattern in `MyLinkedList<T>`, and the resize trace + binary search trace are
  documented in `report/array_and_search_report.md`.
- Pending (blocked on teammates): console menu entry (Ivan), DB log upload (Hannah).
