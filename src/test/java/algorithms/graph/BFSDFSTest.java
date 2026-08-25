package algorithms.graph;

import structures.Graph;

/**
 * Unit tests for BFS & DFS traversals — normal case, boundary case, invalid input case.
 * Plain Java standalone test runner for live defense evidence.
 * Owner: Sadiq Moro Ayariga
 */
public class BFSDFSTest {

    private static int total = 0;
    private static int passed = 0;

    public static void main(String[] args) {
        runAllTests();
        System.out.println("\n========== BFSDFSTest SUMMARY ==========");
        System.out.println(passed + " / " + total + " tests passed");
        if (passed != total) {
            System.out.println((total - passed) + " TEST(S) FAILED");
            System.exit(1);
        } else {
            System.out.println("ALL TESTS PASSED");
        }
    }

    public static void runAllTests() {
        testBFSNormal();
        testDFSNormal();
        testBFSInvalidStartNode();
        testDFSNullGraph();
    }

    private static void assertTrue(String name, boolean condition) {
        total++;
        if (condition) {
            passed++;
            System.out.println("  [PASS] " + name);
        } else {
            System.err.println("  [FAIL] " + name);
        }
    }

    private static void assertEquals(String name, Object expected, Object actual) {
        total++;
        boolean match = (expected == null && actual == null) || (expected != null && expected.equals(actual));
        if (match) {
            passed++;
            System.out.println("  [PASS] " + name);
        } else {
            System.err.println("  [FAIL] " + name + " - Expected: " + expected + ", Actual: " + actual);
        }
    }

    private static Graph buildGraph() {
        Graph graph = new Graph();
        graph.addVertex(1, "Depot");
        graph.addVertex(2, "Station A");
        graph.addVertex(3, "Station B");
        graph.addVertex(4, "Station C");

        graph.addEdge(1, 2, 5.0);
        graph.addEdge(1, 3, 3.0);
        graph.addEdge(2, 4, 2.0);
        return graph;
    }

    private static void testBFSNormal() {
        Graph g = buildGraph();
        BFSDFS.TraversalResult res = BFSDFS.bfs(g, 1);
        assertEquals("BFS Order Length", 4, res.order.length);
        assertEquals("BFS Start Node", 1, res.order[0]);
        assertTrue("BFS Trace Table Content", res.traceTable.contains("Frontier"));
    }

    private static void testDFSNormal() {
        Graph g = buildGraph();
        BFSDFS.TraversalResult res = BFSDFS.dfs(g, 1);
        assertEquals("DFS Order Length", 4, res.order.length);
        assertEquals("DFS Start Node", 1, res.order[0]);
        assertTrue("DFS Trace Table Content", res.traceTable.contains("Visited V1"));
    }

    private static void testBFSInvalidStartNode() {
        Graph g = buildGraph();
        boolean caught = false;
        try {
            BFSDFS.bfs(g, 999);
        } catch (IllegalArgumentException e) {
            caught = true;
        }
        assertTrue("BFS Invalid Start Throws Exception", caught);
    }

    private static void testDFSNullGraph() {
        boolean caught = false;
        try {
            BFSDFS.dfs(null, 1);
        } catch (IllegalArgumentException e) {
            caught = true;
        }
        assertTrue("DFS Null Graph Throws Exception", caught);
    }
}
