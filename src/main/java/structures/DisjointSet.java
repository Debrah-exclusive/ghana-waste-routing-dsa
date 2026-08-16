package structures;

import java.util.NoSuchElementException;

/**
 * Owner: Emmanuel Aseda Kow Bentsil
 *
 * Disjoint Set (Union-Find), built from scratch with no built-in Java
 * collections. Elements are stored in a manually-grown {@code Object[]}
 * (same doubling-capacity pattern as {@link Graph}'s vertex array), with
 * parallel {@code int[]} arrays for parent pointers, rank, and size —
 * looked up by a linear scan of the element array, exactly like
 * {@code Graph.findVertex}. That is O(n) per lookup, which is fine at this
 * project's scale (dataset briefs cap Locations at 50 and Roads at 100).
 *
 * <p>Supports both union-by-rank and union-by-size attachment strategies
 * (constructor flag), full path compression on find, and accurate component
 * sizes regardless of which attachment heuristic is active.
 *
 * <p>Used directly against location IDs from the Roads dataset
 * (fromLocationId, toLocationId) to drive Kruskal's cycle detection — see
 * {@code demo.DisjointSetKruskalTrace} for the connectivity trace evidence.
 *
 * <p>Complexity: with path compression + union by rank/size, m operations on
 * n elements run in O(m * alpha(n)) amortised for the union/find logic
 * itself (alpha = inverse Ackermann, effectively constant); the linear-scan
 * lookup by element value adds an extra O(n) factor per call, which is the
 * deliberate, documented trade-off described above.
 */
public class DisjointSet<T> {

    private static final int DEFAULT_CAPACITY = 16;

    private Object[] elements;
    private int[] parent;
    private int[] rank;
    private int[] size;
    private int count;
    private int numComponents;
    private final boolean unionBySize;

    /** Union-by-rank (classic strategy), default initial capacity. */
    public DisjointSet() {
        this(false, DEFAULT_CAPACITY);
    }

    /**
     * @param unionBySize if true, union attaches the smaller tree (by element
     *                    count) under the larger one; if false, union
     *                    attaches by rank (an upper bound on tree height).
     */
    public DisjointSet(boolean unionBySize) {
        this(unionBySize, DEFAULT_CAPACITY);
    }

    public DisjointSet(boolean unionBySize, int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("initialCapacity must be > 0");
        }
        this.elements = new Object[initialCapacity];
        this.parent = new int[initialCapacity];
        this.rank = new int[initialCapacity];
        this.size = new int[initialCapacity];
        this.count = 0;
        this.numComponents = 0;
        this.unionBySize = unionBySize;
    }

    private void ensureCapacity() {
        if (count < elements.length) {
            return;
        }
        int newCap = elements.length * 2;
        Object[] newElements = new Object[newCap];
        int[] newParent = new int[newCap];
        int[] newRank = new int[newCap];
        int[] newSize = new int[newCap];
        for (int i = 0; i < count; i++) {
            newElements[i] = elements[i];
            newParent[i] = parent[i];
            newRank[i] = rank[i];
            newSize[i] = size[i];
        }
        elements = newElements;
        parent = newParent;
        rank = newRank;
        size = newSize;
    }

    /** Linear scan for the index holding x, or -1 if x hasn't been registered. */
    private int indexOf(T x) {
        for (int i = 0; i < count; i++) {
            if (elements[i] != null && elements[i].equals(x)) {
                return i;
            }
        }
        return -1;
    }

    private int requireIndex(T x) {
        if (x == null) {
            throw new IllegalArgumentException("Element must be non-null.");
        }
        int idx = indexOf(x);
        if (idx == -1) {
            throw new NoSuchElementException(
                    "Element '" + x + "' was never registered - call makeSet(" + x + ") first.");
        }
        return idx;
    }

    /**
     * Registers a new singleton set {x}. Idempotent: calling it again on an
     * element already present is a harmless no-op (real usage calls
     * makeSet once per edge endpoint while scanning the Roads CSV, so
     * duplicates are expected).
     *
     * @throws IllegalArgumentException if x is null
     */
    public void makeSet(T x) {
        if (x == null) {
            throw new IllegalArgumentException("Cannot makeSet(null): element must be non-null.");
        }
        if (indexOf(x) != -1) {
            return;
        }
        ensureCapacity();
        elements[count] = x;
        parent[count] = count;
        rank[count] = 0;
        size[count] = 1;
        count++;
        numComponents++;
    }

    /** Finds the root index of i, applying full path compression along the way. */
    private int findRootIndex(int i) {
        int root = i;
        while (parent[root] != root) {
            root = parent[root];
        }
        int current = i;
        while (parent[current] != root) {
            int next = parent[current];
            parent[current] = root;
            current = next;
        }
        return root;
    }

    /**
     * Finds the representative element for the set containing x.
     *
     * @throws NoSuchElementException if x was never registered via makeSet
     */
    @SuppressWarnings("unchecked")
    public T find(T x) {
        int idx = requireIndex(x);
        int rootIdx = findRootIndex(idx);
        return (T) elements[rootIdx];
    }

    /**
     * Merges the sets containing x and y.
     *
     * @return true if x and y were in different sets and have now been
     *         merged; false if they were already in the same set (the
     *         signal Kruskal's algorithm uses to reject an edge as a cycle)
     * @throws NoSuchElementException if x or y was never registered via makeSet
     */
    public boolean union(T x, T y) {
        int ix = requireIndex(x);
        int iy = requireIndex(y);
        int rootX = findRootIndex(ix);
        int rootY = findRootIndex(iy);

        if (rootX == rootY) {
            return false;
        }

        int winner;
        int loser;

        if (unionBySize) {
            if (size[rootX] >= size[rootY]) {
                winner = rootX;
                loser = rootY;
            } else {
                winner = rootY;
                loser = rootX;
            }
        } else {
            if (rank[rootX] > rank[rootY]) {
                winner = rootX;
                loser = rootY;
            } else if (rank[rootY] > rank[rootX]) {
                winner = rootY;
                loser = rootX;
            } else {
                winner = rootX;
                loser = rootY;
                rank[winner]++;
            }
        }

        parent[loser] = winner;
        size[winner] += size[loser];
        numComponents--;
        return true;
    }

    /**
     * @throws NoSuchElementException if x or y was never registered via makeSet
     */
    public boolean connected(T x, T y) {
        int ix = requireIndex(x);
        int iy = requireIndex(y);
        return findRootIndex(ix) == findRootIndex(iy);
    }

    /**
     * @throws NoSuchElementException if x was never registered via makeSet
     */
    public int componentSize(T x) {
        int idx = requireIndex(x);
        int rootIdx = findRootIndex(idx);
        return size[rootIdx];
    }

    /** Number of disjoint sets currently active across all registered elements. */
    public int componentCount() {
        return numComponents;
    }

    /** Number of elements registered via makeSet. */
    public int elementCount() {
        return count;
    }

    /** True if x has been registered via makeSet. */
    public boolean contains(T x) {
        return indexOf(x) != -1;
    }
}
