package algorithms.graph;

import structures.Graph;

/**
 * Unit tests for Prim's & Kruskal's MST algorithms — normal case, boundary case, invalid input case.
 * Plain Java standalone test runner for live defense evidence.
 * Owner: Adam Mohammed
 */
public class PrimKruskalTest {

    private static int total = 0;
    private static int passed = 0;

    public static void main(String[] args) {
        runAllTests();
        System.out.println("\n========== PrimKruskalTest SUMMARY ==========");
        System.out.println(passed + " / " + total + " tests passed");
        if (passed != total) {
            System.out.println((total - passed) + " TEST(S) FAILED");
            System.exit(1);
        } else {
            System.out.println("ALL TESTS PASSED");
        }
    }

    public static void runAllTests() {
        testPrimMSTNormal();
        testKruskalMSTNormal();
        testPrimInvalidStart();
        testKruskalNullGraph();
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
        graph.addVertex(1, "Node 1");
        graph.addVertex(2, "Node 2");
        graph.addVertex(3, "Node 3");
        graph.addVertex(4, "Node 4");

        graph.addEdge(1, 2, 1.0);
        graph.addEdge(2, 3, 4.0);
        graph.addEdge(1, 3, 3.0);
        graph.addEdge(3, 4, 2.0);
        return graph;
    }

    private static void testPrimMSTNormal() {
        Graph g = buildGraph();
        PrimKruskal.MSTResult res = PrimKruskal.prim(g, 1);
        assertEquals("Prim Edge Count", 3, res.edges.length);
        assertTrue("Prim Total Cost", Math.abs(res.totalCost - 6.0) < 0.001);
    }

    private static void testKruskalMSTNormal() {
        Graph g = buildGraph();
        PrimKruskal.MSTResult res = PrimKruskal.kruskal(g);
        assertEquals("Kruskal Edge Count", 3, res.edges.length);
        assertTrue("Kruskal Total Cost", Math.abs(res.totalCost - 6.0) < 0.001);
    }

    private static void testPrimInvalidStart() {
        Graph g = buildGraph();
        boolean caught = false;
        try {
            PrimKruskal.prim(g, 999);
        } catch (IllegalArgumentException e) {
            caught = true;
        }
        assertTrue("Prim Invalid Start Throws Exception", caught);
    }

    private static void testKruskalNullGraph() {
        boolean caught = false;
        try {
            PrimKruskal.kruskal(null);
        } catch (IllegalArgumentException e) {
            caught = true;
        }
        assertTrue("Kruskal Null Graph Throws Exception", caught);
    }
}
