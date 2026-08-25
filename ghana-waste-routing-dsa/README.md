# Ghana Smart Service Operations Optimizer — Waste/Sanitation Routing

DCIT 204/308 — Data Structures and Algorithms I & II — Joint DSA Semester Project

## Context
Municipal waste/sanitation routing for a local Ghanaian environment: service
requests, collection points, truck routes, and priority areas.

## Repo layout
```
src/main/java/structures/     custom data structures (13 required, no built-in collections)
src/main/java/algorithms/     search, sorting, greedy, dp, graph algorithms
src/main/java/db/             database schema + CSV loader
src/main/java/menu/           console menu (run everything without editing code)
src/main/java/Main.java       entry point
src/test/java/                mirrored unit tests (normal/boundary/invalid input per module)
db/schema.sql                 database schema
db/seed/                      seed CSVs with headers (fill in your rows here)
data/                         working copies of seed data for local runs
results/csv/                  raw performance timing data
results/graphs/                exported performance graphs
report/                       report structure + notes (draft in shared doc, export here)
docs/dev-log.md               weekly progress log
docs/evidence-note.md         AI-resistance / localisation evidence note
```

## Module ownership
See `docs/ownership.md` for who owns which structure, algorithm, and dataset slice.

## Running
1. `psql`/`sqlite3` — run `db/schema.sql` to create tables.
2. Load seed data: run `db.DatabaseLoader` (reads CSVs from `db/seed/`).
3. Run `Main` to launch the console menu.

## Getting started (per person)
1. Clone the repo.
2. Find your file(s) under `src/main/java/structures/` and/or `algorithms/` —
   each has a header comment with your name and TODOs.
3. Implement from scratch — built-in Java collections (HashMap, TreeMap,
   PriorityQueue, Stack, ArrayDeque, etc.) are **not allowed** for assessed core logic.
4. Add your unit tests in the matching `src/test/java/` file (normal, boundary,
   invalid input).
5. Add your trace table + explanation to the report.
6. Commit and push regularly — small commits, clear messages.

## How to Compile & Run Unit Tests

### Command Line (All Platforms - Windows / Mac / Linux)

1. **Compile all Java files into `bin/` directory**:
   ```bash
   javac -d bin src/main/java/structures/*.java src/main/java/algorithms/*/*.java src/test/java/structures/*.java src/test/java/algorithms/*/*.java


## Constraints (from the brief)
- Built-in Java utilities ARE allowed for: file reading, printing, JDBC/DB
  support, plotting export, unit-test scaffolding.
- Every custom structure needs tests for normal case, boundary case, invalid input.
- Run each performance experiment ≥3 times and report the average.
- Same machine for all experiments — state the machine spec in the report.
