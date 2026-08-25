# Linked List, Selection Sort and Insertion Sort

**Module owner:** Emmanuel Thisara Otoo
**Course:** DCIT 204/308 — Data Structures and Algorithms I & II
**Project:** Ghana Smart Service Operations Optimizer — Waste/Sanitation Routing

This is the section to paste into the shared report under headings 5 (data
structure implementation), 6 (algorithm implementation), 7 (correctness
evidence), 8 (performance analysis), 10 (responsible algorithm selection) and
11 (individual contribution).

Source files:

| File | Contents |
|---|---|
| `src/main/java/structures/MyLinkedList.java` | doubly linked list, built from scratch |
| `src/main/java/algorithms/sorting/SelectionSort.java` | selection sort (array, Comparable, linked list) + trace |
| `src/main/java/algorithms/sorting/InsertionSort.java` | insertion sort (array, Comparable, linked list) + trace |
| `src/main/java/algorithms/sorting/SortMetrics.java` | comparison/move/time counters shared by both sorts |
| `src/test/java/structures/MyLinkedListTest.java` | 100 assertions — normal, boundary, invalid |
| `src/test/java/algorithms/sorting/SelectionSortTest.java` | 36 assertions |
| `src/test/java/algorithms/sorting/InsertionSortTest.java` | 39 assertions |
| `src/main/java/demo/LinkedListSortDemo.java` | live demo: diagrams, iterators, traces |
| `src/main/java/demo/SortBenchmark.java` | the performance experiment |

---

## 1. Role in the system

The dispatcher holds the day's service requests as an ordered working list.
Requests arrive through the day and are cancelled, escalated or completed out of
order, so the list is modified far more often than it is indexed. That is the
case a linked list is for: removing a request the dispatcher is already looking
at costs two pointer writes, no matter how long the list is, whereas an array
would have to shift every element behind it.

The list is then ordered by deadline (or urgency) before dispatch, which is what
the two sorts do.

---

## 2. Data structure: doubly linked list

### 2.1 Node layout

Each node holds a value and two links. The list keeps a `head` and a `tail`
pointer and an integer `size`; there are no sentinel nodes.

```
        head                                                  tail
         |                                                     |
         v                                                     v
null <- [prev| Accra Central |next] <-> [prev| Madina |next] <-> [prev| Kaneshie |next] -> null
```

An empty list is `head == tail == null, size == 0`.

### 2.2 Insert in the middle — the operation that justifies the structure

`add(2, "Tema Newtown")` on the list above:

```
before:   ... [Madina] <--------------------> [Kaneshie] ...

                       [Tema Newtown]              (new node allocated)

after:    ... [Madina] <-> [Tema Newtown] <-> [Kaneshie] ...
```

Four pointer writes, no shifting, independent of list length. The same picture
run backwards is `unlink()`, used by `remove`, `removeValue` and `Iterator.remove`.

### 2.3 Reverse in place

`reverse()` swaps `prev` and `next` inside every node and then exchanges `head`
and `tail`. No new node is allocated — the outbound route becomes the return
route. Measured output from `demo.LinkedListSortDemo`:

```
outbound: null <- [Depot] <-> [Madina] <-> [Kaneshie] <-> [Landfill] -> null
return:   null <- [Landfill] <-> [Kaneshie] <-> [Madina] <-> [Depot] -> null
```

### 2.4 Operations and complexity

| Operation | Cost | Note |
|---|---|---|
| `addFirst`, `addLast` | O(1) | tail pointer is why `addLast` is not O(n) |
| `removeFirst`, `removeLast` | O(1) | |
| `get`, `set`, `add(index,…)`, `remove(index)` | O(min(i, n−i)) | walk starts from the nearer end |
| `indexOf`, `contains`, `removeValue` | O(n) | |
| `reverse` | O(n) | in place, O(1) extra space |
| `iterator().remove()` | O(1) | the node is already in hand |
| space | O(n) | 1 value + 2 references per node |

The `O(min(i, n−i))` walk is the payoff of keeping `prev` pointers: `get(n-1)`
touches one node rather than n.

### 2.5 Iterators

Two iterators are provided, both hand-written:

- `iterator()` — head to tail, supports `remove()`.
- `descendingIterator()` — tail to head, only possible because the list is doubly linked.

Both are **fail-fast**: the list keeps a `modCount` that every structural change
increments, and an iterator that sees an unexpected `modCount` throws
`ConcurrentModificationException` instead of silently walking a broken chain.
`Iterator.remove()` updates its own expected count, so removing *through* the
iterator is legal while removing behind its back is not.

```
before iterator.remove(): [1, 2, 3, 4, 5, 6, 7, 8]
after removing evens:     [1, 3, 5, 7]
modifying the list mid-walk: ConcurrentModificationException, as designed
```

### 2.6 Invariants

The class documents five invariants and `invariantsHold()` re-checks them by
walking the chain in both directions:

| | Invariant |
|---|---|
| I1 | `size` equals the number of nodes reachable from `head` via `next` |
| I2 | `head == null` ⟺ `tail == null` ⟺ `size == 0` |
| I3 | `head.prev == null` and `tail.next == null` |
| I4 | for every node n with `n.next != null`: `n.next.prev == n` |
| I5 | no node holds a null value |

Every mutating test scenario asserts `invariantsHold()` afterwards, so a
structural bug fails a test rather than surfacing later as a lost node.

---

## 3. Algorithms

### 3.1 Selection sort

```
for i = 0 to n-2
    minIndex = i
    for j = i+1 to n-1
        if a[j] < a[minIndex] then minIndex = j
    if minIndex != i then swap(a[i], a[minIndex])
```

**Invariant.** Before pass i, positions `0..i-1` hold the i smallest elements in
ascending order, and each is ≤ every element in `i..n-1`. Pass i selects the
minimum of the suffix and places it at i, which re-establishes the invariant for
i+1. After pass n−2 the whole array is sorted.

**Cost.** The inner loop runs (n−1)+(n−2)+…+1 = n(n−1)/2 times *regardless of the
input order*: best = average = worst = **O(n²)**. Swaps are at most n−1, i.e. at
most 3(n−1) element writes — the fewest of any elementary sort. Space O(1).
**Not stable**: swapping a distant minimum can jump one equal key over another.

### 3.2 Insertion sort

```
for i = 1 to n-1
    key = a[i]
    j = i-1
    while j >= 0 and a[j] > key
        a[j+1] = a[j]
        j = j-1
    a[j+1] = key
```

**Invariant.** Before pass i, positions `0..i-1` contain the first i input
elements in ascending order. The shift loop opens a gap at the correct position
and the key is written into it, so `0..i` is sorted afterwards. Only shifts and
one write occur, so the array stays a permutation of the input.

**Cost.** Best case **O(n)** — a sorted input does one comparison per pass and no
shifts. Worst case (reverse order) and average are **O(n²)**, about n²/4
comparisons on random data. Space O(1). **Stable**: the shift loop stops at the
first element that is ≤ key, so equal keys never cross. It is **adaptive** — cost
is proportional to the number of inversions.

### 3.3 Both sorts run directly on the linked list

Each class also has a `sort(MyLinkedList<T>, SortMetrics)` overload that never
copies to an array:

- **Selection:** one iterator pass finds the minimum of the remaining nodes, that
  node is unlinked and appended to the result.
- **Insertion:** each element is walked into the already-sorted result with an
  iterator and spliced in with `add(position, value)` — the insert itself is a
  relink, so on a linked list insertion sort performs **no element shifting at
  all**; the cost is purely the search for the position.

This is the version demonstrated live, because it shows the structure and the
algorithm working together.

---

## 4. Correctness evidence

### 4.1 Selection sort trace — input `[42, 7, 19, 3, 25, 11]`

```
Pass | i | minIndex | swap      | array after pass
-----+---+----------+-----------+------------------
   - | - |        - | start     | [42 7 19 3 25 11]
   1 | 0 |        3 | 42<->3    | [3 7 19 42 25 11]
   2 | 1 |        1 | none      | [3 7 19 42 25 11]
   3 | 2 |        5 | 19<->11   | [3 7 11 42 25 19]
   4 | 3 |        5 | 42<->19   | [3 7 11 19 25 42]
   5 | 4 |        4 | none      | [3 7 11 19 25 42]
```

Five passes, 15 comparisons (= 6·5/2), 3 swaps. Note passes 2 and 5 find the
minimum already in place and still pay the full scan — the reason the sorted-input
row in the measurements below is no cheaper than the random one.

### 4.2 Insertion sort trace — same input

```
Pass | i | key | shifts | array after pass
-----+---+-----+--------+------------------
   - | - |   - |      - | [42 7 19 3 25 11]
   1 | 1 |   7 |      1 | [7 42 19 3 25 11]
   2 | 2 |  19 |      1 | [7 19 42 3 25 11]
   3 | 3 |   3 |      3 | [3 7 19 42 25 11]
   4 | 4 |  25 |      1 | [3 7 19 25 42 11]
   5 | 5 |  11 |      3 | [3 7 11 19 25 42]
```

11 comparisons against selection sort's 15 on the same input, and the shift count
per pass is exactly the number of inversions that key had.

Both traces are regenerated by `SelectionSort.trace(int[])` and
`InsertionSort.trace(int[])`, so the tables in this report cannot drift from the
code.

### 4.3 Unit tests

175 assertions across three suites, all passing. No JUnit jar is checked in, so
the suites are plain runnable classes that print one line per assertion and exit
non-zero on failure.

| Suite | Assertions | Result |
|---|---|---|
| `MyLinkedListTest` | 100 | 100 passed, 0 failed |
| `SelectionSortTest` | 36 | 36 passed, 0 failed |
| `InsertionSortTest` | 39 | 39 passed, 0 failed |

Coverage by category, as the brief requires:

**Normal** — insertion order preserved; middle insert; removal by value and by
index; `indexOf`/`contains`; `set`; `reverse`; forward and backward iteration;
`Iterator.remove`; both sorts on random, sorted, reverse, duplicate, negative and
string data; both sorts on the linked list itself.

**Boundary** — empty list; single element; insert at index 0 and at index `size`;
draining a list to empty and reusing it; `clear`; reverse of empty and of one
element; index walks from both halves; empty array; one element; two elements;
all-equal elements; `Integer.MAX_VALUE`/`MIN_VALUE`; and the exact operation
counts predicted by the analysis — selection sort n(n−1)/2 comparisons on both
sorted and reversed input, insertion sort n−1 on sorted and n(n−1)/2 on reversed.

**Invalid input** — null element rejected on every insertion path
(`IllegalArgumentException`); index below 0, at `size` and beyond
(`IndexOutOfBoundsException`); `removeFirst`/`getFirst` on an empty list
(`NoSuchElementException`); iterating past the end; structural modification
during iteration (`ConcurrentModificationException`); `Iterator.remove()` before
`next()` and twice in a row (`IllegalStateException`); null array, null metrics,
null element inside a `Comparable[]`, null list and null trace input; sorting
non-Comparable elements. Every rejection case also asserts that the list was left
unchanged.

**Stability** is tested explicitly: five requests, three sharing deadline 5, must
come out as `[REQ-4@1, REQ-2@2, REQ-1@5, REQ-3@5, REQ-5@5]` — original order
preserved among ties, for both the array and the linked-list version of insertion
sort.

### 4.4 Reproducing

```bash
javac -d bin src/main/java/structures/MyLinkedList.java src/main/java/algorithms/sorting/*.java src/main/java/demo/*.java src/test/java/structures/MyLinkedListTest.java src/test/java/algorithms/sorting/SelectionSortTest.java src/test/java/algorithms/sorting/InsertionSortTest.java
java -cp bin MyLinkedListTest
java -cp bin SelectionSortTest
java -cp bin InsertionSortTest
java -cp bin demo.LinkedListSortDemo
java -cp bin demo.SortBenchmark
```

---

## 5. Performance analysis

### 5.1 Method

- **Machine:** Intel Core i5-10310U @ 1.70 GHz, 4 cores / 8 threads, 15.7 GB RAM,
  Windows 11 (amd64), OpenJDK 21.0.8. Every experiment ran on this machine.
- **Distributions:** random, already sorted, reverse sorted, nearly sorted.
- **Sizes:** 350 → 11 200, doubling (6 steps per distribution).
- **Repeats:** 4 per configuration, averaged; one warm-up run is discarded first
  so the JIT has compiled the sort loop before anything is timed.
- **Fairness:** both algorithms sort an identical copy of the same generated array.
- **Verification:** every timed run is checked with `isSorted` before its result
  is recorded, so a wrong answer cannot be reported as a fast one.
- **Counters:** comparisons and moves are counted as well as time. They are
  deterministic and are what the O(n²) analysis actually predicts, whereas
  wall-clock time on a laptop is noisy.

**Parameters derived from the member index number** (brief requirement, ≥3).
Index number **22146178**:

| Parameter | Derivation | Value |
|---|---|---|
| RNG seed | `INDEX_NUMBER` | 22 146 178 |
| repeats per configuration | `3 + (INDEX_NUMBER % 3)` | 4 |
| base input size | `100 + (INDEX_NUMBER % 7) × 50` | 350 |
| nearly-sorted disorder | `1 + (INDEX_NUMBER % 5)` | 4 % of positions swapped |

The generator is a hand-written LCG seeded from that number, so the same index
number reproduces the same arrays on any machine.

**Output files** — `results/csv/otoo_sorts_raw.csv` (192 individual runs),
`results/csv/otoo_sorts_summary.csv` (48 averaged configurations, plot these),
`results/csv/otoo_algorithm_runs.csv` (48 rows shaped like the `algorithm_runs`
table in `db/schema.sql`, for the database owner to load).

### 5.2 Results — average of 4 runs, time in milliseconds

**Random input**

| n | Selection (ms) | Insertion (ms) | Selection comparisons | Insertion comparisons |
|---:|---:|---:|---:|---:|
| 350 | 1.228 | 1.025 | 61 075 | 32 304 |
| 700 | 1.492 | 2.466 | 244 650 | 124 191 |
| 1 400 | 2.673 | 2.215 | 979 300 | 491 506 |
| 2 800 | 9.133 | 5.036 | 3 918 600 | 1 995 931 |
| 5 600 | 37.468 | 18.451 | 15 677 200 | 7 905 226 |
| 11 200 | 125.191 | 62.946 | 62 714 400 | 31 382 613 |

**Already sorted input**

| n | Selection (ms) | Insertion (ms) | Selection comparisons | Insertion comparisons |
|---:|---:|---:|---:|---:|
| 350 | 0.167 | 0.002 | 61 075 | 349 |
| 700 | 0.711 | 0.004 | 244 650 | 699 |
| 1 400 | 2.112 | 0.010 | 979 300 | 1 399 |
| 2 800 | 7.422 | 0.016 | 3 918 600 | 2 799 |
| 5 600 | 32.048 | 0.019 | 15 677 200 | 5 599 |
| 11 200 | 123.494 | 0.040 | 62 714 400 | 11 199 |

**Reverse sorted input (insertion sort's worst case)**

| n | Selection (ms) | Insertion (ms) | Selection moves | Insertion moves |
|---:|---:|---:|---:|---:|
| 350 | 0.136 | 0.179 | 525 | 61 424 |
| 700 | 0.498 | 0.464 | 1 050 | 245 349 |
| 1 400 | 2.125 | 1.875 | 2 100 | 980 699 |
| 2 800 | 8.129 | 7.498 | 4 200 | 3 921 399 |
| 5 600 | 34.810 | 31.356 | 8 400 | 15 682 799 |
| 11 200 | 170.834 | 123.897 | 16 800 | 62 725 599 |

**Nearly sorted input (4 % of positions swapped)**

| n | Selection (ms) | Insertion (ms) | Selection comparisons | Insertion comparisons |
|---:|---:|---:|---:|---:|
| 350 | 0.114 | 0.007 | 61 075 | 3 451 |
| 700 | 0.405 | 0.033 | 244 650 | 14 887 |
| 1 400 | 1.594 | 0.103 | 979 300 | 53 803 |
| 2 800 | 6.481 | 0.353 | 3 918 600 | 196 291 |
| 5 600 | 24.495 | 1.309 | 15 677 200 | 806 827 |
| 11 200 | 97.380 | 5.159 | 62 714 400 | 3 076 239 |

`memoryKb` is 0 in every row of the summary file: both sorts are in place, so the
measured heap does not grow with n. That is the O(1) auxiliary space claim,
confirmed rather than asserted.

### 5.3 Interpretation

1. **Both are quadratic, and the counters prove it exactly.** Selection sort's
   comparison count is 61 075 at n = 350 and 62 714 400 at n = 11 200 — n grew ×32,
   comparisons grew ×1 027 ≈ 32². Times follow: 9.13 ms → 37.47 ms when n doubles
   from 2 800 to 5 600, a factor of 4.1.

2. **Selection sort is completely blind to input order.** Its comparison count is
   identical — 62 714 400 at n = 11 200 — for random, sorted, reversed and nearly
   sorted input. It cannot benefit from data that is already in order, and on the
   sorted case it is roughly **3 100× slower** than insertion sort (123.5 ms vs
   0.040 ms at n = 11 200).

3. **Insertion sort is adaptive, and that is what matters here.** Comparisons on
   nearly-sorted input (3.08 million at n = 11 200) are ~10× below its own
   random-input figure (31.4 million) and ~20× below the 62.7 million a full n²
   scan costs, because the cost tracks the number of inversions, not n². At
   n = 11 200 it finishes nearly-sorted input in 5.2 ms against selection sort's
   97.4 ms — a **19× gap**.

4. **Selection sort's one genuine advantage is write count.** On reverse-sorted
   input at n = 11 200 it performs 16 800 moves against insertion sort's
   62 725 599 — about **3 700× fewer writes**. If a write were expensive (records
   moved on disk, or an audit entry per move) that would flip the choice, even
   though selection sort loses on time here.

5. **The small-n rows are noise-dominated.** At n = 700 on random input selection
   sort is *faster* (1.49 ms vs 2.47 ms) despite doing twice the comparisons
   (244 650 vs 124 191) — the opposite of the ordering at every larger size. At
   that scale JIT warm-up and OS scheduling swamp the algorithm, which is
   precisely why comparison counts are reported alongside time and why the small
   sizes are not used to draw conclusions.

### 5.4 Graphs to export

From `results/csv/otoo_sorts_summary.csv`, into `results/graphs/`:

1. `avgTimeMs` vs `inputSize`, one line per algorithm, **random** distribution — the headline O(n²) curves.
2. `avgTimeMs` vs `inputSize`, one line per algorithm, **nearly sorted** — insertion sort flat, selection sort quadratic.
3. `comparisons` vs `inputSize` for insertion sort, one line per distribution — the adaptivity result.

---

## 6. Responsible algorithm selection

**Where these two are the right choice.** Small n and simple code. A dispatcher
re-orders the ~20 requests in one truck's round, or slots a handful of newly
logged requests into a list already in deadline order. Insertion sort touches
almost nothing there, needs no extra memory, and is short enough to be audited
by hand — which matters when the output decides whose street is collected first.
Selection sort is the right choice when writes are the expensive operation rather
than comparisons.

**Where they are the wrong choice.** Anything approaching the full dataset. On
11 200 random records selection sort spends 125 ms and 62.7 million comparisons;
merge sort would do the same work in roughly 11 200·log₂(11 200) ≈ 151 000
comparisons — about 415× fewer. The 300-row `service_requests.csv` is already past the point
where a quadratic sort is defensible if it runs on every dispatch cycle. For that
path the project uses the O(n log n) sorts (`MergeSort`/`QuickSort`, Dennis Kumi
Lartey's module).

**Where sorting is the wrong tool entirely.** If only the single most urgent
request is needed, sorting the whole list is wasted work — a priority queue
(Derrick Debrah's module) answers that in O(log n) per operation. And ordering by
"urgency" is a modelling decision, not a neutral one: the sort faithfully executes
whatever priority rule the dataset encodes, so if that rule under-weights a
neighbourhood, the algorithm will reproduce that bias efficiently and invisibly.
The choice of sort key belongs in the ethics note, not just in the code.

---

## 7. Individual contribution statement

**Emmanuel Thisara Otoo** — designed and implemented `MyLinkedList` (doubly
linked, from scratch, no built-in Java collections) including both iterators with
fail-fast modification detection, in-place reversal, and a runtime invariant
check; implemented Selection Sort and Insertion Sort over primitive arrays,
`Comparable` arrays and the linked list itself, with instrumentation for
comparisons, moves and elapsed time; wrote 175 unit assertions covering normal,
boundary and invalid-input cases including stability and exact operation counts;
built the trace generators used for the correctness tables and the pointer-diagram
demo used in the video and defense; designed and ran the Selection vs Insertion
performance experiment (4 distributions × 6 sizes × 4 repeats = 192 timed runs,
48 averaged records exported in `algorithm_runs` format); and authored this report
section.

### Oral defense preparation

Questions to be ready for, with the evidence to point at:

1. *Why a linked list rather than an array?* → §2.2 diagram: O(1) relink versus
   O(n) shift. And the honest counter: `get(i)` is O(n), so if the workload were
   random access I would have chosen the dynamic array.
2. *Why keep `prev` pointers?* → `descendingIterator`, O(1) `removeLast`, and the
   `O(min(i, n−i))` walk. Cost: one extra reference per node.
3. *What breaks if `modCount` is removed?* → an iterator can walk into an unlinked
   node and silently return stale data; the test
   `modifyingDuringIterationDetected` is what pins that down.
4. *Why is insertion sort stable and selection sort not?* → the shift loop stops
   at the first element ≤ key (`> 0`, strictly), so equal keys never cross;
   selection sort swaps a distant minimum over them. Demonstrate with the
   `REQ-@5` stability test.
5. *Insertion sort is O(n²), so why use it at all?* → the nearly-sorted table:
   5.2 ms vs 97.4 ms at n = 11 200, because cost tracks inversions.
6. *When would you pick selection sort?* → 3 700× fewer writes on reverse input;
   choose it when writes cost more than comparisons.
7. *Why does the n = 700 random row contradict the comparison counts?* → §5.3
   point 5: at that size the measurement is noise, which is why counters are
   reported too.
8. *Live trace request* → run `demo.LinkedListSortDemo`; the trace tables are
   generated from the same code that is being defended.
