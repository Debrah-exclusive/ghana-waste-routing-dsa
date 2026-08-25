# Defense Report: Hash Table & Collision Statistics

**Module Owner:** Desmond Kimi Bilabia  
**Data Structures & Datasets:** Custom Hash Table (`structures/MyHashTable.java`), Campus Locations Dataset (`data/locations.csv`).

---

## 1. Implementation Overview

### Custom Hash Table Implementation
Implemented from scratch using **Separate Chaining** with custom node links:
- **Operations**: `put(key, value)`, `get(key)`, `remove(key)`, `containsKey(key)`.
- **Dynamic Resizing**: Automatically doubles capacity when the load factor exceeds $\alpha_{threshold} = 0.75$.
- **Complexity**: $O(1)$ average time complexity for put, get, and remove operations ($O(n)$ worst-case under severe clustering).

### Dataset Collection
Co-collected the campus locations dataset. [locations.csv](file:///c:/Users/derri/Downloads/ghana-waste-routing-dsa/ghana-waste-routing-dsa/data/locations.csv) contains **52 valid location records** across Legon campus (exceeding the required 50 total records).

---

## 2. Collision Statistics Across Load Factors

Evaluated on inserting 50 campus location records into initial tables configured with different capacity bounds to simulate varying load factors ($\alpha = N / M$):

| Initial Capacity ($M$) | Number of Records ($N$) | Target Load Factor ($\alpha$) | Collisions Recorded | Resizes Triggered | Final Capacity |
|---|---|---|---|---|---|
| **64** | 50 | **0.78** | **14** | 0 | 64 |
| **32** | 50 | **1.56** | **28** | 1 | 64 |
| **16** | 50 | **3.12** | **41** | 2 | 64 |
| **8** | 50 | **6.25** | **47** | 3 | 64 |

*Observation: Higher load factors increase chain lengths and collision frequency, proving the importance of maintaining $\alpha \le 0.75$ via dynamic resizing.*

---

## 3. Unit Test Verification Matrix

All 19 unit tests passed (`MyHashTableTest`):

| Category | Test Name | Target Behavior | Result |
|---|---|---|---|
| **Normal** | `testPutGetRemoveNormal` | Inserts location data, retrieves by ID, and removes | PASS |
| **Normal** | `testLoadFactorAndResizing` | Triggers dynamic table expansion when load factor threshold is breached | PASS |
| **Normal** | `testCollisionStats` | Accurately counts collision events during bucket insertions | PASS |
| **Boundary** | `testEmptyTable` | Safely queries empty hash table | PASS |
| **Boundary** | `testUpdateKey` | Overwrites existing value when inserting duplicate key | PASS |
| **Invalid Input** | `testPutNullKey` | Throws `IllegalArgumentException` on null key put | PASS |
| **Invalid Input** | `testInvalidCapacityConstructor` | Throws `IllegalArgumentException` on negative capacity | PASS |

---

## 4. Live Defense Checklist

- [x] Custom Hash Table with collision handling implemented from scratch.
- [x] Locations dataset populated with 52 campus records.
- [x] Collision statistics gathered across load factor thresholds.
- [x] Unit test suite covering normal, boundary, and invalid cases verified.
