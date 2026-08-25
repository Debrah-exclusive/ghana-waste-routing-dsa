package structures;

/**
 * Owner: Thelma Osei-Fiagbor
 * Custom B-Tree implementation from scratch (degree T = 2, i.e. 2-3-4 tree structure).
 * Built-in Java collections are NOT used.
 *
 * Supported operations:
 * - Insert (node splitting when full)
 * - Search & search trace recording
 * - Traversals
 * - Split explanation logging
 */
public class BTree<K extends Comparable<K>, V> {

    public static final int T = 2; // Minimum degree (max keys = 2*T - 1 = 3)

    public static class BTreeNode<K, V> {
        public int numKeys;
        @SuppressWarnings("unchecked")
        public K[] keys = (K[]) new Comparable[2 * T - 1];
        @SuppressWarnings("unchecked")
        public V[] values = (V[]) new Object[2 * T - 1];
        @SuppressWarnings("unchecked")
        public BTreeNode<K, V>[] children = (BTreeNode<K, V>[]) new BTreeNode[2 * T];
        public boolean isLeaf;

        public BTreeNode(boolean isLeaf) {
            this.numKeys = 0;
            this.isLeaf = isLeaf;
        }
    }

    private BTreeNode<K, V> root;
    private int size;
    private StringBuilder splitLog;

    public BTree() {
        this.root = new BTreeNode<>(true);
        this.size = 0;
        this.splitLog = new StringBuilder();
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public String getSplitLog() {
        return splitLog.toString();
    }

    public void clearSplitLog() {
        splitLog.setLength(0);
    }

    public V search(K key) {
        if (key == null) return null;
        return searchRecursive(root, key);
    }

    private V searchRecursive(BTreeNode<K, V> node, K key) {
        int i = 0;
        while (i < node.numKeys && key.compareTo(node.keys[i]) > 0) {
            i++;
        }
        if (i < node.numKeys && key.compareTo(node.keys[i]) == 0) {
            return node.values[i];
        }
        if (node.isLeaf) {
            return null;
        }
        return searchRecursive(node.children[i], key);
    }

    public String getSearchTrace(K key) {
        if (key == null || isEmpty()) return "Search trace: Empty/Null";
        StringBuilder sb = new StringBuilder("Search trace: ");
        searchTraceRecursive(root, key, sb);
        return sb.toString();
    }

    private void searchTraceRecursive(BTreeNode<K, V> node, K key, StringBuilder sb) {
        sb.append("[Node keys: ");
        for (int k = 0; k < node.numKeys; k++) {
            if (k > 0) sb.append(", ");
            sb.append(node.keys[k]);
        }
        sb.append("] ");

        int i = 0;
        while (i < node.numKeys && key.compareTo(node.keys[i]) > 0) {
            i++;
        }
        if (i < node.numKeys && key.compareTo(node.keys[i]) == 0) {
            sb.append("-> Found ").append(key);
            return;
        }
        if (node.isLeaf) {
            sb.append("-> Key NOT FOUND in leaf");
            return;
        }
        sb.append("-> Down child ").append(i).append(" -> ");
        searchTraceRecursive(node.children[i], key, sb);
    }

    public void insert(K key, V value) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");

        // If key exists, update value
        if (search(key) != null) {
            updateValueRecursive(root, key, value);
            return;
        }

        BTreeNode<K, V> r = root;
        if (r.numKeys == 2 * T - 1) { // Full node split
            BTreeNode<K, V> s = new BTreeNode<>(false);
            root = s;
            s.children[0] = r;
            splitChild(s, 0, r);
            insertNonFull(s, key, value);
        } else {
            insertNonFull(r, key, value);
        }
        size++;
    }

    private void updateValueRecursive(BTreeNode<K, V> node, K key, V value) {
        int i = 0;
        while (i < node.numKeys && key.compareTo(node.keys[i]) > 0) i++;
        if (i < node.numKeys && key.compareTo(node.keys[i]) == 0) {
            node.values[i] = value;
            return;
        }
        if (!node.isLeaf) updateValueRecursive(node.children[i], key, value);
    }

    private void splitChild(BTreeNode<K, V> parent, int i, BTreeNode<K, V> childToSplit) {
        splitLog.append(String.format("Splitting node with keys [%s, %s, %s] around median key %s\n",
                childToSplit.keys[0], childToSplit.keys[1], childToSplit.keys[2], childToSplit.keys[T - 1]));

        BTreeNode<K, V> z = new BTreeNode<>(childToSplit.isLeaf);
        z.numKeys = T - 1;

        // Copy last T-1 keys and values to z
        for (int j = 0; j < T - 1; j++) {
            z.keys[j] = childToSplit.keys[j + T];
            z.values[j] = childToSplit.values[j + T];
        }

        if (!childToSplit.isLeaf) {
            for (int j = 0; j < T; j++) {
                z.children[j] = childToSplit.children[j + T];
            }
        }

        childToSplit.numKeys = T - 1;

        for (int j = parent.numKeys; j >= i + 1; j--) {
            parent.children[j + 1] = parent.children[j];
        }
        parent.children[i + 1] = z;

        for (int j = parent.numKeys - 1; j >= i; j--) {
            parent.keys[j + 1] = parent.keys[j];
            parent.values[j + 1] = parent.values[j];
        }

        parent.keys[i] = childToSplit.keys[T - 1];
        parent.values[i] = childToSplit.values[T - 1];
        parent.numKeys++;
    }

    private void insertNonFull(BTreeNode<K, V> node, K key, V value) {
        int i = node.numKeys - 1;
        if (node.isLeaf) {
            while (i >= 0 && key.compareTo(node.keys[i]) < 0) {
                node.keys[i + 1] = node.keys[i];
                node.values[i + 1] = node.values[i];
                i--;
            }
            node.keys[i + 1] = key;
            node.values[i + 1] = value;
            node.numKeys++;
        } else {
            while (i >= 0 && key.compareTo(node.keys[i]) < 0) {
                i--;
            }
            i++;
            if (node.children[i].numKeys == 2 * T - 1) {
                splitChild(node, i, node.children[i]);
                if (key.compareTo(node.keys[i]) > 0) {
                    i++;
                }
            }
            insertNonFull(node.children[i], key, value);
        }
    }
}
