package structures;

/**
 * Unit tests for Graph — normal case, boundary case, invalid input case.
 * Owner: Elsie Atsu
 *
 * The Graph is implemented as a weighted, undirected ADJACENCY MATRIX using
 * three parallel 2D double arrays (distance, travelTime, roadConditionWeight).
 *
 * Test groups (per DSA assignment brief):
 *   NORMAL   — typical happy-path usage
 *   BOUNDARY — edge of structure validity (empty, single-vertex, resize,
 *              zero-weight edge, fully-connected, clear/reuse, self-weight zero)
 *   INVALID  — structurally impossible / unallowed inputs
 */
public class GraphTest {

    private static int total = 0;
    private static int passed = 0;

    public static void main(String[] args) {
        runAllTests();
        System.out.println("\n========== GraphTest SUMMARY ==========");
        System.out.println(passed + " / " + total + " tests passed");
        if (passed != total) {
            System.out.println((total - passed) + " TEST(S) FAILED");
            System.exit(1);
        } else {
            System.out.println("ALL TESTS PASSED");
        }
    }

    public static void runAllTests() {
        runNormalCaseTests();
        runBoundaryCaseTests();
        runInvalidInputTests();
    }

    // ====================================================================
    // NORMAL CASE TESTS
    // ====================================================================
    static void runNormalCaseTests() {
        testNormal_addVerticesAndEdges();
        testNormal_hasVertexAndHasEdge();
        testNormal_getNeighborsAndWeights();
        testNormal_getFullEdgeAttributes();
        testNormal_removeEdge();
        testNormal_removeVertex();
        testNormal_getAllVerticesAndAllEdges();
        testNormal_getDegree();
        testNormal_updateEdgeWeight();
        testNormal_setAndGetVertexName();
        testNormal_loadSmallRoadsDataset();
    }

    static void testNormal_addVerticesAndEdges() {
        total++;
        Graph g = new Graph();
        boolean ok = true;
        ok &= g.addVertex(1);
        ok &= g.addVertex(2, "Circle");
        ok &= g.addVertex(3, "Mall");
        ok &= g.vertexCount() == 3;
        ok &= g.isEmpty() == false;
        ok &= g.edgeCount() == 0;

        ok &= g.addEdge(1, 2, 0.5, 2, 1.2);
        ok &= g.addEdge(2, 3, 0.25, 1, 1.0);
        ok &= g.addEdge(1, 3, 0.7, 3, 1.3);
        ok &= g.edgeCount() == 3;

        check("NORMAL: addVerticesAndEdges", ok);
    }

    static void testNormal_hasVertexAndHasEdge() {
        total++;
        Graph g = new Graph();
        g.addVertex(10); g.addVertex(20); g.addVertex(30);
        g.addEdge(10, 20, 5.0);
        g.addEdge(20, 30, 7.0);

        boolean ok = true;
        ok &= g.hasVertex(10) && g.hasVertex(20) && g.hasVertex(30);
        ok &= !g.hasVertex(99);
        ok &= g.hasEdge(10, 20) && g.hasEdge(20, 10);
        ok &= g.hasEdge(20, 30) && g.hasEdge(30, 20);
        ok &= !g.hasEdge(10, 30);
        ok &= !g.hasEdge(10, 99);
        ok &= !g.hasEdge(99, 10);
        check("NORMAL: hasVertex hasEdge", ok);
    }

    static void testNormal_getNeighborsAndWeights() {
        total++;
        Graph g = new Graph();
        for (int i = 1; i <= 5; i++) g.addVertex(i);
        g.addEdge(3, 1, 10);
        g.addEdge(3, 2, 20);
        g.addEdge(3, 4, 30);
        g.addEdge(3, 5, 40);

        int[] nbrs = g.getNeighbors(3);
        double[] wts  = g.getNeighborWeights(3);
        java.util.Arrays.sort(nbrs);
        java.util.Arrays.sort(wts);
        boolean ok = true;
        ok &= nbrs.length == 4;
        ok &= nbrs[0] == 1 && nbrs[1] == 2 && nbrs[2] == 4 && nbrs[3] == 5;
        ok &= wts[0] == 10 && wts[1] == 20 && wts[2] == 30 && wts[3] == 40;
        ok &= g.getNeighbors(999).length == 0;
        ok &= g.getNeighborWeights(999).length == 0;
        check("NORMAL: getNeighbors getNeighborWeights", ok);
    }

    static void testNormal_getFullEdgeAttributes() {
        total++;
        Graph g = new Graph();
        g.addVertex(1); g.addVertex(2);
        g.addEdge(1, 2, 0.25, 2, 1.1);

        boolean ok = true;
        ok &= close(g.getEdgeWeight(1, 2), 0.25);
        ok &= close(g.getEdgeTravelTime(1, 2), 2.0);
        ok &= close(g.getEdgeRoadConditionWeight(1, 2), 1.1);
        ok &= close(g.getEdgeWeight(2, 1), 0.25);
        ok &= close(g.getEdgeTravelTime(2, 1), 2.0);
        ok &= close(g.getEdgeRoadConditionWeight(2, 1), 1.1);
        check("NORMAL: getFullEdgeAttributes (undirected)", ok);
    }

    static void testNormal_removeEdge() {
        total++;
        Graph g = new Graph();
        g.addVertex(1); g.addVertex(2); g.addVertex(3);
        g.addEdge(1, 2, 1.0);
        g.addEdge(2, 3, 2.0);

        boolean ok = true;
        ok &= g.removeEdge(1, 2);
        ok &= g.edgeCount() == 1;
        ok &= !g.hasEdge(1, 2) && !g.hasEdge(2, 1);
        ok &= g.hasEdge(2, 3);
        check("NORMAL: removeEdge", ok);
    }

    static void testNormal_removeVertex() {
        total++;
        Graph g = new Graph();
        for (int i = 1; i <= 4; i++) g.addVertex(i);
        g.addEdge(2, 1, 1);
        g.addEdge(2, 3, 2);
        g.addEdge(2, 4, 3);
        g.addEdge(1, 3, 4);

        boolean ok = true;
        ok &= g.removeVertex(2);
        ok &= g.vertexCount() == 3;
        ok &= !g.hasVertex(2);
        ok &= g.edgeCount() == 1;
        ok &= g.hasEdge(1, 3);
        ok &= g.getNeighbors(1).length == 1;
        check("NORMAL: removeVertex with edges", ok);
    }

    static void testNormal_getAllVerticesAndAllEdges() {
        total++;
        Graph g = new Graph();
        g.addVertex(1); g.addVertex(2); g.addVertex(3);
        g.addEdge(1, 2, 1.0);
        g.addEdge(1, 3, 2.0);
        g.addEdge(2, 3, 3.0);

        int[] verts = g.getAllVertexIds();
        java.util.Arrays.sort(verts);
        int[][] edges = g.getAllEdges();

        boolean ok = true;
        ok &= verts.length == 3 && verts[0] == 1 && verts[1] == 2 && verts[2] == 3;
        ok &= edges.length == 3;

        java.util.Set<String> set = new java.util.HashSet<String>();
        for (int[] e : edges) {
            int a = Math.min(e[0], e[1]);
            int b = Math.max(e[0], e[1]);
            set.add(a + "-" + b);
        }
        ok &= set.contains("1-2") && set.contains("1-3") && set.contains("2-3");
        check("NORMAL: getAllVertexIds getAllEdges", ok);
    }

    static void testNormal_getDegree() {
        total++;
        Graph g = new Graph();
        for (int i = 0; i < 5; i++) g.addVertex(i);
        g.addEdge(0, 1, 1);
        g.addEdge(0, 2, 1);
        g.addEdge(0, 3, 1);

        boolean ok = true;
        ok &= g.getDegree(0) == 3;
        ok &= g.getDegree(1) == 1;
        ok &= g.getDegree(4) == 0;
        ok &= g.getDegree(99) == -1;
        check("NORMAL: getDegree", ok);
    }

    static void testNormal_updateEdgeWeight() {
        total++;
        Graph g = new Graph();
        g.addVertex(1); g.addVertex(2);
        g.addEdge(1, 2, 10.0, 1, 1);

        boolean ok = true;
        ok &= g.updateEdgeWeight(1, 2, 99.5);
        ok &= close(g.getEdgeWeight(1, 2), 99.5);
        ok &= close(g.getEdgeWeight(2, 1), 99.5);
        ok &= !g.updateEdgeWeight(1, 99, 5);
        check("NORMAL: updateEdgeWeight", ok);
    }

    static void testNormal_setAndGetVertexName() {
        total++;
        Graph g = new Graph();
        g.addVertex(1);

        boolean ok = true;
        ok &= g.setVertexName(1, "Accra Mall");
        ok &= "Accra Mall".equals(g.getVertexName(1));
        ok &= g.getVertexName(99) == null;
        ok &= !g.setVertexName(99, "No");
        check("NORMAL: setVertexName getVertexName", ok);
    }

    static void testNormal_loadSmallRoadsDataset() {
        total++;
        Graph g = new Graph();
        String[] locNames = { "Accra Central Market", "Victoriaborg Waste Site",
            "Kaneshie Market Depot", "Osu Oxford Street", "La Trade Fair" };
        for (int i = 1; i <= locNames.length; i++) g.addVertex(i, locNames[i-1]);

        boolean ok = true;
        ok &= g.addEdge(1, 17, 0.25, 2, 1.1) == false;
        ok &= g.addEdge(1, 2, 0.25, 2, 1.1);
        ok &= g.addEdge(2, 3, 0.11, 1, 1.2);
        ok &= g.addEdge(3, 4, 0.56, 4, 1.1);
        ok &= g.addEdge(4, 5, 0.44, 3, 1.0);
        ok &= g.vertexCount() == 5;
        ok &= g.edgeCount() == 4;
        ok &= close(g.getEdgeWeight(2, 3), 0.11);
        ok &= close(g.getEdgeTravelTime(3, 4), 4.0);
        ok &= close(g.getEdgeRoadConditionWeight(4, 5), 1.0);
        check("NORMAL: small road dataset load", ok);
    }

    // ====================================================================
    // BOUNDARY CASE TESTS
    // ====================================================================
    static void runBoundaryCaseTests() {
        testBoundary_emptyGraph();
        testBoundary_singleVertexNoEdges();
        testBoundary_zeroDistanceEdge();
        testBoundary_diagonalZeroSelfWeight();
        testBoundary_manyVerticesTriggerMatrixResize();
        testBoundary_fullyConnectedSmallGraph();
        testBoundary_clearThenReuse();
        testBoundary_removeLastVertexAdded();
        testBoundary_vertexWithZeroDegree();
    }

    static void testBoundary_emptyGraph() {
        total++;
        Graph g = new Graph();
        boolean ok = true;
        ok &= g.isEmpty();
        ok &= g.vertexCount() == 0;
        ok &= g.edgeCount() == 0;
        ok &= g.getAllVertexIds().length == 0;
        ok &= g.getAllEdges().length == 0;
        ok &= !g.hasVertex(1);
        ok &= !g.hasEdge(1, 2);
        ok &= g.getEdgeWeight(1, 2) == -1.0;
        ok &= g.getNeighbors(1).length == 0;
        ok &= g.getDegree(1) == -1;
        check("BOUNDARY: empty graph queries", ok);
    }

    static void testBoundary_singleVertexNoEdges() {
        total++;
        Graph g = new Graph();
        g.addVertex(42);
        boolean ok = true;
        ok &= g.vertexCount() == 1;
        ok &= g.edgeCount() == 0;
        ok &= !g.isEmpty();
        ok &= g.hasVertex(42);
        ok &= g.getDegree(42) == 0;
        ok &= g.getNeighbors(42).length == 0;
        ok &= g.getAllEdges().length == 0;
        ok &= g.removeVertex(42);
        ok &= g.isEmpty();
        check("BOUNDARY: single vertex", ok);
    }

    static void testBoundary_zeroDistanceEdge() {
        total++;
        Graph g = new Graph();
        g.addVertex(1); g.addVertex(2);
        boolean ok = true;
        ok &= g.addEdge(1, 2, 0.0, 1, 1.0);
        ok &= g.edgeCount() == 1;
        ok &= g.hasEdge(1, 2);
        ok &= close(g.getEdgeWeight(1, 2), 0.0);
        int[] nbrs = g.getNeighbors(1);
        ok &= nbrs.length == 1 && nbrs[0] == 2;
        check("BOUNDARY: zero-distance edge allowed", ok);
    }

    static void testBoundary_diagonalZeroSelfWeight() {
        total++;
        Graph g = new Graph();
        g.addVertex(7);
        g.addVertex(8);
        boolean ok = true;
        ok &= close(g.getEdgeWeight(7, 7), 0.0);
        ok &= close(g.getEdgeTravelTime(7, 7), 0.0);
        ok &= close(g.getEdgeRoadConditionWeight(7, 7), 0.0);
        ok &= close(g.getEdgeWeight(8, 8), 0.0);
        check("BOUNDARY: diagonal (self) cells are 0.0", ok);
    }

    static void testBoundary_manyVerticesTriggerMatrixResize() {
        total++;
        Graph g = new Graph();
        int initialCap = g.capacity();
        int n = initialCap + 20;
        for (int i = 1; i <= n; i++) g.addVertex(i);
        for (int i = 1; i < n; i++) g.addEdge(i, i + 1, i * 0.01, i, 1);

        boolean ok = true;
        ok &= g.vertexCount() == n;
        ok &= g.edgeCount() == n - 1;
        ok &= g.capacity() >= n;
        for (int i = 1; i < n; i++) {
            ok &= g.hasEdge(i, i + 1);
            ok &= close(g.getEdgeWeight(i, i + 1), i * 0.01);
        }
        check("BOUNDARY: matrix resize past initial capacity (" + initialCap + " -> " + n + ")", ok);
    }

    static void testBoundary_fullyConnectedSmallGraph() {
        total++;
        Graph g = new Graph();
        final int n = 6;
        for (int i = 0; i < n; i++) g.addVertex(i);
        int expected = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                g.addEdge(i, j, (i+j));
                expected++;
            }
        }
        boolean ok = true;
        ok &= g.edgeCount() == expected;
        ok &= g.getAllEdges().length == expected;
        for (int i = 0; i < n; i++) ok &= g.getDegree(i) == n - 1;
        check("BOUNDARY: fully-connected K" + n + " graph", ok);
    }

    static void testBoundary_clearThenReuse() {
        total++;
        Graph g = new Graph();
        g.addVertex(1); g.addVertex(2);
        g.addEdge(1, 2, 10);
        g.clear();
        boolean ok = true;
        ok &= g.isEmpty();
        ok &= g.vertexCount() == 0;
        ok &= g.edgeCount() == 0;
        ok &= g.addVertex(10);
        ok &= g.addVertex(20);
        ok &= g.addEdge(10, 20, 5);
        ok &= g.vertexCount() == 2 && g.edgeCount() == 1;
        ok &= close(g.getEdgeWeight(10, 20), 5.0);
        check("BOUNDARY: clear then reuse", ok);
    }

    static void testBoundary_removeLastVertexAdded() {
        total++;
        Graph g = new Graph();
        g.addVertex(1); g.addVertex(2); g.addVertex(3);
        g.addEdge(1, 3, 5);
        g.addEdge(2, 3, 7);

        boolean ok = true;
        ok &= g.removeVertex(3);
        ok &= g.vertexCount() == 2;
        ok &= g.edgeCount() == 0;
        ok &= !g.hasVertex(3);
        ok &= !g.hasEdge(1, 3) && !g.hasEdge(2, 3);
        ok &= g.hasVertex(1) && g.hasVertex(2);
        check("BOUNDARY: remove last-added vertex with edges", ok);
    }

    static void testBoundary_vertexWithZeroDegree() {
        total++;
        Graph g = new Graph();
        g.addVertex(1); g.addVertex(2); g.addVertex(3);
        g.addEdge(1, 2, 1.0);
        boolean ok = true;
        ok &= g.getDegree(3) == 0;
        ok &= g.getNeighbors(3).length == 0;
        ok &= g.getNeighborWeights(3).length == 0;
        ok &= g.vertexCount() == 3;
        ok &= g.edgeCount() == 1;
        check("BOUNDARY: isolated zero-degree vertex", ok);
    }

    // ====================================================================
    // INVALID INPUT TESTS
    // ====================================================================
    static void runInvalidInputTests() {
        testInvalid_selfLoop();
        testInvalid_duplicateVertex();
        testInvalid_duplicateEdge();
        testInvalid_edgeWithMissingVertex();
        testInvalid_removeMissingVertex();
        testInvalid_removeMissingEdge();
        testInvalid_updateWeightOnMissingEdge();
        testInvalid_nameSetterMissingVertex();
    }

    static void testInvalid_selfLoop() {
        total++;
        Graph g = new Graph();
        g.addVertex(1);
        boolean ok = true;
        ok &= g.addEdge(1, 1, 1.0) == false;
        ok &= g.addEdge(1, 1, 1.0, 2, 1) == false;
        ok &= g.edgeCount() == 0;
        ok &= g.hasEdge(1, 1) == false;
        check("INVALID: self-loop rejected", ok);
    }

    static void testInvalid_duplicateVertex() {
        total++;
        Graph g = new Graph();
        boolean ok = true;
        ok &= g.addVertex(5);
        ok &= g.addVertex(5) == false;
        ok &= g.addVertex(5, "x") == false;
        ok &= g.vertexCount() == 1;
        check("INVALID: duplicate vertex rejected", ok);
    }

    static void testInvalid_duplicateEdge() {
        total++;
        Graph g = new Graph();
        g.addVertex(1); g.addVertex(2);
        g.addEdge(1, 2, 1.0);
        boolean ok = true;
        ok &= g.addEdge(1, 2, 99.0) == false;
        ok &= g.addEdge(2, 1, 99.0) == false;
        ok &= g.addEdge(1, 2, 0.5, 3, 1) == false;
        ok &= g.edgeCount() == 1;
        ok &= close(g.getEdgeWeight(1, 2), 1.0);
        check("INVALID: duplicate edge rejected (first weight wins)", ok);
    }

    static void testInvalid_edgeWithMissingVertex() {
        total++;
        Graph g = new Graph();
        g.addVertex(1);
        boolean ok = true;
        ok &= g.addEdge(1, 99, 1.0) == false;
        ok &= g.addEdge(99, 1, 1.0) == false;
        ok &= g.addEdge(10, 20, 1.0) == false;
        ok &= g.edgeCount() == 0;
        check("INVALID: edge between non-existent vertices rejected", ok);
    }

    static void testInvalid_removeMissingVertex() {
        total++;
        Graph g = new Graph();
        g.addVertex(1); g.addVertex(2);
        g.addEdge(1, 2, 1.0);

        boolean ok = true;
        ok &= g.removeVertex(999) == false;
        ok &= g.vertexCount() == 2;
        ok &= g.edgeCount() == 1;
        check("INVALID: remove missing vertex returns false", ok);
    }

    static void testInvalid_removeMissingEdge() {
        total++;
        Graph g = new Graph();
        g.addVertex(1); g.addVertex(2); g.addVertex(3);
        g.addEdge(1, 2, 1.0);

        boolean ok = true;
        ok &= g.removeEdge(1, 3) == false;
        ok &= g.removeEdge(1, 99) == false;
        ok &= g.removeEdge(99, 1) == false;
        ok &= g.edgeCount() == 1;
        check("INVALID: remove missing edge returns false", ok);
    }

    static void testInvalid_updateWeightOnMissingEdge() {
        total++;
        Graph g = new Graph();
        g.addVertex(1); g.addVertex(2); g.addVertex(3);

        boolean ok = true;
        ok &= g.updateEdgeWeight(1, 2, 99) == false;
        ok &= g.updateEdgeWeight(1, 99, 5) == false;
        check("INVALID: updateEdgeWeight on missing edge returns false", ok);
    }

    static void testInvalid_nameSetterMissingVertex() {
        total++;
        Graph g = new Graph();
        g.addVertex(1);
        boolean ok = true;
        ok &= g.setVertexName(99, "Bad") == false;
        ok &= g.getVertexName(99) == null;
        check("INVALID: setVertexName on missing vertex returns false", ok);
    }

    // ====================================================================
    // Helpers
    // ====================================================================
    static boolean close(double a, double b) {
        return Math.abs(a - b) < 1e-9;
    }

    static void check(String name, boolean pass) {
        if (pass) passed++;
        System.out.println((pass ? "[PASS] " : "[FAIL] ") + name);
    }
}
