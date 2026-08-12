# Queue & Circular Queue

**Author:** Wiafe Franklin Asare
**Module:** Data structure — Queue & Circular Queue; Dataset — service requests 1–100
**Source files:** `src/main/java/structures/QueueCircular.java`, `src/main/java/structures/MyQueue.java`
**Evidence:** `src/test/java/structures/QueueCircularTest.java`, `MyQueueTest.java`, `QueueTraceDemo.java`,
`report/queue-trace.md`, `results/csv/queue_front_rear_trace.csv`

This section slots into the main report as part of §5 (data-structure implementation),
§7 (correctness evidence) and §3 (dataset description).

---

## 1. Why a queue belongs in this system

A waste-collection dispatcher receives service requests continuously and serves
them with a bounded number of trucks. Two properties of that job map directly
onto a queue:

- **Arrival order is the fair default.** Two households that report an
  overflowing bin on the same street with the same urgency should be served in
  the order they reported. First-in-first-out encodes that fairness with no
  extra bookkeeping.
- **A shift has a bounded backlog.** A depot dispatching a fixed fleet only
  holds so many requests "in hand" at once. A fixed-capacity buffer models the
  real constraint, and refusing an enqueue is meaningful information: the depot
  is saturated and the request must be deferred to the next shift.

FIFO is not the whole dispatch policy — see §9 on where it is the wrong choice.

Two implementations are provided because the system needs both shapes:

| | `QueueCircular` | `MyQueue` |
|---|---|---|
| Backing store | fixed array reused as a ring | singly linked nodes |
| Capacity | fixed (optionally doubling) | unbounded |
| Per-item cost | no allocation, contiguous memory | one `Node` object per item |
| Overflow | rejected — a real signal | cannot happen |
| Used for | bounded per-shift dispatch buffer | BFS/DFS frontier, full-day backlog |

---

## 2. `QueueCircular` — design

### 2.1 Index model

Three fields describe the whole state:

- `front` — index of the request that will leave next
- `rear` — index of the request that arrived last
- `size` — how many requests are held

Both indices only ever move **forward**, wrapping from `capacity - 1` back to
`0`. Physically the array is straight; logically the last slot is glued to the
first, which is what makes it a *ring*.

```
   physical array (capacity = 5)

     slot 0    slot 1    slot 2    slot 3    slot 4
   +---------+---------+---------+---------+---------+
   |         |         |         |         |         |
   +---------+---------+---------+---------+---------+
        ^                                        |
        |________________________________________|
                    rear wraps back here
```

A fresh queue starts at `front = 0`, `rear = capacity - 1`. That looks odd until
you see the rule behind it: **when the queue is empty, `rear` sits exactly one
slot behind `front`**. Parking it at `capacity - 1` means the very first
`enqueue` advances it to slot `0`, with no special case in the code.

### 2.2 Class invariant

After every public operation:

```
    rear == (front + size - 1) mod capacity
    the `size` slots starting at `front` are non-null
    every other slot is null
```

The first line is the load-bearing one. It says the occupied region is exactly
the run of `size` slots that starts at `front` and ends at `rear`, walking
forward with wrap-around. `checkInvariant()` implements it, and the unit tests
assert it after every mutation, so a bad index is caught at the operation that
broke it rather than several steps later.

Note the invariant is *total* — it holds when the queue is empty too, where it
reduces to `rear == (front - 1) mod capacity`, the parked position.

### 2.3 Telling full from empty

This is the classic difficulty with a ring buffer. When the array is completely
full, `rear` is one slot behind `front`. When it is completely empty, `rear` is
*also* one slot behind `front`. The indices alone cannot distinguish the two
states. Three standard fixes exist:

1. **Keep an explicit `size` counter** — used here.
2. Sacrifice one slot, so "full" means one gap remains. Costs a slot and makes
   `capacity` a lie.
3. Keep a `wasLastOpEnqueue` flag. Cheapest in space, easiest to get wrong.

Option 1 was chosen because `size()` is needed by callers anyway (a dispatcher
asking how deep the backlog is), it makes `isFull()`/`isEmpty()` trivially
correct, and it costs one `int` for the whole structure rather than one slot per
queue.

### 2.4 Operations

```
enqueue(item):
    if item is null              -> reject (IllegalArgumentException)
    if size == capacity:
        if not growable          -> reject (IllegalStateException, overflow)
        else                     -> grow()
    rear <- (rear + 1) mod capacity
    slots[rear] <- item
    size <- size + 1

dequeue():
    if size == 0                 -> reject (IllegalStateException, underflow)
    item  <- slots[front]
    slots[front] <- null                 // release the reference
    front <- (front + 1) mod capacity
    size  <- size - 1
    return item
```

Two API styles are offered, matching what an examiner will recognise from
`java.util`: `enqueue`/`dequeue` throw on overflow/underflow, while
`offer`/`poll` report failure by return value. A rejected operation leaves
`front`, `rear`, `size` and the array **completely unchanged** — asserted by the
test `invalidRejectedEnqueueLeavesQueueUntouched`.

`slots[front] = null` on dequeue is deliberate: without it the array keeps a
reference to a served request forever, and the object cannot be collected. The
test `normalClearResetsToStartingState` checks that no stale references survive
a `clear()`.

### 2.5 Complexity

| Operation | Time | Space | Note |
|---|---|---|---|
| `enqueue` | O(1) | O(1) | one modulo, one store |
| `dequeue` | O(1) | O(1) | one modulo, one clear |
| `peek`, `peekRear` | O(1) | O(1) | |
| `size`, `isEmpty`, `isFull` | O(1) | O(1) | counter read |
| `contains` | O(n) | O(1) | linear scan of the live region |
| `toArray` | O(n) | O(n) | |
| `clear` | O(capacity) | O(1) | nulls every slot |
| `grow` (growable only) | O(n) once, **amortised O(1)** | O(n) | doubling |

Whole structure: O(capacity) space, with no per-item allocation.

The wrap costs nothing extra. `(rear + 1) mod capacity` is constant work whether
or not it rolls over, which is exactly why the ring beats the alternative of
shifting every element down by one on dequeue (O(n) per dequeue).

**Amortised argument for the growable variant.** Doubling from capacity `c`
copies `c` items, but only after `c` enqueues have happened since the previous
doubling. Charging each enqueue 2 units — one for its own insert, one banked
towards the eventual copy — pays for every copy in advance, so `n` enqueues cost
O(n) in total and O(1) each on average.

---

## 3. Wrap-around handling

Wrap-around is the whole point of the structure, so it is worth stating
precisely what happens.

When `rear` sits on the last slot and a request arrives, `(rear + 1) mod
capacity` evaluates to `0` and `rear` moves to the front of the array — into
slots that earlier `dequeue` calls freed. The queue's *logical* order is
unaffected: it is still `front`, `front+1`, … walking forward with wrap. Only
the physical layout is split.

Step 10 of the trace, where this has happened:

```
     slot 0    slot 1    slot 2    slot 3    slot 4
   +---------+---------+---------+---------+---------+
   |   R6    |   R7    |   R3    |   R4    |   R5    |
   +---------+---------+---------+---------+---------+
                  ^         ^
               rear = 1   front = 2

   logical order:  R3 -> R4 -> R5 -> R6 -> R7
   invariant:      rear == (2 + 5 - 1) mod 5 == 6 mod 5 == 1   ✓
```

`rear` is *numerically behind* `front` here, and that is perfectly legal. Any
code that assumes `front <= rear` is wrong for a circular queue; that assumption
is what the `size` counter exists to replace.

### 3.1 Why circular — measured, not asserted

`QueueTraceDemo` runs the same 18-step sequence against the circular queue and
against a plain non-circular array queue whose `rear` advances but never wraps.
Both have 5 slots. Full tables in `report/queue-trace.md`.

| | Circular queue | Non-circular array queue |
|---|---|---|
| Requests accepted | **7** | 5 |
| Requests served | **7** | 5 |
| Slots permanently wasted | **0** | 5 |
| State after step 14 | working normally | empty, yet cannot accept anything |

The non-circular queue reports *full* from step 7 onward while holding only 4,
then 3, then 2 items — the **false overflow** problem. By step 14 it holds
nothing at all and still refuses every enqueue, because its `rear` has run off
the end and has no way back. Its usable capacity degrades to zero after one pass
through the array. The circular queue reuses every freed slot and, with the same
5 slots, handled 7 requests and finished healthy.

For the dispatcher this is the difference between a depot buffer that works all
day and one that jams after the first 5 requests.

---

## 4. `MyQueue` — the unbounded companion

Same FIFO contract, different trade-off. A singly linked chain with `head` and
`tail` pointers gives O(1) enqueue and dequeue with no capacity ceiling, at the
cost of one `Node` object per item and no memory locality.

Invariant:

```
    size == 0  <=>  head == null  <=>  tail == null
    size >  0  =>   tail.next == null, and the chain from head is exactly `size` long
```

The dangerous case is dequeuing the *last* item: `head` becomes null but a
careless implementation leaves `tail` pointing at the removed node, so the next
enqueue links onto a node that is no longer in the queue. `dequeue()` therefore
nulls `tail` whenever `head` becomes null, and `boundarySingleItem` tests
exactly this.

**When to use which.** Use `QueueCircular` when the bound is real and refusing
work is meaningful (per-shift dispatch buffer). Use `MyQueue` when the size is
genuinely unknown and refusing work would be wrong — the BFS frontier over the
road network, where the frontier size depends on graph structure discovered at
run time.

---

## 5. Correctness evidence

### 5.1 Unit tests

Plain Java, no external test framework, so they run on a bare JDK. Both suites
exit non-zero on failure.

| Suite | Assertions | Result |
|---|---|---|
| `QueueCircularTest` | 142 | all passed |
| `MyQueueTest` | 83 | all passed |
| **Total** | **225** | **all passed** |

Coverage against the brief's three required categories:

**Normal cases** — FIFO order preserved across enqueue/dequeue; `size` tracks
contents; `peek`/`peekRear` do not remove; indices advance exactly one slot per
operation; interleaved enqueue/dequeue (trucks collecting while new reports
arrive); `contains`/`toArray`; `clear` resets to the starting state.

**Boundary cases** — empty queue (`peek`, `poll`, `toArray` on nothing);
capacity-1 ring, where every operation touches the single slot; filling to
*exactly* capacity; `rear` wrapping 4 → 0; `front` wrapping 4 → 0; 1000
enqueue/dequeue cycles on a 4-slot ring, lapping the array ~250 times with FIFO
order and the invariant checked every iteration; drain-then-refill across four
laps; growth while the contents are physically split across the array end —
the copy must re-lay them in *logical* order, not slot order; 5000 items into
the linked queue.

**Invalid input** — capacity `0` and `-3` rejected at construction;
`enqueue(null)` and `offer(null)` rejected; `dequeue()` on an empty queue and on
a drained queue both throw underflow; `enqueue` into a full fixed queue throws
overflow; a rejected enqueue leaves the queue byte-for-byte unchanged;
`contains(null)` returns false rather than throwing; `offer`/`poll` report
failure by return value instead of throwing.

Every test that mutates a queue also asserts `checkInvariant()`.

### 5.2 Trace table

Full front/rear movement trace: **`report/queue-trace.md`** (also
`results/csv/queue_front_rear_trace.csv`). It is generated, not hand-written —
re-run `java -cp bin QueueTraceDemo` to refresh it. Abridged:

| Step | Operation | front | rear | size | Backing array | What moved |
|---|---|---|---|---|---|---|
| 0 | initialise | 0 | 4 | 0 | `[-- -- -- -- --]` | rear parked one slot behind front |
| 5 | `enqueue(R5)` | 0 | 4 | 5 | `[R1 R2 R3 R4 R5]` | queue now full |
| 6 | `enqueue(R6)` | 0 | 4 | 5 | `[R1 R2 R3 R4 R5]` | **rejected**, indices hold still |
| 7 | `dequeue()` | 1 | 4 | 4 | `[-- R2 R3 R4 R5]` | front steps forward, slot 0 freed |
| 8 | `dequeue()` | 2 | 4 | 3 | `[-- -- R3 R4 R5]` | front steps forward, slot 1 freed |
| 9 | `enqueue(R6)` | 2 | 0 | 4 | `[R6 -- R3 R4 R5]` | **WRAP**: rear 4 → 0, reuses freed slot |
| 10 | `enqueue(R7)` | 2 | 1 | 5 | `[R6 R7 R3 R4 R5]` | rear → 1, full again |
| 11 | `enqueue(R8)` | 2 | 1 | 5 | `[R6 R7 R3 R4 R5]` | **rejected**, genuinely full |
| 14 | `dequeue()` | 0 | 1 | 2 | `[R6 R7 -- -- --]` | **WRAP**: front 4 → 0 |
| 16 | `dequeue()` | 2 | 1 | 0 | `[-- -- -- -- --]` | last request served, empty |
| 17 | `dequeue()` | 2 | 1 | 0 | `[-- -- -- -- --]` | **rejected**, underflow |

Counters after the run: 7 enqueues accepted, 7 dequeues served, 2 wrap-arounds,
2 enqueues rejected, invariant holds.

The trace is loaded with **real records** — R1…R8 are `requestId` 1–8 from
`data/service_requests.csv`, in arrival order — so the structure section and the
dataset section describe the same data.

### 5.3 A defect this evidence caught

The first version of `wrapArounds()` counted a wrap on the *first* enqueue into
any empty queue. Because `rear` is parked at `capacity - 1` when empty, the
first insert rolls it to `0`, which looks like a wrap but carries no live data
over the array end. The boundary test `boundaryRearWrapsAround` — which asserts
zero wraps after filling and partially draining, then exactly one after the real
wrap — failed and exposed it. The counter now only counts a roll that moves an
index over live data. Worth stating in the defence: the test found a real bug,
and the fix was to the structure, not to the test.

---

## 6. Dataset — service requests 1–100

### 6.1 Slice and ownership

The brief requires ≥300 service requests, split three ways. This section covers
**`requestId` 1–100**, written to `db/seed/service_requests.csv` and
`data/service_requests.csv` (identical files; `db/seed/` is the load source,
`data/` the working copy). Records 101–200 and 201–300 belong to Able Mwintuma
Gambo and Hannah Aidoo respectively; all three slices share the schema below and
append to the same files.

### 6.2 Data dictionary

| Column | Type | Domain | Meaning |
|---|---|---|---|
| `requestId` | INTEGER | 1–100 in this slice | Primary key. Also the arrival sequence number. |
| `source` | INTEGER | 1–45 | `locationId` of the collection point that raised the request. |
| `destination` | INTEGER | 46–50 | `locationId` of the transfer station or landfill the load goes to. |
| `category` | TEXT | 10 values, §6.3 | Type of sanitation work. |
| `urgency` | TEXT | `LOW`, `MEDIUM`, `HIGH`, `CRITICAL` | Public-health priority. |
| `timeSubmitted` | TEXT | ISO-8601 `YYYY-MM-DDThh:mm` | When the request was reported. |
| `deadline` | TEXT | ISO-8601 `YYYY-MM-DDThh:mm` | Service-level target, derived from `urgency`. |
| `status` | TEXT | `PENDING`, `ASSIGNED`, `IN_PROGRESS`, `COMPLETED` | State at the snapshot instant. |

Matches `db/schema.sql` exactly; `source` and `destination` are foreign keys into
`locations(locationId)`.

### 6.3 Construction rules

Every field follows a stated rule rather than being filled in arbitrarily, so
any row can be justified on demand.

**Location convention.** IDs 1–45 are collection points across the Greater Accra
area (Accra Central, Makola, Kaneshie, Dansoman, Osu, Labadi, Achimota, Lapaz,
Nima, Mamobi, Madina, Adenta, Dome, Kwabenya, Taifa, Amasaman, Teshie, Nungua,
Tema Communities, Ashaiman and others). IDs 46–50 are disposal facilities:
Achimota Transfer Station (46), Teshie Transfer Station (47), Kpone Sanitary
Landfill (48), Nsumia Engineered Landfill (49), Adjen Kotoku Landfill (50).

> **To reconcile:** the `locations` table is owned by Desmond Kimi Bilabia and
> Kelvin Amaah Mankata. This 1–45 / 46–50 split is the convention this slice was
> built on and needs confirming against their final `locations.csv` before the
> loader runs. If their IDs differ, only the two integer columns need remapping.

**Destination is not random.** Each collection point is assigned its nearest
facility, so every request from a given suburb routes to the same disposal site
— which is how municipal collection actually works, and what makes the routing
algorithms meaningful. Nima, Madina and Adenta go north to Nsumia; Kaneshie,
Dansoman and Achimota go to Achimota; Tema and Ashaiman go to Kpone.

**Category depends on location kind.** Markets generate `MARKET_WASTE` and
`SKIP_OVERFLOW`; high-density areas generate more `PUBLIC_TOILET_SERVICE` and
`DRAIN_CLEARING`; residential areas are dominated by `HOUSEHOLD_WASTE`,
`BULK_REFUSE` and `SEPTIC_EMPTYING`; commercial areas add `STREET_SWEEPING` and
the occasional `MEDICAL_WASTE`.

**Urgency depends on category, not on the reporter.** Public-health risk drives
priority: `MEDICAL_WASTE` is `CRITICAL` or `HIGH` and never lower; overflowing
public toilets and blocked drains skew high; routine household collection skews
`LOW`/`MEDIUM`.

**Deadline is derived, never invented:** `deadline = timeSubmitted + SLA`, where
SLA is CRITICAL 4 h, HIGH 12 h, MEDIUM 48 h, LOW 120 h.

**Arrival times** span seven operating days, 2026-06-08 (Mon) to 2026-06-14
(Sun), weighted towards the 06:00–08:00 crew start and a second late-afternoon
peak, with a lighter Sunday shift and heavier Wednesday/Saturday market days.

**Status** follows how long a request had been waiting at the snapshot instant
of 2026-06-14 18:00 — recent requests are still `PENDING`/`ASSIGNED`, older ones
have worked through to `COMPLETED`.

### 6.4 Profile of the 100 records

| Dimension | Distribution |
|---|---|
| Window | 2026-06-08 05:05 → 2026-06-14 16:46 (7 days) |
| Distinct source locations | 40 of 45 |
| Urgency | MEDIUM 42, LOW 36, HIGH 17, CRITICAL 5 |
| Status | COMPLETED 74, IN_PROGRESS 16, ASSIGNED 8, PENDING 2 |
| Destination | Nsumia 30, Achimota 28, Teshie 28, Adjen Kotoku 9, Kpone 5 |
| Top categories | HOUSEHOLD_WASTE 35, BULK_REFUSE 13, SKIP_OVERFLOW 10, SEPTIC_EMPTYING 10 |
| Long tail | ILLEGAL_DUMP_CLEARANCE 9, DRAIN_CLEARING 9, PUBLIC_TOILET_SERVICE 5, MARKET_WASTE 4, STREET_SWEEPING 4, MEDICAL_WASTE 1 |

### 6.5 Integrity checks

Verified over all 100 rows:

- `requestId` is unique, contiguous 1–100, no gaps.
- **`requestId` order is identical to `timeSubmitted` order.** This matters for
  this module specifically: it means the CSV can be fed straight into the queue
  and the FIFO output *is* chronological order, with no sort step in between.
- Every `source` is in 1–45; every `destination` is in 46–50.
- `deadline > timeSubmitted` on every row, and the gap equals the SLA for that
  urgency.
- No free-text fields, no names, no addresses, no phone numbers — every column
  is an ID, a controlled vocabulary term, or a timestamp.

### 6.6 Provenance

These are **synthetic records built to a documented rule set**, not observed
municipal data. Suburb names, the collection-point-to-facility mapping, the
category vocabulary and the crew-hours pattern come from the real geography and
operating practice of waste collection in the Greater Accra area; the individual
rows are generated. They contain no personal data of any kind, by construction.
See `docs/evidence-note.md` for the full statement.

---

## 7. Reproducing everything

From the repository root:

```bash
# compile the structures, the tests and the trace demo
javac -d bin src/main/java/structures/*.java src/test/java/structures/*.java

# unit tests (exit status 0 = all passed)
java -cp bin QueueCircularTest
java -cp bin MyQueueTest

# regenerate the trace table and CSV from the real dataset
java -cp bin QueueTraceDemo
```

`QueueTraceDemo` reads `data/service_requests.csv` and rewrites
`report/queue-trace.md` and `results/csv/queue_front_rear_trace.csv`.

---

## 8. Integration notes for the team

- `QueueCircular<T>` and `MyQueue<T>` are generic; `QueueCircular<Integer>`
  works for location IDs in BFS, `QueueCircular<String>` for request labels.
  When a `ServiceRequest` model class exists, `QueueCircular<ServiceRequest>`
  needs no change.
- For **BFS** (Sadiq Moro Ayariga): use `MyQueue<Integer>` for the frontier —
  the frontier size is not known in advance, and a fixed-capacity queue would
  need a capacity of |V| to be safe, which defeats the point.
- For the **console menu** (Ivan Kwamena Johnson): `toString()` on both classes
  prints front-to-rear contents with the indices, which is enough for a demo
  screen. `QueueTraceDemo` can be called from a menu option as-is.
- Both classes reject `null`, so a `null` returned by `poll()`/`peek()`
  unambiguously means "empty" and never "an item that happened to be null".

---

## 9. Oral defence preparation

**Why a queue rather than the priority queue for dispatch?**
FIFO is the fair default *within* an urgency class, and this system uses it that
way: requests of the same urgency are served in arrival order. Across urgency
classes, FIFO alone would be irresponsible — a `CRITICAL` medical-waste request
must not wait behind 40 routine household collections just because they arrived
first. That is Derrick Debrah's priority queue. The honest positioning for
report §10 is: **queue for fairness within a class, heap for triage across
classes.** Pure FIFO is the wrong tool wherever the cost of waiting differs
between items.

**Why does `rear` start at `capacity - 1` rather than `-1` or `0`?**
So that "empty" and "non-empty" obey the same rule: `rear` is always one slot
behind `front` modulo capacity when the queue is empty. `-1` would need a
special case in `enqueue`; `0` would make the first enqueue overwrite slot 0
while `rear` still pointed at it. This choice makes the invariant total, which
is what lets `checkInvariant()` run after *every* operation including on an
empty queue.

**How do you distinguish full from empty, when the indices look the same?**
An explicit `size` counter — see §2.3, including the two alternatives
(sacrificing a slot, or a last-operation flag) and why the counter wins here.

**Why is enqueue still O(1) when it wraps?**
`(rear + 1) mod capacity` is one arithmetic operation whether or not it rolls
over. Nothing is shifted, copied or re-indexed. Contrast with a naive array
queue that keeps `front` at 0 by shifting every element down on dequeue: that is
O(n) per dequeue, O(n²) to drain the queue.

**Show me the wrap-around.**
Step 9 of `report/queue-trace.md`: `rear` moves 4 → 0 with `front` at 2, so the
contents are physically split (`[R6 -- R3 R4 R5]`) while the logical order stays
`R3 R4 R5 R6`. The invariant still holds at that step:
`rear == (front + size - 1) mod capacity == (2 + 4 - 1) mod 5 == 0`. ✓

**What happens when it overflows?**
`enqueue` throws `IllegalStateException`; `offer` returns `false`. Either way the
queue is left completely unchanged — no partial write, no index movement. In
domain terms an overflow means the depot buffer is saturated and the request
must be deferred, which is real information, not an error to swallow.

**Why reject `null`?**
So that `poll()` returning `null` means "empty" and nothing else. Allowing null
elements would make the return value ambiguous.

**What does the growable mode cost?**
O(n) for the one doubling operation, amortised O(1) per enqueue — argument in
§2.5. The subtle part is that a growing queue may be *wrapped*, so `grow()` must
copy in logical order starting from `front`, not in slot order. Copying in slot
order would silently scramble FIFO order; `boundaryGrowablePreservesOrderAcrossWrap`
is the test that pins this down.

**Which test would fail first if I broke your `mod`?**
`checkInvariant()` fires inside almost every test, so `boundaryRearWrapsAround`
or the 1000-cycle lap test would fail immediately. That is by design — the
invariant is checked after every mutation rather than only at the end.
