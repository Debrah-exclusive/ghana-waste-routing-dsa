package graph;

import static graph.TestHarness.*;

public class GraphTest {

    public static void main(String[] args) {
        System.out.println("GraphTest");

        test("addVertex + hasVertex - normal insert", () -> {
            Graph g = new Graph();
            g.addVertex("A");
            assertTrue(g.hasVertex("A"), "vertex A should exist after addVertex");
            assertTrue(!g.hasVertex("B"), "vertex B should not exist");
        });

        test("addEdge - undirected edge is traversable both ways", () -> {
            Graph g = new Graph(false);
            g.addVertex("A"); g.addVertex("B");
            g.addEdge("A", "B", 5.0);
            assertEquals(1, g.getNeighbors("A").size(), "A should have 1 neighbor");
            assertEquals(1, g.getNeighbors("B").size(), "B should have 1 neighbor (undirected)");
            assertEquals("B", g.getNeighbors("A").get(0).getTo(), "A's neighbor should be B");
            assertEquals("A", g.getNeighbors("B").get(0).getTo(), "B's neighbor should be A");
        });

        test("addEdge - directed edge is one-way only", () -> {
            Graph g = new Graph(true);
            g.addVertex("A"); g.addVertex("B");
            g.addEdge("A", "B", 5.0);
            assertEquals(1, g.getNeighbors("A").size(), "A should have 1 neighbor");
            assertEquals(0, g.getNeighbors("B").size(), "B should have 0 neighbors (directed)");
        });

        test("vertexCount / edgeCount - normal graph", () -> {
            Graph g = new Graph(false);
            g.addVertex("A"); g.addVertex("B"); g.addVertex("C");
            g.addEdge("A", "B", 1.0);
            g.addEdge("B", "C", 2.0);
            assertEquals(3, g.vertexCount(), "should have 3 vertices");
            assertEquals(2, g.edgeCount(), "should have 2 undirected edges");
        });

        test("boundary - empty graph has zero vertices/edges", () -> {
            Graph g = new Graph();
            assertEquals(0, g.vertexCount(), "empty graph should have 0 vertices");
            assertEquals(0, g.edgeCount(), "empty graph should have 0 edges");
        });

        test("boundary - single vertex, no edges", () -> {
            Graph g = new Graph();
            g.addVertex("A");
            assertEquals(1, g.vertexCount(), "should have exactly 1 vertex");
            assertTrue(g.getNeighbors("A").isEmpty(), "isolated vertex should have no neighbors");
        });

        test("boundary - zero-weight edge is allowed", () -> {
            Graph g = new Graph();
            g.addVertex("A"); g.addVertex("B");
            g.addEdge("A", "B", 0.0);
            assertEquals(0.0, g.getNeighbors("A").get(0).getWeight(), 1e-9,
                "zero weight edge should be stored as 0.0, not rejected");
        });

        test("boundary - re-adding the same vertex id is a no-op", () -> {
            Graph g = new Graph();
            g.addVertex("A");
            g.addVertex("A");
            assertEquals(1, g.vertexCount(), "duplicate addVertex should not create a second vertex");
        });

        test("boundary - largest dataset scale (52 vertices) still consistent", () -> {
            Graph g = new Graph();
            for (int i = 1; i <= 52; i++) g.addVertex("L" + i);
            for (int i = 1; i < 52; i++) g.addEdge("L" + i, "L" + (i + 1), 1.0);
            assertEquals(52, g.vertexCount(), "should scale to 52 vertices like the campus dataset");
            assertEquals(51, g.edgeCount(), "chain of 52 vertices should have 51 edges");
        });

        test("invalid - null vertex id is rejected", () -> {
            Graph g = new Graph();
            assertThrows(IllegalArgumentException.class, () -> g.addVertex(null),
                "addVertex(null) should throw");
        });

        test("invalid - blank vertex id is rejected", () -> {
            Graph g = new Graph();
            assertThrows(IllegalArgumentException.class, () -> g.addVertex("   "),
                "addVertex(blank) should throw");
        });

        test("invalid - edge between unknown vertices is rejected", () -> {
            Graph g = new Graph();
            g.addVertex("A");
            assertThrows(IllegalArgumentException.class, () -> g.addEdge("A", "Z", 1.0),
                "addEdge to unknown vertex Z should throw");
        });

        test("invalid - negative edge weight is rejected", () -> {
            Graph g = new Graph();
            g.addVertex("A"); g.addVertex("B");
            assertThrows(IllegalArgumentException.class, () -> g.addEdge("A", "B", -3.0),
                "negative weight should throw (breaks Dijkstra's correctness guarantee)");
        });

        test("invalid - self-loop is rejected", () -> {
            Graph g = new Graph();
            g.addVertex("A");
            assertThrows(IllegalArgumentException.class, () -> g.addEdge("A", "A", 1.0),
                "self-loop A->A should throw");
        });

        test("invalid - getNeighbors on unknown vertex throws", () -> {
            Graph g = new Graph();
            assertThrows(java.util.NoSuchElementException.class, () -> g.getNeighbors("ghost"),
                "getNeighbors on a vertex that was never added should throw");
        });

        printSummary("GraphTest");
        if (hasFailures()) System.exit(1);
    }
}
