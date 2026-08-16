# Weekly development log

Log progress, challenges, and decisions here each week. Every member should add
their own entries under their name.

## Week 1
- 

## Week 2

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

### Emmanuel Aseda Kow Bentsil — Disjoint Set + Kruskal connectivity trace (2026-08-16)
- Implemented `structures.DisjointSet<T>`: built from scratch, no built-in
  collections. Manually-doubling `Object[]` + parallel `int[]` parent/rank/size
  arrays (same growth pattern as `Graph`'s vertex array), full path compression
  on `find`, and both union-by-rank and union-by-size attachment (constructor
  flag) so both are demonstrable live.
- Wrote 40 self-checking assertions (`DisjointSetTest.java`, no JUnit — plain
  `main()` + `check`/`checkTrue`/`checkThrows` helpers matching this repo's
  existing test style): normal (21), boundary (14), invalid input (5). All
  passing.
- Added `demo.DisjointSetKruskalTrace` as the connectivity-trace evidence for
  this module: loads a Roads CSV, sorts by weight, runs Kruskal's
  cycle-detection logic against `DisjointSet`, prints and exports the full
  step-by-step accept/reject trace. Auto-detects both the official
  `db/seed/roads.csv` column format and the raw `data/roads.csv` format.
- Ran it against the 104-row `data/roads.csv` / 52-row `data/locations.csv`
  already in the repo (real UG Legon campus data): 51 accepted, 53 rejected
  as cycles, final component count 1 (fully connected). Full trace exported
  to `results/csv/aseda_disjoint_set_kruskal_trace.csv`.
- **Flagging for the team:** `data/roads.csv`/`data/locations.csv` already
  contain a full real dataset (52 locations, 104 roads — past the 50/100
  minimums) but haven't been confirmed as official or copied into
  `db/seed/` in the agreed column format yet. Need Elsie (co-owner of the
  Roads dataset) and whoever added these files to confirm before we treat
  them as final, since `db/seed/roads.csv` uses different column names and
  drops the `road_id` column.
- Report section drafted in `report/aseda_bentsil_disjoint_set.md`.
- Still to do: get the dataset provenance confirmed (see above), then log
  ≥30 `algorithm_runs` rows once the DB loader is live, and confirm the
  Kruskal weight formula with Adam (Prim's/Kruskal's MST module) so both
  agree on the same edge-cost calculation.
