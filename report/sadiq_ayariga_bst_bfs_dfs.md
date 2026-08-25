# Defense Report: Binary Search Tree (BST), BFS & DFS

**Module Owner:** Sadiq Moro Ayariga  
**Data Structures & Algorithms Implemented:** Custom Binary Search Tree (`structures/BST.java`), Breadth-First Search & Depth-First Search (`algorithms/graph/BFSDFS.java`).

---

## 1. Implementation Overview

All components have been implemented from scratch using primitive arrays and custom classes without relying on `java.util` collections.

### Key Operations & Complexity
* **BST Insertion (`insert(key, value)`)**: $O(h)$ time, where $h$ is tree height ($O(\log n)$ average, $O(n)$ worst-case).
* **BST Search (`search(key)`)**: $O(h)$ time complexity.
* **BST Inorder Traversal (`getSortedInorderKeys()`)**: $O(n)$ time complexity, returns keys in strictly sorted ascending order.
* **BFS Graph Traversal (`bfs(graph, startId)`)**: $O(V + E)$ time complexity using custom queue (`structures/MyQueue`).
* **DFS Graph Traversal (`dfs(graph, startId)`)**: $O(V + E)$ time complexity using recursive call stack.

---

## 2. Search Path & Traversal Trace Evidence

### BST Search Path Output Example
When querying key `40` in a BST populated with `[50, 30, 70, 20, 40, 60, 80]`:
```text
Path: 50 -> 30 -> 40 (Found)
```

### Sorted Inorder Traversal Output
```text
[20, 30, 40, 50, 60, 70, 80]
```

### BFS Traversal Trace Table
Starting from Depot Vertex `V1`:

| Step | Visited Vertex | Queue Frontier | Action |
|---|---|---|---|
| 1 | V1 | head -> [2, 3] <- tail (size=2) | Dequeued V1, Enqueued [2, 3] |
| 2 | V2 | head -> [3, 4] <- tail (size=2) | Dequeued V2, Enqueued [4] |
| 3 | V3 | head -> [4] <- tail (size=1) | Dequeued V3, Enqueued [] |
| 4 | V4 | head -> [] <- tail (size=0) | Dequeued V4, Enqueued [] |

---

## 3. Unit Test Verification Matrix

All 28 test cases passed cleanly under automated test execution (`BSTTest` & `BFSDFSTest`):

| Category | Test Name | Target Behavior | Result |
|---|---|---|---|
| **Normal** | `testInsertAndSearchNormal` | Inserts multiple nodes and verifies retrieval | PASS |
| **Normal** | `testInorderSortedOutput` | Verifies BST inorder outputs sorted array | PASS |
| **Normal** | `testBFSNormal` | Verifies BFS traversal sequence from root | PASS |
| **Normal** | `testDFSNormal` | Verifies DFS deep traversal sequence | PASS |
| **Boundary** | `testEmptyTree` | Handles empty tree operations gracefully | PASS |
| **Boundary** | `testSingleNode` | Single-element BST insertion, search, and delete | PASS |
| **Invalid Input** | `testInsertNullKey` | Throws `IllegalArgumentException` on null key insert | PASS |
| **Invalid Input** | `testBFSInvalidStartNode` | Throws `IllegalArgumentException` on missing start vertex | PASS |

---

## 4. Live Defense Checklist

- [x] BST insert, search, delete, and inorder traversal implemented from scratch.
- [x] BFS and DFS graph search algorithms fully implemented.
- [x] Search path logs and trace tables generated.
- [x] Unit tests covering normal, boundary, and invalid inputs verified.
