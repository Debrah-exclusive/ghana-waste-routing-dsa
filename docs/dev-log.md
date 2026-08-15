# Weekly development log

Log progress, challenges, and decisions here each week. Every member should add
their own entries under their name.

## Week 1
- 

## Week 2

### Wiafe Franklin Asare — Queue & Circular Queue, service requests 1–100
- Implemented `structures/QueueCircular.java` from scratch: fixed-capacity ring
  array with `front`/`rear`/`size`, wrap-around on both indices, throwing
  (`enqueue`/`dequeue`) and non-throwing (`offer`/`poll`) variants, and an
  optional growable mode that re-lays a wrapped queue in FIFO order when it
  doubles.
- Added `structures/MyQueue.java`, the unbounded linked companion, for the cases
  where a capacity ceiling would be wrong (BFS frontier, full-day backlog).
- Decision: full-vs-empty is resolved with an explicit `size` counter rather
  than sacrificing a slot, because callers need `size()` anyway and it keeps
  `capacity` honest. Reasoning written up in `report/queue-section.md` §2.3.
- Wrote 225 assertions across `QueueCircularTest` and `MyQueueTest` (normal,
  boundary, invalid input). Plain Java, no JUnit, so they run on a bare JDK.
  All passing.
- Challenge: the boundary tests caught a real bug in `wrapArounds()` — it was
  counting the first enqueue into an empty queue as a wrap, because `rear` is
  parked at `capacity - 1` when empty. Fixed the counter, not the test; a roll
  only counts when it carries an index over live data.
- Built `QueueTraceDemo` to generate the front/rear trace from the real dataset
  rather than by hand. It also simulates a non-circular array queue over the
  same 18 steps: the circular queue handled 7 requests on 5 slots, the
  non-circular one jammed after 5 and ended empty-but-unable-to-accept. Outputs
  `report/queue-trace.md` and `results/csv/queue_front_rear_trace.csv`.
- Collected service requests 1–100 into `db/seed/` and `data/`. All fields are
  rule-derived (destination = nearest facility, urgency = public-health risk,
  deadline = submitted + SLA); `requestId` order equals `timeSubmitted` order,
  so the CSV feeds the queue directly with no sort step.
- Blocked on nothing, but `locationId` 1–45 = collection points / 46–50 =
  disposal sites is an assumption that needs confirming with Desmond and Kelvin
  once `locations.csv` is filled.
- Report section drafted at `report/queue-section.md`, including defence notes.
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
