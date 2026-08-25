# Intelligent Campus Waste Management & Dispatch Routing System

**Course:** DCIT 204 / DCIT 308 — Data Structures & Algorithms I & II (Joint Semester Project)  
**Institution:** University of Ghana, Legon — Department of Computer Science  
**Academic Year:** 2025/2026  

---

##  Executive Summary for Project Evaluation

This repository contains the complete implementation and technical documentation for the **Intelligent Campus Waste Management & Dispatch Routing System**, developed for the University of Ghana, Legon campus environment.

The system addresses critical municipal sanitation challenges:
- **Skip Overflows & Waste Accumulation**: High-density campus locations (e.g., Night Market `L009`, Sarbah Park `L014`).
- **Drainage Blockages**: Emergency dispatch for blocked campus drains prior to rainstorms to prevent flash flooding.
- **Truck Routing & Capacity Optimization**: Shortest path computation across 52 location nodes (`L001` - `L052`) and 104 weighted road segments (`roads.csv`).

### Strict Pedagogical Constraints Adhered To
1. **Zero External Java Collections**: All 15 data structures (LinkedList, Queue, Circular Queue, Deque, Stack, Priority Queue Heap, BST, Red-Black Tree, B-Tree, Hash Table, Set/Map ADTs, Disjoint Set, Adjacency Matrix Graph, Adjacency List Graph) were built **100% from scratch** using primitive arrays and object node references without importing `java.util` collections (`ArrayList`, `HashMap`, `PriorityQueue`, `TreeSet`, `LinkedList`, etc.).
2. **Empirical Benchmarking & Correctness**: 29 unit test runners covering 126 test cases with **100% PASS** rates. Benchmarking was performed across scaling inputs ($N = 100 \dots 10,000$).
3. **Embedded Database Persistence**: Full SQLite database integration (`waste_routing.db`) with normalized schema and automated seed loading (`DatabaseLoader.java`).

---

##  Team Roster & Module Ownership Matrix (15 Members)

| ID | Student Name | Custom Data Structure Owned | Algorithm / Dataset Owned | Project Role |
| :--- | :--- | :--- | :--- | :--- |
| **1** | Ivan Kwamena Johnson | Graph (Adjacency List) | Dijkstra's Algorithm | Route Engine Lead |
| **2** | Elsie Atsu | Graph (Adjacency Matrix) | Roads Dataset (104 edges) | Network Matrix |
| **3** | Emmanuel Aseda Kow Bentsil | Disjoint Set (Union-Find) | Roads Dataset (104 edges) | MST Connectivity |
| **4** | Wiafe Franklin Asare | Queue & Circular Ring Queue | Service Requests Dataset (200 records) | Dispatch Buffers |
| **5** | Able Mwintuma Gambo | Deque (Double-Ended Queue) | Service Requests Dataset | Urgent Override Queue |
| **6** | Hannah Aidoo | Resources Dataset (30 assets) | Database Schema & Loader | SQLite Database Lead |
| **7** | Abdul-Aziz Naeem | Dynamic Array | Linear & Binary Search | Array & Search Engine |
| **8** | Emmanuel Thisara Otoo | Doubly Linked List | Selection & Insertion Sort | LinkedList & Sorts |
| **9** | Dennis Kumi Lartey | Stack | Merge Sort & QuickSort | Recursion & Sorting |
| **10** | Derrick Debrah | Priority Queue Heap | Priority Greedy Assignment | Greedy Dispatch Lead |
| **11** | Sadiq Moro Ayariga | Binary Search Tree (BST) | BFS & DFS Graph Search | BST & Traversals |
| **12** | Adam Mohammed | Red-Black Tree (RBT) | Prim's & Kruskal's MST | Balanced Trees & MST |
| **13** | Thelma Osei-Fiagbor | B-Tree ($T=2$) | 0/1 Knapsack DP | Multi-way Tree & DP |
| **14** | Desmond Kimi Bilabia | Hash Table (Chaining) | Locations Dataset (52 nodes) | Hash Table & Collisions |
| **15** | Kelvin Amaah Mankata | Set & Map ADTs | Locations Dataset (52 nodes) | Membership & Map ADT |

---

##  Repository Layout

```text
ghana-waste-routing-dsa/
├── src/
│   ├── main/java/
│   │   ├── algorithms/          # Search, Sort, Dijkstra, BFS/DFS, Prim/Kruskal, Greedy, 0/1 Knapsack DP
│   │   ├── db/                  # SQLite schema manager & DatabaseLoader CSV parser
│   │   ├── menu/                # Interactive console CLI menu interfaces
│   │   ├── structures/          # 15 custom data structures built from scratch
│   │   └── Main.java            # Main console application entry point
│   └── test/java/               # 29 test runners (normal, boundary, invalid input tests)
├── data/                        # Active CSV datasets (locations.csv, roads.csv, service_requests.csv, resources.csv)
├── db/                          # Database schema DDL (schema.sql)
├── report/                      # Comprehensive Final Technical Report submitted for evaluation
│   ├── FINAL_TECHNICAL_REPORT.md
│   └── FINAL_TECHNICAL_REPORT.docx
├── results/
│   ├── charts/                  # Plotted empirical benchmark comparison PNG charts
│   └── csv/                     # Raw performance timing logs & trace outputs
└── README.md                    # Lecturer Evaluation Guide & Build Instructions
```

---

##  Lecturer Quick-Start & Evaluation Instructions

### 1. Prerequisites
- **Java Development Kit (JDK 17 or higher)** installed and available in environment path.
- **SQLite JDBC Driver** (`sqlite-jdbc.jar` located in `lib/`).

---

### 2. Compilation (Single Command)
To compile all source classes and unit test runners into the `bin/` directory:

#### Windows (Command Prompt / PowerShell):
```cmd
javac -d bin -cp "lib/*;src/main/java;src/test/java" src/main/java/structures/*.java src/main/java/algorithms/*/*.java src/main/java/db/*.java src/main/java/menu/*.java src/main/java/Main.java src/test/java/structures/*.java src/test/java/algorithms/*/*.java src/test/java/db/*.java
```

#### Linux / macOS:
```bash
javac -d bin -cp "lib/*:src/main/java:src/test/java" $(find src -name "*.java")
```

---

### 3. Database Seeding & Verification
To create and seed the embedded SQLite database (`waste_routing.db`) with 52 locations, 104 roads, 200 service requests, and 30 resource assets:

#### Windows:
```cmd
java -cp "bin;lib/*" db.DatabaseLoader
```

#### Linux / macOS:
```bash
java -cp "bin:lib/*" db.DatabaseLoader
```

---

### 4. Interactive Console Application
To launch the interactive system CLI console for truck dispatching, route calculation, and sorting:

#### Windows:
```cmd
java -cp "bin;lib/*" Main
```

#### Linux / macOS:
```bash
java -cp "bin:lib/*" Main
```

---

### 5. Automated Unit Test Verification (29 Test Runners)
To execute individual test suites or run all 29 test runners:

#### Sample Individual Test Commands:
```cmd
java -cp "bin;lib/*" algorithms.graph.DijkstraTest
java -cp "bin;lib/*" structures.BSTTest
java -cp "bin;lib/*" db.DatabaseLoaderTest
```

---

##  Technical Report & Deliverables

The final submission documents and empirical benchmarking charts are located in the `report/` and `results/` directories:

-  **Final Technical Report (Markdown)**: [`report/FINAL_TECHNICAL_REPORT.md`](report/FINAL_TECHNICAL_REPORT.md)
-  **Final Technical Report (Word DOCX)**: [`report/FINAL_TECHNICAL_REPORT.docx`](report/FINAL_TECHNICAL_REPORT.docx)
-  **Benchmark Graphs**: [`results/charts/`](results/charts/)
