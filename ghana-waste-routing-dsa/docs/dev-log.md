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
