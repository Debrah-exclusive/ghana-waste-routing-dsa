package graph;

import java.util.*;

public class Graph {

    public static final class Edge {
        private final String to;
        private final double weight;
        private final String edgeId;

        public Edge(String to, double weight, String edgeId) {
            this.to = to;
            this.weight = weight;
            this.edgeId = edgeId;
        }

        public String getTo() { return to; }
        public double getWeight() { return weight; }
        public String getEdgeId() { return edgeId; }

        @Override
        public String toString() {
            return "-> " + to + " (w=" + weight + (edgeId != null ? ", id=" + edgeId : "") + ")";
        }
    }

    private final Map<String, List<Edge>> adjacencyList = new LinkedHashMap<>();
    private final boolean directed;

    public Graph(boolean directed) {
        this.directed = directed;
    }

    public Graph() {
        this(false);
    }

    public boolean isDirected() {
        return directed;
    }

    public void addVertex(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Vertex id must not be null/blank");
        }
        adjacencyList.putIfAbsent(id, new ArrayList<>());
    }

    public boolean hasVertex(String id) {
        return adjacencyList.containsKey(id);
    }

    public void addEdge(String from, String to, double weight, String edgeId) {
        if (!hasVertex(from) || !hasVertex(to)) {
            throw new IllegalArgumentException(
                "Cannot add edge - unknown vertex: " + from + " or " + to);
        }
        if (weight < 0) {
            throw new IllegalArgumentException(
                "Edge weight must be non-negative for Dijkstra's algorithm, got: " + weight);
        }
        if (from.equals(to)) {
            throw new IllegalArgumentException("Self-loops are not supported: " + from);
        }
        adjacencyList.get(from).add(new Edge(to, weight, edgeId));
        if (!directed) {
            adjacencyList.get(to).add(new Edge(from, weight, edgeId));
        }
    }

    public void addEdge(String from, String to, double weight) {
        addEdge(from, to, weight, null);
    }

    public List<Edge> getNeighbors(String id) {
        List<Edge> edges = adjacencyList.get(id);
        if (edges == null) {
            throw new NoSuchElementException("No such vertex: " + id);
        }
        return Collections.unmodifiableList(edges);
    }

    public Set<String> getVertices() {
        return Collections.unmodifiableSet(adjacencyList.keySet());
    }

    public int vertexCount() {
        return adjacencyList.size();
    }

    public int edgeCount() {
        int total = adjacencyList.values().stream().mapToInt(List::size).sum();
        return directed ? total : total / 2;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Graph(").append(directed ? "directed" : "undirected")
          .append(", |V|=").append(vertexCount())
          .append(", |E|=").append(edgeCount()).append(")\n");
        for (Map.Entry<String, List<Edge>> entry : adjacencyList.entrySet()) {
            sb.append(entry.getKey()).append(" ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }
}
