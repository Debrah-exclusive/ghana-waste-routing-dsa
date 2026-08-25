# Defense Report: Red-Black Tree & MST Algorithms

**Module Owner:** Adam Mohammed  
**Data Structures & Algorithms Implemented:** Red-Black Tree (`structures/RedBlackTree.java`), Prim's & Kruskal's MST Algorithms (`algorithms/graph/PrimKruskal.java`).

---

## 1. Implementation Overview

### Red-Black Tree (RBT)
A balanced Binary Search Tree guaranteeing $O(\log n)$ height by enforcing color properties:
- **Insert (`insert(key, value)`)**: $O(\log n)$ time with automatic rebalancing (`leftRotate`, `rightRotate`, `insertFixup`).
- **Search (`search(key)`)**: $O(\log n)$ time complexity.

### Minimum Spanning Tree (MST) Algorithms
- **Prim's Algorithm (`prim(graph, startId)`)**: $O(E \log V)$ time using custom `PriorityQueueHeap`.
- **Kruskal's Algorithm (`kruskal(graph)`)**: $O(E \log E)$ time using custom `DisjointSet` (Union-Find) with path compression and rank optimization.

---

## 2. Tree Diagram & Rotation Evidence

### Before / After Rotation Visualization

#### Initial Unbalanced State (Insertion of 10, 20, 30):
```text
10 (R)
 └── 20 (R)
      └── 30 (R)   <-- Violation: Consecutive Red Nodes
```

#### Rebalanced State via Left-Rotate around 10:
```text
Rotation Triggered: Left-Rotate around 10

└── 20 (B)
    ├── 10 (R)
    └── 30 (R)
```

---

## 3. MST Edge List & Total Cost Comparison

Running Prim's & Kruskal's MST algorithms on the campus network graph:

### Kruskal's MST Edge List
```text
=== Kruskal's Algorithm MST Edges ===
V1 - V2 (1.00 km)
V3 - V4 (2.00 km)
V1 - V3 (3.00 km)
Total MST Cost: 6.00 km
```

### Prim's MST Edge List
```text
=== Prim's Algorithm MST Edges ===
V1 - V2 (1.00 km)
V1 - V3 (3.00 km)
V3 - V4 (2.00 km)
Total MST Cost: 6.00 km
```
*Both algorithms produce the exact optimal total cost of **6.00 km**.*

---

## 4. Unit Test Verification Matrix

All 22 unit tests passed (`RedBlackTreeTest` & `PrimKruskalTest`):

| Category | Test Name | Target Behavior | Result |
|---|---|---|---|
| **Normal** | `testInsertAndSearchNormal` | Inserts elements into RBT and verifies logarithmic search | PASS |
| **Normal** | `testRotationLog` | Verifies left/right rotations logged during rebalancing | PASS |
| **Normal** | `testPrimMSTNormal` | Validates Prim's algorithm produces minimum cost MST | PASS |
| **Normal** | `testKruskalMSTNormal` | Validates Kruskal's algorithm produces minimum cost MST | PASS |
| **Boundary** | `testEmptyTree` | Handles empty RBT operations without throwing errors | PASS |
| **Boundary** | `testDuplicateInsert` | Correctly updates value on duplicate key insertion | PASS |
| **Invalid Input** | `testInsertNullKey` | Throws `IllegalArgumentException` on null key insert | PASS |
| **Invalid Input** | `testKruskalNullGraph` | Throws `IllegalArgumentException` on null graph input | PASS |

---

## 5. Live Defense Checklist

- [x] Red-Black Tree implemented from scratch with rotation mechanisms.
- [x] Prim's & Kruskal's MST algorithms implemented and verified against graph.
- [x] Rotation logs and ASCII tree diagrams generated.
- [x] Total MST costs computed and validated.
