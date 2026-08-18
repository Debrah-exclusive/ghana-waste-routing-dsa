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
