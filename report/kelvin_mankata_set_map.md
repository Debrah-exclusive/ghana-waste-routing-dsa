# Defense Report: Set & Map ADTs

**Module Owner:** Kelvin Amaah Mankata  
**Data Structures & Datasets:** Custom Set & Map ADTs (`structures/SetMap.java`), Campus Locations Dataset (`data/locations.csv`).

---

## 1. Implementation Overview

### Custom Set & Map Implementations
Implemented `MySet<E>` and `SetMap<K, V>` on top of `MyHashTable`:
- **`MySet<E>`**: Encapsulates unique keys, guaranteeing no duplicate elements. Supported operations: `add(E)`, `contains(E)`, `remove(E)`, `size()`, `toArray()`.
- **`SetMap<K, V>`**: Key-Value mapping ADT providing `put(K, V)`, `get(K)`, `remove(K)`, `containsKey(K)`, `keySet()`.
- **Complexity**: $O(1)$ average time complexity for membership check and map lookup.

### Dataset Integration
Co-collected the remaining 25 campus location records to complete the [locations.csv](file:///c:/Users/derri/Downloads/ghana-waste-routing-dsa/ghana-waste-routing-dsa/data/locations.csv) dataset (52 total records).

---

## 2. Membership & Lookup Use-Case Demonstration

### Use-Case 1: Campus Waste Service Location Membership (`MySet`)
Determining if a requested collection point is a registered Legon campus location:
```text
Set populated with Location IDs: [L001, L002, L003, ..., L052]

Lookup("L002") -> TRUE (Computer Science Department)
Lookup("L999") -> FALSE (Unregistered / Invalid Location ID)
```

### Use-Case 2: Location ID -> Name Lookup (`SetMap`)
Mapping location codes to full campus names for waste truck route dispatch:
```text
Map Put: ("L001", "Balme Library")
Map Put: ("L013", "University of Ghana Medical Centre")

get("L013") -> "University of Ghana Medical Centre"
keySet() -> ["L001", "L002", ..., "L052"] (Returns Set of unique keys)
```

---

## 3. Unit Test Verification Matrix

All 22 unit tests passed (`SetMapTest`):

| Category | Test Name | Target Behavior | Result |
|---|---|---|---|
| **Normal** | `testSetAddAndContains` | Adds elements to set, checks membership, rejects duplicates | PASS |
| **Normal** | `testMapOperations` | Puts key-values, retrieves values, extracts keySet | PASS |
| **Boundary** | `testEmptySetAndMap` | Verifies behavior of empty set/map queries | PASS |
| **Invalid Input** | `testSetAddNull` | Throws `IllegalArgumentException` on null set insertion | PASS |
| **Invalid Input** | `testSetContainsNull` | Returns `false` on null element membership check | PASS |
| **Invalid Input** | `testMapPutNullKey` | Throws `IllegalArgumentException` on null key put | PASS |

---

## 4. Live Defense Checklist

- [x] Custom Set & Map ADTs built on top of Hash Table.
- [x] Dataset of 52 campus locations fully integrated.
- [x] Membership check and lookup use cases demonstrated.
- [x] Unit test suite covering normal, boundary, and invalid cases verified.
