package algorithms.graph;

import structures.DisjointSet;
import structures.Graph;
import structures.PriorityQueueHeap;
import java.util.Comparator;

/**
 * Owner: Adam Mohammed
 * Implementations of Prim's and Kruskal's Minimum Spanning Tree (MST) algorithms.
 * Uses custom structures (Graph, DisjointSet, PriorityQueueHeap).
 */
public class PrimKruskal {

    public static class Edge {
        public int u, v;
        public double weight;

        public Edge(int u, int v, double weight) {
            this.u = u;
            this.v = v;
            this.weight = weight;
        }

        @Override
        public String toString() {
            return String.format("V%d - V%d (%.2f km)", u, v, weight);
        }
    }

    public static class MSTResult {
        public Edge[] edges;
        public double totalCost;
        public String algorithmName;

        public MSTResult(Edge[] edges, double totalCost, String algorithmName) {
            this.edges = edges;
            this.totalCost = totalCost;
            this.algorithmName = algorithmName;
        }

        public String getEdgeListFormatted() {
            StringBuilder sb = new StringBuilder();
            sb.append("=== ").append(algorithmName).append(" MST Edges ===\n");
            for (Edge e : edges) {
                sb.append(e.toString()).append("\n");
            }
            sb.append(String.format("Total MST Cost: %.2f km\n", totalCost));
            return sb.toString();
        }
    }

    /**
     * Prim's Algorithm using Min-Priority Queue.
     */
    public static MSTResult prim(Graph graph, int startVertexId) {
        if (graph == null || !graph.hasVertex(startVertexId)) {
            throw new IllegalArgumentException("Invalid graph or starting vertex");
        }

        int vertexCount = graph.vertexCount();
        int[] allIds = graph.getAllVertexIds();
        int maxId = 0;
        for (int id : allIds) if (id > maxId) maxId = id;

        boolean[] inMST = new boolean[maxId + 100];
        Edge[] mstEdges = new Edge[vertexCount - 1];
        int edgeIdx = 0;
        double totalCost = 0.0;

        PriorityQueueHeap<Edge> pq = new PriorityQueueHeap<>(Comparator.comparingDouble(e -> e.weight));
        inMST[startVertexId] = true;

        // Add initial edges
        int[] neighbors = graph.getNeighbors(startVertexId);
        for (int nbr : neighbors) {
            double w = graph.getEdgeWeight(startVertexId, nbr);
            pq.insert(new Edge(startVertexId, nbr, w));
        }

        while (!pq.isEmpty() && edgeIdx < vertexCount - 1) {
            Edge minEdge = pq.extractTop();
            if (inMST[minEdge.u] && inMST[minEdge.v]) continue;

            int nextV = inMST[minEdge.u] ? minEdge.v : minEdge.u;
            inMST[nextV] = true;
            mstEdges[edgeIdx++] = minEdge;
            totalCost += minEdge.weight;

            // Add new edges from nextV
            int[] nextNeighbors = graph.getNeighbors(nextV);
            for (int nbr : nextNeighbors) {
                if (!inMST[nbr]) {
                    pq.insert(new Edge(nextV, nbr, graph.getEdgeWeight(nextV, nbr)));
                }
            }
        }

        // Trim edges array if disconnected graph
        Edge[] actualEdges = new Edge[edgeIdx];
        System.arraycopy(mstEdges, 0, actualEdges, 0, edgeIdx);

        return new MSTResult(actualEdges, totalCost, "Prim's Algorithm");
    }

    /**
     * Kruskal's Algorithm using DisjointSet & PriorityQueue.
     */
    public static MSTResult kruskal(Graph graph) {
        if (graph == null || graph.isEmpty()) {
            throw new IllegalArgumentException("Invalid graph for Kruskal MST");
        }

        int[][] rawEdges = graph.getAllEdges();
        int edgeCount = rawEdges.length;
        PriorityQueueHeap<Edge> pq = new PriorityQueueHeap<>(Comparator.comparingDouble(e -> e.weight), Math.max(1, edgeCount));

        int maxId = 0;
        for (int[] e : rawEdges) {
            double w = graph.getEdgeWeight(e[0], e[1]);
            pq.insert(new Edge(e[0], e[1], w));
            if (e[0] > maxId) maxId = e[0];
            if (e[1] > maxId) maxId = e[1];
        }

        DisjointSet ds = new DisjointSet(maxId + 100);
        Edge[] mstEdges = new Edge[graph.vertexCount() - 1];
        int edgeIdx = 0;
        double totalCost = 0.0;

        while (!pq.isEmpty() && edgeIdx < graph.vertexCount() - 1) {
            Edge edge = pq.extractTop();
            if (ds.union(edge.u, edge.v)) {
                mstEdges[edgeIdx++] = edge;
                totalCost += edge.weight;
            }
        }

        Edge[] actualEdges = new Edge[edgeIdx];
        System.arraycopy(mstEdges, 0, actualEdges, 0, edgeIdx);

        return new MSTResult(actualEdges, totalCost, "Kruskal's Algorithm");
    }
}
