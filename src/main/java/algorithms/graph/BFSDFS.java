package algorithms.graph;

import structures.Graph;
import structures.MyQueue;

/**
 * Owner: Sadiq Moro Ayariga
 * Implementation of Breadth-First Search (BFS) and Depth-First Search (DFS)
 * graph traversals using custom data structures (Graph, MyQueue, and recursive call stack).
 *
 * Produces traversal order and step-by-step trace output for defense demonstration.
 */
public class BFSDFS {

    public static class TraversalResult {
        public int[] order;
        public String traceTable;

        public TraversalResult(int[] order, String traceTable) {
            this.order = order;
            this.traceTable = traceTable;
        }
    }

    /**
     * Performs Breadth-First Search (BFS) starting from startVertexId.
     */
    public static TraversalResult bfs(Graph graph, int startVertexId) {
        if (graph == null || !graph.hasVertex(startVertexId)) {
            throw new IllegalArgumentException("Invalid graph or starting vertex ID");
        }

        int n = graph.vertexCount();
        int[] allIds = graph.getAllVertexIds();
        
        // Custom visited tracking
        boolean[] visited = new boolean[10000]; // Assuming max vertex ID < 10000 or map dynamically
        int maxId = 0;
        for (int id : allIds) if (id > maxId) maxId = id;
        if (maxId >= visited.length) visited = new boolean[maxId + 100];

        int[] visitOrder = new int[n];
        int visitCount = 0;

        MyQueue<Integer> queue = new MyQueue<>();
        StringBuilder trace = new StringBuilder();
        trace.append("| Step | Visited Vertex | Queue Frontier | Action |\n");
        trace.append("|---|---|---|---|\n");

        queue.enqueue(startVertexId);
        visited[startVertexId] = true;

        int step = 1;
        while (!queue.isEmpty()) {
            int current = queue.dequeue();
            visitOrder[visitCount++] = current;

            int[] neighbors = graph.getNeighbors(current);
            StringBuilder enqueuedNames = new StringBuilder();
            for (int neighbor : neighbors) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.enqueue(neighbor);
                    if (enqueuedNames.length() > 0) enqueuedNames.append(", ");
                    enqueuedNames.append(neighbor);
                }
            }

            trace.append(String.format("| %d | V%d | %s | Dequeued V%d, Enqueued [%s] |\n",
                    step++, current, queue.toString(), current, enqueuedNames.toString()));
        }

        // Trim visitOrder if graph is disconnected
        int[] actualOrder = new int[visitCount];
        System.arraycopy(visitOrder, 0, actualOrder, 0, visitCount);

        return new TraversalResult(actualOrder, trace.toString());
    }

    /**
     * Performs Depth-First Search (DFS) starting from startVertexId.
     */
    public static TraversalResult dfs(Graph graph, int startVertexId) {
        if (graph == null || !graph.hasVertex(startVertexId)) {
            throw new IllegalArgumentException("Invalid graph or starting vertex ID");
        }

        int[] allIds = graph.getAllVertexIds();
        int maxId = 0;
        for (int id : allIds) if (id > maxId) maxId = id;
        boolean[] visited = new boolean[maxId + 100];

        int[] visitOrder = new int[graph.vertexCount()];
        int[] visitCount = new int[]{0};

        StringBuilder trace = new StringBuilder();
        trace.append("| Step | Visited Vertex | Action |\n");
        trace.append("|---|---|---|\n");
        int[] step = new int[]{1};

        dfsRecursive(graph, startVertexId, visited, visitOrder, visitCount, trace, step);

        int[] actualOrder = new int[visitCount[0]];
        System.arraycopy(visitOrder, 0, actualOrder, 0, visitCount[0]);

        return new TraversalResult(actualOrder, trace.toString());
    }

    private static void dfsRecursive(Graph graph, int u, boolean[] visited, int[] order, int[] count, StringBuilder trace, int[] step) {
        visited[u] = true;
        order[count[0]++] = u;
        trace.append(String.format("| %d | V%d | Visited V%d, exploring neighbors |\n", step[0]++, u, u));

        int[] neighbors = graph.getNeighbors(u);
        for (int v : neighbors) {
            if (!visited[v]) {
                dfsRecursive(graph, v, visited, order, count, trace, step);
            }
        }
    }
}
