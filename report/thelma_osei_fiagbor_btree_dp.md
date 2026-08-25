# Defense Report: B-Tree & Dynamic Programming

**Module Owner:** Thelma Osei-Fiagbor  
**Data Structures & Algorithms Implemented:** Custom B-Tree (`structures/BTree.java`), 0/1 Knapsack Dynamic Programming (`algorithms/dp/KnapsackDP.java`).

---

## 1. Implementation Overview

### B-Tree Implementation
Implemented a multi-way search tree with minimum degree $T = 2$ (2-3-4 tree):
- **Node Capacity**: Each node contains at most $2T - 1 = 3$ keys and $2T = 4$ children.
- **Node Splitting**: When inserting into a full node, it splits around the median key into two child nodes of size $T - 1 = 1$, pushing the median key up to the parent.
- **Complexity**: $O(\log n)$ for search and insert.

### 0/1 Knapsack Dynamic Programming
Optimizes waste service request selection for garbage collection trucks under a weight budget capacity:
- **Tabulation**: $O(N \times W)$ time and space complexity, building a 2D dynamic programming grid.

---

## 2. B-Tree Split Explanation & Search Trace

### Node Split Log
When inserting keys `[1, 2, 3, 4]`:
```text
Splitting node with keys [1, 2, 3] around median key 2
Parent receives key 2; left child [1], right child [3, 4]
```

### B-Tree Search Trace Example
Searching for key `10` in B-Tree:
```text
Search trace: [Node keys: 2] -> Down child 1 -> [Node keys: 10, 20] -> Found 10
```

---

## 3. Dynamic Programming Memoization / Tabulation Table

Selection of 3 waste collection requests under a **50 kg** truck load budget:
- Request 1: 10 kg, Priority 60
- Request 2: 20 kg, Priority 100
- Request 3: 30 kg, Priority 120

### 2D DP Tabulation Matrix

| Item / Cap | 0 kg | 10 kg | 20 kg | 30 kg | 40 kg | 50 kg |
|---|---|---|---|---|---|---|
| **Item 0 (Init)** | 0 | 0 | 0 | 0 | 0 | 0 |
| **Item 1 (10kg/60)** | 0 | 60 | 60 | 60 | 60 | 60 |
| **Item 2 (20kg/100)**| 0 | 60 | 100 | 160 | 160 | 160 |
| **Item 3 (30kg/120)**| 0 | 60 | 100 | 160 | 180 | **220** |

**Optimal Selection Result:** Requests `REQ02` + `REQ03` yielding a maximum urgency priority value of **220**.

---

## 4. Unit Test Verification Matrix

All 21 unit tests passed (`BTreeTest` & `KnapsackDPTest`):

| Category | Test Name | Target Behavior | Result |
|---|---|---|---|
| **Normal** | `testInsertAndSearchNormal` | Inserts keys into B-Tree and tests search across nodes | PASS |
| **Normal** | `testSplitLog` | Verifies node split logging on full node insertion | PASS |
| **Normal** | `testSolveKnapsackNormal` | Computes optimal knapsack selection & tabulation grid | PASS |
| **Boundary** | `testEmptyTree` | Handles search on empty B-Tree | PASS |
| **Boundary** | `testZeroCapacityBoundary` | Returns 0 selected items when truck capacity is 0 kg | PASS |
| **Invalid Input** | `testInsertNullKey` | Throws `IllegalArgumentException` on null key | PASS |
| **Invalid Input** | `testInvalidNegativeCapacity` | Throws `IllegalArgumentException` on negative capacity | PASS |

---

## 5. Live Defense Checklist

- [x] Custom B-Tree with node splitting implemented from scratch.
- [x] 0/1 Knapsack DP algorithm implemented with tabulation table generation.
- [x] Node split explanation logs and search trace recorded.
- [x] Unit test suite covering normal, boundary, and invalid cases verified.
