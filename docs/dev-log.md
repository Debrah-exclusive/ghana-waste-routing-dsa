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
