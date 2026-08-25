package structures;

/**
 * Owner: Emmanuel Aseda Kow Bentsil
 * Disjoint Set (Union-Find) with path compression and rank optimization.
 * Built from scratch using primitive arrays.
 */
public class DisjointSet {

    private int[] parent;
    private int[] rank;
    private int capacity;

    public DisjointSet(int maxElements) {
        this.capacity = maxElements;
        this.parent = new int[maxElements];
        this.rank = new int[maxElements];
        for (int i = 0; i < maxElements; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
    }

    public void makeSet(int i) {
        if (i < 0 || i >= capacity) return;
        parent[i] = i;
        rank[i] = 0;
    }

    public int find(int i) {
        if (i < 0 || i >= capacity) {
            throw new IllegalArgumentException("Index out of bounds for DisjointSet: " + i);
        }
        if (parent[i] != i) {
            parent[i] = find(parent[i]); // Path compression
        }
        return parent[i];
    }

    public boolean union(int i, int j) {
        int rootI = find(i);
        int rootJ = find(j);
        if (rootI == rootJ) return false;

        if (rank[rootI] < rank[rootJ]) {
            parent[rootI] = rootJ;
        } else if (rank[rootI] > rank[rootJ]) {
            parent[rootJ] = rootI;
        } else {
            parent[rootJ] = rootI;
            rank[rootI]++;
        }
        return true;
    }

    public boolean connected(int i, int j) {
        return find(i) == find(j);
    }
}
