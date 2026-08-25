# AI-resistance / localisation evidence note

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
