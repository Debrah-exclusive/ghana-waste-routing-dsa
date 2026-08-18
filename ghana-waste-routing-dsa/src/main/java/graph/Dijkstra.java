package graph;

import java.util.*;

public final class Dijkstra {

    private Dijkstra() { }

    private static final class HeapEntry {
        final String vertex;
        final double distance;
        HeapEntry(String vertex, double distance) {
            this.vertex = vertex;
            this.distance = distance;
        }
    }

    public static DijkstraResult run(Graph graph, String source) {
        if (graph == null || graph.vertexCount() == 0) {
            throw new IllegalArgumentException("Graph must not be null or empty");
        }
        if (!graph.hasVertex(source)) {
            throw new IllegalArgumentException("Source vertex not found in graph: " + source);
        }

        Map<String, Double> dist = new HashMap<>();
        Map<String, String> pred = new HashMap<>();
        Set<String> visited = new HashSet<>();

        for (String v : graph.getVertices()) {
            dist.put(v, Double.POSITIVE_INFINITY);
        }
        dist.put(source, 0.0);

        PriorityQueue<HeapEntry> pq = new PriorityQueue<>(Comparator.comparingDouble(e -> e.distance));
        pq.add(new HeapEntry(source, 0.0));

        while (!pq.isEmpty()) {
            HeapEntry current = pq.poll();
            String u = current.vertex;

            if (visited.contains(u)) {
                continue;
            }
            visited.add(u);

            for (Graph.Edge edge : graph.getNeighbors(u)) {
                String v = edge.getTo();
                if (visited.contains(v)) continue;

                double candidate = dist.get(u) + edge.getWeight();
                if (candidate < dist.get(v)) {
                    dist.put(v, candidate);
                    pred.put(v, u);
                    pq.add(new HeapEntry(v, candidate));
                }
            }
        }

        return new DijkstraResult(source, dist, pred);
    }

    public static DijkstraResult shortestPath(Graph graph, String source, String target) {
        if (!graph.hasVertex(target)) {
            throw new IllegalArgumentException("Target vertex not found in graph: " + target);
        }
        return run(graph, source);
    }
}
