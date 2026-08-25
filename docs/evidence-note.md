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
