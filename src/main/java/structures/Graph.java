package structures;

/**
 * Owner: Ivan Kwamena Johnson and Elsie Atsu
 * TODO: implement from scratch (built-in Java collections not allowed for this class).
 * Required evidence: normal-case, boundary-case, invalid-input unit tests + trace table.
 */
public class Graph {

    private static class Edge {
        int source;
        int destination;
        double weight;
        double travelTime;
        double roadConditionWeight;

        Edge(int source, int destination, double weight) {
            this.source = source;
            this.destination = destination;
            this.weight = weight;
            this.travelTime = 0.0;
            this.roadConditionWeight = 0.0;
        }

        Edge(int source, int destination, double weight, double travelTime, double roadConditionWeight) {
            this.source = source;
            this.destination = destination;
            this.weight = weight;
            this.travelTime = travelTime;
            this.roadConditionWeight = roadConditionWeight;
        }
    }

    private static class Vertex {
        int id;
        String name;
        Edge[] edges;
        int edgeCount;

        Vertex(int id) {
            this.id = id;
            this.name = null;
            this.edges = new Edge[4];
            this.edgeCount = 0;
        }

        Vertex(int id, String name) {
            this.id = id;
            this.name = name;
            this.edges = new Edge[4];
            this.edgeCount = 0;
        }

        void ensureEdgeCapacity() {
            if (edgeCount >= edges.length) {
                Edge[] newEdges = new Edge[edges.length * 2];
                for (int i = 0; i < edgeCount; i++) {
                    newEdges[i] = edges[i];
                }
                edges = newEdges;
            }
        }

        void addEdge(Edge edge) {
            ensureEdgeCapacity();
            edges[edgeCount++] = edge;
        }

        boolean removeEdgeTo(int destId) {
            for (int i = 0; i < edgeCount; i++) {
                if (edges[i].destination == destId) {
                    for (int j = i; j < edgeCount - 1; j++) {
                        edges[j] = edges[j + 1];
                    }
                    edges[edgeCount - 1] = null;
                    edgeCount--;
                    return true;
                }
            }
            return false;
        }

        Edge findEdgeTo(int destId) {
            for (int i = 0; i < edgeCount; i++) {
                if (edges[i].destination == destId) {
                    return edges[i];
                }
            }
            return null;
        }
    }

    private Vertex[] vertices;
    private int vertexCount;
    private int edgeCount;

    public Graph() {
        this.vertices = new Vertex[8];
        this.vertexCount = 0;
        this.edgeCount = 0;
    }

    private void ensureVertexCapacity() {
        if (vertexCount >= vertices.length) {
            Vertex[] newVertices = new Vertex[vertices.length * 2];
            for (int i = 0; i < vertexCount; i++) {
                newVertices[i] = vertices[i];
            }
            vertices = newVertices;
        }
    }

    private Vertex findVertex(int id) {
        for (int i = 0; i < vertexCount; i++) {
            if (vertices[i].id == id) {
                return vertices[i];
            }
        }
        return null;
    }

    public boolean addVertex(int id) {
        if (findVertex(id) != null) {
            return false;
        }
        ensureVertexCapacity();
        vertices[vertexCount++] = new Vertex(id);
        return true;
    }

    public boolean addVertex(int id, String name) {
        if (findVertex(id) != null) {
            return false;
        }
        ensureVertexCapacity();
        vertices[vertexCount++] = new Vertex(id, name);
        return true;
    }

    public boolean removeVertex(int id) {
        int index = -1;
        for (int i = 0; i < vertexCount; i++) {
            if (vertices[i].id == id) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            return false;
        }

        Vertex toRemove = vertices[index];
        for (int i = 0; i < toRemove.edgeCount; i++) {
            int neighborId = toRemove.edges[i].destination;
            Vertex neighbor = findVertex(neighborId);
            if (neighbor != null) {
                neighbor.removeEdgeTo(id);
            }
        }
        edgeCount -= toRemove.edgeCount;

        for (int i = index; i < vertexCount - 1; i++) {
            vertices[i] = vertices[i + 1];
        }
        vertices[vertexCount - 1] = null;
        vertexCount--;
        return true;
    }

    public boolean hasVertex(int id) {
        return findVertex(id) != null;
    }

    public String getVertexName(int id) {
        Vertex v = findVertex(id);
        return v != null ? v.name : null;
    }

    public boolean setVertexName(int id, String name) {
        Vertex v = findVertex(id);
        if (v != null) {
            v.name = name;
            return true;
        }
        return false;
    }

    public boolean addEdge(int from, int to, double weight) {
        if (from == to) {
            return false;
        }
        Vertex vFrom = findVertex(from);
        Vertex vTo = findVertex(to);
        if (vFrom == null || vTo == null) {
            return false;
        }
        if (vFrom.findEdgeTo(to) != null) {
            return false;
        }
        vFrom.addEdge(new Edge(from, to, weight));
        vTo.addEdge(new Edge(to, from, weight));
        edgeCount++;
        return true;
    }

    public boolean addEdge(int from, int to, double weight, double travelTime, double roadConditionWeight) {
        if (from == to) {
            return false;
        }
        Vertex vFrom = findVertex(from);
        Vertex vTo = findVertex(to);
        if (vFrom == null || vTo == null) {
            return false;
        }
        if (vFrom.findEdgeTo(to) != null) {
            return false;
        }
        vFrom.addEdge(new Edge(from, to, weight, travelTime, roadConditionWeight));
        vTo.addEdge(new Edge(to, from, weight, travelTime, roadConditionWeight));
        edgeCount++;
        return true;
    }

    public boolean removeEdge(int from, int to) {
        Vertex vFrom = findVertex(from);
        Vertex vTo = findVertex(to);
        if (vFrom == null || vTo == null) {
            return false;
        }
        boolean removed1 = vFrom.removeEdgeTo(to);
        boolean removed2 = vTo.removeEdgeTo(from);
        if (removed1 || removed2) {
            edgeCount--;
            return true;
        }
        return false;
    }

    public boolean hasEdge(int from, int to) {
        Vertex vFrom = findVertex(from);
        if (vFrom == null) return false;
        return vFrom.findEdgeTo(to) != null;
    }

    public double getEdgeWeight(int from, int to) {
        Vertex vFrom = findVertex(from);
        if (vFrom == null) {
            return -1.0;
        }
        Edge e = vFrom.findEdgeTo(to);
        return e != null ? e.weight : -1.0;
    }

    public double getEdgeTravelTime(int from, int to) {
        Vertex vFrom = findVertex(from);
        if (vFrom == null) {
            return -1.0;
        }
        Edge e = vFrom.findEdgeTo(to);
        return e != null ? e.travelTime : -1.0;
    }

    public double getEdgeRoadConditionWeight(int from, int to) {
        Vertex vFrom = findVertex(from);
        if (vFrom == null) {
            return -1.0;
        }
        Edge e = vFrom.findEdgeTo(to);
        return e != null ? e.roadConditionWeight : -1.0;
    }

    public boolean updateEdgeWeight(int from, int to, double weight) {
        Vertex vFrom = findVertex(from);
        Vertex vTo = findVertex(to);
        if (vFrom == null || vTo == null) return false;
        Edge e1 = vFrom.findEdgeTo(to);
        Edge e2 = vTo.findEdgeTo(from);
        if (e1 == null || e2 == null) return false;
        e1.weight = weight;
        e2.weight = weight;
        return true;
    }

    public int[] getNeighbors(int vertexId) {
        Vertex v = findVertex(vertexId);
        if (v == null) {
            return new int[0];
        }
        int[] result = new int[v.edgeCount];
        for (int i = 0; i < v.edgeCount; i++) {
            result[i] = v.edges[i].destination;
        }
        return result;
    }

    public double[] getNeighborWeights(int vertexId) {
        Vertex v = findVertex(vertexId);
        if (v == null) {
            return new double[0];
        }
        double[] result = new double[v.edgeCount];
        for (int i = 0; i < v.edgeCount; i++) {
            result[i] = v.edges[i].weight;
        }
        return result;
    }

    public int[] getAllVertexIds() {
        int[] result = new int[vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            result[i] = vertices[i].id;
        }
        return result;
    }

    public int[][] getAllEdges() {
        int[][] result = new int[edgeCount][2];
        int idx = 0;
        boolean[] processed = new boolean[vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            processed[i] = true;
            Vertex v = vertices[i];
            for (int j = 0; j < v.edgeCount; j++) {
                Edge e = v.edges[j];
                int destIdx = -1;
                for (int k = 0; k < vertexCount; k++) {
                    if (vertices[k].id == e.destination) {
                        destIdx = k;
                        break;
                    }
                }
                if (destIdx != -1 && !processed[destIdx]) {
                    result[idx][0] = e.source;
                    result[idx][1] = e.destination;
                    idx++;
                }
            }
        }
        int[][] trimmed = new int[idx][2];
        for (int i = 0; i < idx; i++) {
            trimmed[i][0] = result[i][0];
            trimmed[i][1] = result[i][1];
        }
        return trimmed;
    }

    public int vertexCount() {
        return vertexCount;
    }

    public int edgeCount() {
        return edgeCount;
    }

    public boolean isEmpty() {
        return vertexCount == 0;
    }

    public void clear() {
        for (int i = 0; i < vertexCount; i++) {
            vertices[i] = null;
        }
        vertexCount = 0;
        edgeCount = 0;
    }

    public int getDegree(int vertexId) {
        Vertex v = findVertex(vertexId);
        return v != null ? v.edgeCount : -1;
    }
}
