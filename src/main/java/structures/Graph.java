package structures;

/**
 * Owner: Ivan Kwamena Johnson and Elsie Atsu
 * TODO: implement from scratch (built-in Java collections not allowed for this class).
 * Required evidence: normal-case, boundary-case, invalid-input unit tests + trace table.
 *
 * Implementation: Weighted, undirected ADJACENCY MATRIX using 2D primitive arrays.
 * Three parallel matrices: distance (km), travelTime (min), roadConditionWeight.
 * Vertex IDs are user-supplied integers; we maintain a parallel idArray for ID<->index mapping.
 */
public class Graph {

    private static final int DEFAULT_CAPACITY = 16;
    private static final double NO_EDGE = -1.0;

    private int[] idArray;
    private String[] nameArray;
    private int size;
    private int capacity;

    private double[][] distanceMatrix;
    private double[][] travelTimeMatrix;
    private double[][] conditionMatrix;
    private int edgeCount;

    public Graph() {
        this.capacity = DEFAULT_CAPACITY;
        this.size = 0;
        this.edgeCount = 0;
        this.idArray = new int[capacity];
        this.nameArray = new String[capacity];
        this.distanceMatrix = new double[capacity][capacity];
        this.travelTimeMatrix = new double[capacity][capacity];
        this.conditionMatrix = new double[capacity][capacity];
        initMatrix(distanceMatrix);
        initMatrix(travelTimeMatrix);
        initMatrix(conditionMatrix);
    }

    private static void initMatrix(double[][] m) {
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[i].length; j++) {
                m[i][j] = NO_EDGE;
            }
        }
    }

    private void ensureCapacity(int minCapacity) {
        if (minCapacity <= capacity) return;
        int newCap = capacity * 2;
        while (newCap < minCapacity) newCap *= 2;

        int[] newIds = new int[newCap];
        String[] newNames = new String[newCap];
        for (int i = 0; i < size; i++) {
            newIds[i] = idArray[i];
            newNames[i] = nameArray[i];
        }

        double[][] newDist = new double[newCap][newCap];
        double[][] newTime = new double[newCap][newCap];
        double[][] newCond = new double[newCap][newCap];
        initMatrix(newDist);
        initMatrix(newTime);
        initMatrix(newCond);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                newDist[i][j] = distanceMatrix[i][j];
                newTime[i][j] = travelTimeMatrix[i][j];
                newCond[i][j] = conditionMatrix[i][j];
            }
        }

        this.capacity = newCap;
        this.idArray = newIds;
        this.nameArray = newNames;
        this.distanceMatrix = newDist;
        this.travelTimeMatrix = newTime;
        this.conditionMatrix = newCond;
    }

    private int indexOf(int vertexId) {
        for (int i = 0; i < size; i++) {
            if (idArray[i] == vertexId) return i;
        }
        return -1;
    }

    public boolean addVertex(int id) {
        if (indexOf(id) != -1) return false;
        ensureCapacity(size + 1);
        idArray[size] = id;
        nameArray[size] = null;
        distanceMatrix[size][size] = 0.0;
        travelTimeMatrix[size][size] = 0.0;
        conditionMatrix[size][size] = 0.0;
        size++;
        return true;
    }

    public boolean addVertex(int id, String name) {
        if (indexOf(id) != -1) return false;
        ensureCapacity(size + 1);
        idArray[size] = id;
        nameArray[size] = name;
        distanceMatrix[size][size] = 0.0;
        travelTimeMatrix[size][size] = 0.0;
        conditionMatrix[size][size] = 0.0;
        size++;
        return true;
    }

    public boolean removeVertex(int id) {
        int idx = indexOf(id);
        if (idx == -1) return false;

        int removedEdges = 0;
        for (int j = 0; j < size; j++) {
            if (j != idx && distanceMatrix[idx][j] != NO_EDGE) removedEdges++;
        }
        edgeCount -= removedEdges;

        for (int i = idx; i < size - 1; i++) {
            idArray[i] = idArray[i + 1];
            nameArray[i] = nameArray[i + 1];
        }
        idArray[size - 1] = 0;
        nameArray[size - 1] = null;

        for (int i = 0; i < size; i++) {
            for (int j = idx; j < size - 1; j++) {
                distanceMatrix[i][j] = distanceMatrix[i][j + 1];
                travelTimeMatrix[i][j] = travelTimeMatrix[i][j + 1];
                conditionMatrix[i][j] = conditionMatrix[i][j + 1];
            }
            distanceMatrix[i][size - 1] = NO_EDGE;
            travelTimeMatrix[i][size - 1] = NO_EDGE;
            conditionMatrix[i][size - 1] = NO_EDGE;
        }
        for (int j = 0; j < size; j++) {
            for (int i = idx; i < size - 1; i++) {
                distanceMatrix[i][j] = distanceMatrix[i + 1][j];
                travelTimeMatrix[i][j] = travelTimeMatrix[i + 1][j];
                conditionMatrix[i][j] = conditionMatrix[i + 1][j];
            }
            distanceMatrix[size - 1][j] = NO_EDGE;
            travelTimeMatrix[size - 1][j] = NO_EDGE;
            conditionMatrix[size - 1][j] = NO_EDGE;
        }

        size--;
        distanceMatrix[size][size] = NO_EDGE;
        travelTimeMatrix[size][size] = NO_EDGE;
        conditionMatrix[size][size] = NO_EDGE;
        return true;
    }

    public boolean hasVertex(int id) {
        return indexOf(id) != -1;
    }

    public String getVertexName(int id) {
        int idx = indexOf(id);
        return idx == -1 ? null : nameArray[idx];
    }

    public boolean setVertexName(int id, String name) {
        int idx = indexOf(id);
        if (idx == -1) return false;
        nameArray[idx] = name;
        return true;
    }

    public boolean addEdge(int from, int to, double distanceKm) {
        return addEdge(from, to, distanceKm, 0.0, 1.0);
    }

    public boolean addEdge(int from, int to, double distanceKm, double travelTimeMin, double conditionWeight) {
        if (from == to) return false;
        int i = indexOf(from);
        int j = indexOf(to);
        if (i == -1 || j == -1) return false;
        if (distanceMatrix[i][j] != NO_EDGE) return false;
        distanceMatrix[i][j] = distanceKm;
        distanceMatrix[j][i] = distanceKm;
        travelTimeMatrix[i][j] = travelTimeMin;
        travelTimeMatrix[j][i] = travelTimeMin;
        conditionMatrix[i][j] = conditionWeight;
        conditionMatrix[j][i] = conditionWeight;
        edgeCount++;
        return true;
    }

    public boolean removeEdge(int from, int to) {
        if (from == to) return false;
        int i = indexOf(from);
        int j = indexOf(to);
        if (i == -1 || j == -1) return false;
        if (distanceMatrix[i][j] == NO_EDGE) return false;
        distanceMatrix[i][j] = NO_EDGE;
        distanceMatrix[j][i] = NO_EDGE;
        travelTimeMatrix[i][j] = NO_EDGE;
        travelTimeMatrix[j][i] = NO_EDGE;
        conditionMatrix[i][j] = NO_EDGE;
        conditionMatrix[j][i] = NO_EDGE;
        edgeCount--;
        return true;
    }

    public boolean hasEdge(int from, int to) {
        if (from == to) return false;
        int i = indexOf(from);
        int j = indexOf(to);
        if (i == -1 || j == -1) return false;
        return distanceMatrix[i][j] != NO_EDGE;
    }

    public double getEdgeWeight(int from, int to) {
        int i = indexOf(from);
        int j = indexOf(to);
        if (i == -1 || j == -1) return NO_EDGE;
        return distanceMatrix[i][j];
    }

    public double getEdgeTravelTime(int from, int to) {
        int i = indexOf(from);
        int j = indexOf(to);
        if (i == -1 || j == -1) return NO_EDGE;
        return travelTimeMatrix[i][j];
    }

    public double getEdgeRoadConditionWeight(int from, int to) {
        int i = indexOf(from);
        int j = indexOf(to);
        if (i == -1 || j == -1) return NO_EDGE;
        return conditionMatrix[i][j];
    }

    public boolean updateEdgeWeight(int from, int to, double distanceKm) {
        int i = indexOf(from);
        int j = indexOf(to);
        if (i == -1 || j == -1) return false;
        if (distanceMatrix[i][j] == NO_EDGE) return false;
        distanceMatrix[i][j] = distanceKm;
        distanceMatrix[j][i] = distanceKm;
        return true;
    }

    public int[] getNeighbors(int vertexId) {
        int idx = indexOf(vertexId);
        if (idx == -1) return new int[0];
        int count = 0;
        for (int j = 0; j < size; j++) {
            if (j != idx && distanceMatrix[idx][j] != NO_EDGE) count++;
        }
        int[] result = new int[count];
        int k = 0;
        for (int j = 0; j < size; j++) {
            if (j != idx && distanceMatrix[idx][j] != NO_EDGE) {
                result[k++] = idArray[j];
            }
        }
        return result;
    }

    public double[] getNeighborWeights(int vertexId) {
        int idx = indexOf(vertexId);
        if (idx == -1) return new double[0];
        int count = 0;
        for (int j = 0; j < size; j++) {
            if (j != idx && distanceMatrix[idx][j] != NO_EDGE) count++;
        }
        double[] result = new double[count];
        int k = 0;
        for (int j = 0; j < size; j++) {
            if (j != idx && distanceMatrix[idx][j] != NO_EDGE) {
                result[k++] = distanceMatrix[idx][j];
            }
        }
        return result;
    }

    public int[] getAllVertexIds() {
        int[] result = new int[size];
        for (int i = 0; i < size; i++) result[i] = idArray[i];
        return result;
    }

    public int[][] getAllEdges() {
        int[][] result = new int[edgeCount][2];
        int idx = 0;
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                if (distanceMatrix[i][j] != NO_EDGE) {
                    result[idx][0] = idArray[i];
                    result[idx][1] = idArray[j];
                    idx++;
                }
            }
        }
        return result;
    }

    public int vertexCount() {
        return size;
    }

    public int edgeCount() {
        return edgeCount;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        for (int i = 0; i < size; i++) {
            idArray[i] = 0;
            nameArray[i] = null;
            for (int j = 0; j < size; j++) {
                distanceMatrix[i][j] = NO_EDGE;
                travelTimeMatrix[i][j] = NO_EDGE;
                conditionMatrix[i][j] = NO_EDGE;
            }
        }
        size = 0;
        edgeCount = 0;
    }

    public int getDegree(int vertexId) {
        int idx = indexOf(vertexId);
        if (idx == -1) return -1;
        int deg = 0;
        for (int j = 0; j < size; j++) {
            if (j != idx && distanceMatrix[idx][j] != NO_EDGE) deg++;
        }
        return deg;
    }

    public int capacity() {
        return capacity;
    }
}
