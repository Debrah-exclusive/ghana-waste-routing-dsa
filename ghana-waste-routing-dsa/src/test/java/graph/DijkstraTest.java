package graph;

import static graph.TestHarness.*;
import java.util.List;

public class DijkstraTest {

    private static Graph sampleGraph() {
        Graph g = new Graph(false);
        for (String v : List.of("A", "B", "C", "D", "E", "F")) g.addVertex(v);
        g.addEdge("A", "B", 2);
        g.addEdge("B", "C", 2);
        g.addEdge("A", "D", 5);
        g.addEdge("B", "E", 1);
        g.addEdge("C", "F", 3);
        g.addEdge("D", "E", 1);
        g.addEdge("E", "F", 1);
        return g;
    }

    public static void main(String[] args) {
        System.out.println("DijkstraTest");

        test("normal - shortest distance matches hand-worked trace", () -> {
            DijkstraResult r = Dijkstra.run(sampleGraph(), "A");
            assertEquals(0.0, r.distanceTo("A"), 1e-9, "distance to self should be 0");
            assertEquals(2.0, r.distanceTo("B"), 1e-9, "A->B direct edge");
            assertEquals(4.0, r.distanceTo("C"), 1e-9, "A->B->C = 2+2");
            assertEquals(4.0, r.distanceTo("D"), 1e-9, "A->B->E->D = 2+1+1 beats direct A->D = 5");
            assertEquals(3.0, r.distanceTo("E"), 1e-9, "A->B->E = 2+1, beats A->D->E = 5+1");
            assertEquals(4.0, r.distanceTo("F"), 1e-9, "A->B->E->F = 2+1+1, beats A->B->C->F = 4+3");
        });

        test("normal - reconstructed path matches shortest distance", () -> {
            DijkstraResult r = Dijkstra.run(sampleGraph(), "A");
            List<String> path = r.pathTo("F");
            assertEquals(List.of("A", "B", "E", "F"), path, "shortest path A->F");
        });

        test("normal - shortestPath() convenience method matches run()", () -> {
            DijkstraResult r = Dijkstra.shortestPath(sampleGraph(), "A", "C");
            assertEquals(4.0, r.distanceTo("C"), 1e-9, "A->C via B");
        });

        test("normal - full campus dataset produces a valid finite path", () -> {
            Graph campus = loadCampusGraphOrFail();
            DijkstraResult r = Dijkstra.shortestPath(campus, "L001", "L045");
            assertTrue(r.isReachable("L045"), "L045 should be reachable from L001 on the real dataset");
            List<String> path = r.pathTo("L045");
            assertEquals("L001", path.get(0), "path should start at source");
            assertEquals("L045", path.get(path.size() - 1), "path should end at target");
        });

        test("boundary - source equals target gives zero-length path", () -> {
            DijkstraResult r = Dijkstra.shortestPath(sampleGraph(), "A", "A");
            assertEquals(0.0, r.distanceTo("A"), 1e-9, "distance from a vertex to itself is 0");
            assertEquals(List.of("A"), r.pathTo("A"), "path to self is just [A]");
        });

        test("boundary - single-vertex graph", () -> {
            Graph g = new Graph();
            g.addVertex("ONLY");
            DijkstraResult r = Dijkstra.run(g, "ONLY");
            assertEquals(0.0, r.distanceTo("ONLY"), 1e-9, "only vertex has distance 0 to itself");
        });

        test("boundary - disconnected vertex is unreachable (distance = INF)", () -> {
            Graph g = new Graph();
            g.addVertex("A"); g.addVertex("B"); g.addVertex("ISOLATED");
            g.addEdge("A", "B", 1.0);
            DijkstraResult r = Dijkstra.run(g, "A");
            assertTrue(!r.isReachable("ISOLATED"), "isolated vertex should be unreachable");
            assertEquals(Double.POSITIVE_INFINITY, r.distanceTo("ISOLATED"), 0.0, "unreachable distance is +INF");
            assertTrue(r.pathTo("ISOLATED").isEmpty(), "path to unreachable vertex should be empty");
        });

        test("boundary - zero-weight edge does not break shortest path", () -> {
            Graph g = new Graph();
            g.addVertex("A"); g.addVertex("B"); g.addVertex("C");
            g.addEdge("A", "B", 0.0);
            g.addEdge("B", "C", 5.0);
            DijkstraResult r = Dijkstra.run(g, "A");
            assertEquals(0.0, r.distanceTo("B"), 1e-9, "zero-weight edge gives distance 0 to B");
            assertEquals(5.0, r.distanceTo("C"), 1e-9, "distance to C should still accumulate correctly");
        });

        test("boundary - tie between two equal-length paths picks a consistent one", () -> {
            Graph g = new Graph();
            for (String v : List.of("A", "B", "C", "D")) g.addVertex(v);
            g.addEdge("A", "B", 1.0);
            g.addEdge("A", "C", 1.0);
            g.addEdge("B", "D", 1.0);
            g.addEdge("C", "D", 1.0);
            DijkstraResult r = Dijkstra.run(g, "A");
            assertEquals(2.0, r.distanceTo("D"), 1e-9, "both A-B-D and A-C-D cost 2");
            List<String> path = r.pathTo("D");
            assertEquals(3, path.size(), "tie-broken path should still have exactly 3 vertices");
            assertEquals("A", path.get(0), "path starts at A");
            assertEquals("D", path.get(2), "path ends at D");
        });

        test("invalid - null graph is rejected", () -> {
            assertThrows(IllegalArgumentException.class, () -> Dijkstra.run(null, "A"),
                "Dijkstra.run(null, ...) should throw");
        });

        test("invalid - empty graph is rejected", () -> {
            assertThrows(IllegalArgumentException.class, () -> Dijkstra.run(new Graph(), "A"),
                "Dijkstra.run on an empty graph should throw");
        });

        test("invalid - unknown source vertex is rejected", () -> {
            assertThrows(IllegalArgumentException.class, () -> Dijkstra.run(sampleGraph(), "GHOST"),
                "unknown source vertex should throw");
        });

        test("invalid - unknown target vertex is rejected", () -> {
            assertThrows(IllegalArgumentException.class,
                () -> Dijkstra.shortestPath(sampleGraph(), "A", "GHOST"),
                "unknown target vertex should throw");
        });

        test("invalid - negative-weight edge can never enter the graph", () -> {
            Graph g = new Graph();
            g.addVertex("A"); g.addVertex("B");
            assertThrows(IllegalArgumentException.class, () -> g.addEdge("A", "B", -1.0),
                "negative weight rejected at graph construction time, before Dijkstra ever runs");
        });

        printSummary("DijkstraTest");
        if (hasFailures()) System.exit(1);
    }

    private static Graph loadCampusGraphOrFail() {
        try {
            return new CampusDataLoader().load(
                java.nio.file.Path.of("data/locations.csv"),
                java.nio.file.Path.of("data/roads.csv"),
                CampusDataLoader.WeightMetric.DISTANCE_KM);
        } catch (Exception e) {
            throw new AssertionError("Failed to load campus dataset: " + e);
        }
    }
}
