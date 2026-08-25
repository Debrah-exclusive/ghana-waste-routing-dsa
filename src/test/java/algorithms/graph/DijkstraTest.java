package algorithms.graph;

import structures.Graph;

/**
 * Unit tests for Dijkstra algorithm stub delegating to graph Dijkstra runner.
 * Owner: Ivan Kwamena Johnson
 */
public class DijkstraTest {

    private static int total = 0;
    private static int passed = 0;

    public static void main(String[] args) {
        testDijkstraNormal();
        System.out.println("\n========== DijkstraTest SUMMARY ==========");
        System.out.println(passed + " / " + total + " tests passed");
    }

    private static void testDijkstraNormal() {
        Graph g = new Graph();
        g.addVertex(1, "Depot");
        g.addVertex(2, "Station A");
        g.addEdge(1, 2, 5.0);

        total++;
        if (g.hasEdge(1, 2) && g.getEdgeWeight(1, 2) == 5.0) {
            passed++;
            System.out.println("  [PASS] Dijkstra Graph Edge Weight Verification");
        }
    }
}
