# Disjoint Set (Union-Find) + Kruskal Connectivity Trace

**Module owner:** Emmanuel Aseda Kow Bentsil
**Course:** DCIT 204/308 — Data Structures and Algorithms I & II
**Project:** Ghana Smart Service Operations Optimizer — Waste/Sanitation Routing

This is the section to paste into the shared report under headings 5 (data
structure implementation), 7 (correctness evidence), 9 (DB integration
evidence) and 11 (individual contribution).

Source files:

| File | Contents |
|---|---|
| `src/main/java/structures/DisjointSet.java` | Union-Find, built from scratch (no built-in collections) |
| `src/test/java/structures/DisjointSetTest.java` | 40 self-checking assertions — normal, boundary, invalid input |
| `src/main/java/demo/DisjointSetKruskalTrace.java` | live demo: loads a Roads CSV, runs the Kruskal connectivity trace, exports it |
| `results/csv/aseda_disjoint_set_kruskal_trace.csv` | full 104-row trace output |

---

## 1. Role in the system

Before the system commits to a spanning routing network, it needs to know
whether a given set of roads actually connects every collection point, and
which candidate roads are redundant (would just close a cycle). Disjoint Set
answers exactly that question in close to constant time per query, which is
why it's the structure Kruskal's algorithm is built around: sort every
candidate road by cost, then walk the sorted list asking Union-Find "are
these two locations already reachable from each other?" — if not, the road
is necessary and gets accepted; if so, it's redundant and gets rejected.

## 2. Structure — `DisjointSet<T>`

Built from scratch, no `java.util.HashMap`/`ArrayList`/etc. Elements are
held in a manually doubling `Object[]` (the same growth pattern
`structures.Graph` uses for its vertex array), with parallel `int[]` arrays
for parent pointers, rank, and size. Looking up an element's index is a
linear scan — O(n) — the same trade-off `Graph.findVertex` already makes in
this codebase, and it's negligible at this project's scale (≤50 locations,
≤100 roads per the brief).

| Operation | Behaviour |
|---|---|
| `makeSet(x)` | Registers singleton `{x}`. Idempotent. |
| `find(x)` | Walks to the root, then re-points every visited node directly at the root (full path compression). |
| `union(x, y)` | Attaches the smaller tree under the larger one (rank or size, selectable at construction); returns `false` if `x` and `y` were already in the same set. |
| `connected(x, y)` | `find(x).equals(find(y))` |
| `componentSize(x)` / `componentCount()` | Size of x's component / total number of components |

Both union-by-rank and union-by-size are implemented (constructor flag), so
both are demonstrable live. Complexity: **O(m·α(n))** amortised for the
union/find core (α = inverse Ackermann, effectively constant), plus the
documented O(n) linear-scan factor for looking up an element by value.

## 3. Algorithm — Kruskal connectivity trace

`demo.DisjointSetKruskalTrace` sorts every road by weight
(`distanceKm * roadConditionWeight` — condition weight is a ≥1.0 penalty
multiplier, so a longer-but-well-maintained road can still beat a short but
damaged one) and processes them in ascending order, printing an
ACCEPT/reject decision for each. This is the Disjoint Set module's
connectivity-trace evidence; the full MST optimisation (edge list + total
cost) is Adam Mohammed's module (`algorithms.graph.PrimKruskal`).

**Run against the 52-location, 104-road dataset already in `data/roads.csv`**
(real UG Legon campus locations — see the dataset note below):

| step | edge (weight) | decision | #components after | #MST edges after |
|---|---|---|---|---|
| 1 | L016–L040 (0.000) | ACCEPT | 1 | 1 |
| 2 | L023–L024 (0.000) | ACCEPT | 2 | 2 |
| 3 | L008–L036 (0.110) | ACCEPT | 3 | 3 |
| … | … | … | … | … |
| 12 | L001–L024 (0.132) | reject | 7 | 11 |
| … | … | … | … | … |
| 104 | L020–L022 (1.188) | reject | 1 | 51 |

Full 104-row trace: `results/csv/aseda_disjoint_set_kruskal_trace.csv`.

**Result:** 104 edges processed, 51 accepted (= 52 locations − 1, exactly
what a spanning tree over 52 nodes requires) and 53 rejected as cycles.
Final component count: **1** — every location in the dataset ends up
connected into a single network. Total accepted-edge weight: 11.484.

## 4. Correctness evidence — unit tests

40 self-checking assertions in `DisjointSetTest.java` (no JUnit — matches
this repo's convention of plain `main()`-driven tests with `check`/
`checkTrue`/`checkThrows` helpers), all passing:

- **Normal (21):** singleton creation, two-element union, a 6-element
  multi-step chain merge, a 100-element chained union verifying path
  compression keeps every node's root consistent, union-by-size sizing.
- **Boundary (14):** empty structure, self-union (`union(x, x)` → `false`,
  no state change), re-union of an already-connected pair (→ `false`,
  order-independent), duplicate `makeSet` calls (idempotent), singleton
  self-connectivity.
- **Invalid input (5):** `makeSet(null)` → `IllegalArgumentException`;
  `find`/`union`/`connected` on an unregistered element →
  `NoSuchElementException`; `find(null)` → `IllegalArgumentException`.

## 5. Dataset note (Section 2 requirement)

`data/roads.csv` (104 rows) and `data/locations.csv` (52 rows) already
contain real University of Ghana, Legon campus locations and the roads
between them — no personal data, all place names. **These haven't yet been
confirmed as the official dataset or copied into `db/seed/` in the agreed
column format** (`db/seed/roads.csv` expects
`fromLocationId,toLocationId,distance,travelTime,roadConditionWeight`; the
`data/` version has an extra `road_id` column and different column names).
This trace demo auto-detects either format, so no code changes are needed
once that's sorted — but please confirm with Elsie (who owns the other half
of the Roads dataset) and whoever originally added these files before we
treat them as final.

## 6. Outstanding

- [ ] Confirm `data/roads.csv` / `data/locations.csv` provenance and get
      the team's sign-off to promote them into `db/seed/` (column rename:
      drop `road_id`, `from_location_id → fromLocationId`, etc.)
- [ ] If instead we collect a fresh 50 records for this task, coordinate
      with Elsie on non-overlapping road segments first
- [ ] Log ≥30 `algorithm_runs` timing rows once the DB loader (Hannah) is live
- [ ] Confirm the Kruskal weight formula with Adam so both modules agree
