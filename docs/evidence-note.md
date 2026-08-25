# AI-resistance / localisation evidence note
AI-Resistance & Localisation Evidence Note
Project: Ghana Waste Routing DSA (ghana-waste-routing-dsa)

Component: Database Schema, CSV Loader & Data Integrity Engine

1. Localisation & Context-Specific Engineering
Generic AI coding models default to standardized Western routing paradigms or boilerplate SQL schemas. This project requires custom implementation tailored to local Ghanaian infrastructural constraints:

Regional Field Schema: The dataset schema includes Ghanaian-specific location parameters (e.g., area, coordinate bounds specific to local municipalities, and category hierarchies adapted to local waste management contexts).

Road Condition Weighting: Standard routing models assume fixed speeds based on road classification. Our implementation enforces custom multi-variable weightings (road_condition_weight, travel_time_min) to account for unpaved roads, traffic patterns, and local terrain variability.

Strict Foreign Key Validation: Cross-referencing relational validation in DatabaseLoader.java strictly enforces localized relational integrity (from_location_id and to_location_id against active locations table IDs) prior to database insertion.

2. Technical Evidence of Human-Led Engineering (AI-Resistance)
While generic AI tools can generate static syntax, they fail on nuanced transactional state management, specialized CSV parsing edge cases, and project-specific constraint enforcement:

Transaction Safety & Recovery:

AI Flaw: Off-the-shelf AI code generation typically commits line-by-line or forgets to restore auto-commit modes upon encountering validation exceptions.

Engineered Fix: Implemented connection.setAutoCommit(false) with explicit finally block protection to guarantee connection.setAutoCommit(initialAutoCommit) executes even during runtime failures.

Custom In-Memory Validation Engine:

Custom verification logic catches edge cases that generic AI tools pass through, such as enforcing deadline strictly after time_submitted, validating domain-specific category enums (Medical, Security, Cleaning, Transport), and enforcing non-negative capacity/urgency bounds (1–5).

Detailed Failure Log Accumulation:

Rather than abruptly throwing exceptions or swallowing parsing errors silently, the LoadResult class records line-specific failure logs (line X: <error> -- <raw_data>), allowing valid records to proceed while tracking bad data rows for auditing.

3. Execution Evidence & Verification
Unit Test Suite Integrity: 19 out of 19 test cases pass (0 failures), covering normal scenarios, boundary cases (e.g., zero-distance roads, boundary urgency levels), and invalid input rejection (e.g., non-existent locations L999, negative capacities, invalid timestamps).

Database Commit Verification: Verified via docs/evidence-run-log.txt, confirming exact record loads across all seed tables:

locations = 52

roads = 104

service_requests = 309

resources = 30
Short note explaining how the dataset was obtained or constructed from local
knowledge (Ghana waste/sanitation context) without exposing personal data.
Required by the brief (Section 2).

## Service requests 1–100 — Wiafe Franklin Asare

**What these records are.** Synthetic service-request records built to a
documented rule set. They are not observed municipal data and are not presented
as such. What is genuinely local is the structure they encode: the collection
points are real Greater Accra suburbs, the disposal sites are real transfer
stations and landfills serving the area, each suburb is mapped to the facility
that actually serves it, and the daily rhythm reflects how collection runs —
early-morning crew starts, a late-afternoon second peak, heavier Wednesday and
Saturday market days, a lighter Sunday shift.

**Why the rules matter more than the rows.** Every field is derived from a
stated rule rather than filled in arbitrarily, so any row can be justified on
demand and the whole file can be regenerated identically:

- `destination` is the nearest disposal facility to `source` — a fixed mapping,
  not a random draw, so requests from one suburb always route to one site.
- `category` depends on the kind of location (market, high-density, residential,
  commercial).
- `urgency` depends on public-health risk carried by the category, never on who
  reported it. Medical waste is never below `HIGH`.
- `deadline = timeSubmitted + SLA`, with SLA fixed per urgency
  (CRITICAL 4 h, HIGH 12 h, MEDIUM 48 h, LOW 120 h).
- `status` follows how long the request had been waiting at a single snapshot
  instant, 2026-06-14 18:00.

Full rule set and the resulting distributions: `report/queue-section.md` §6.

**Personal data.** None, by construction. Every column is an integer ID, a term
from a controlled vocabulary, or a timestamp. There are no names, addresses,
phone numbers or free-text fields anywhere in the file — a request is tied to a
collection point, never to a person or a household.

**Still to reconcile.** These rows assume `locationId` 1–45 are collection points
and 46–50 are disposal facilities. The `locations` table is owned by Desmond Kimi
Bilabia and Kelvin Amaah Mankata; if their final IDs differ, only the `source`
and `destination` columns need remapping.
---

## Abdul-Aziz Naeem — Search & Array Test Data

All test data used for `DynamicArray`, `LinearSearch`, and `BinarySearch`
is synthetically generated — no real persons, real addresses, or real
service-request IDs are used.

**How the data was constructed:**

The benchmark arrays are produced by a hand-written Linear Congruential
Generator (LCG) seeded from my member index number (7). This makes every
run fully reproducible on any machine. The four distributions used are:

- **Random**: integers in the range [0, 10n), representing arbitrary service-
  request urgency scores or location IDs, as you might encounter in a real
  dispatch system.
- **Sorted**: ascending even integers, simulating a pre-sorted route list
  (e.g. location IDs assigned in order of registration).
- **Reversed**: descending even integers, simulating a worst-case for
  algorithms that expect ascending order (e.g. newly inserted high-priority
  requests dominating the front of the queue).
- **Nearly sorted**: a sorted array with 7% of positions randomly swapped,
  reflecting real-world data that has been mostly ordered but has a few
  recent late entries inserted in the wrong position.

The unit test values (10, 20, 30, etc.) are hand-chosen small integers with
no connection to personal data. The labels "A", "B", "C" used in string
tests are arbitrary single-character identifiers.

No GPS coordinates, names, phone numbers, or any other personal or sensitive
data appear anywhere in the codebase for this module.
