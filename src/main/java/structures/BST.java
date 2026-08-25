package structures;

import java.util.Objects;

/**
 * Owner: Sadiq Moro Ayariga
 * Implementation of Binary Search Tree (BST) from scratch.
 * Built-in Java collections are NOT used.
 *
 * Supported operations:
 * - Insert (Key-Value)
 * - Search & search path recording
 * - Inorder traversal (sorted key output)
 * - Delete
 * - Traversal trace logging
 */
public class BST<K extends Comparable<K>, V> {

    public static class Node<K, V> {
        public K key;
        public V value;
        public Node<K, V> left;
        public Node<K, V> right;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.left = null;
            this.right = null;
        }
    }

    private Node<K, V> root;
    private int size;

    public BST() {
        this.root = null;
        this.size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void clear() {
        root = null;
        size = 0;
    }

    /**
     * Inserts a key-value pair into the BST.
     * If the key exists, updates its value.
     */
    public void insert(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }
        root = insertRecursive(root, key, value);
    }

    private Node<K, V> insertRecursive(Node<K, V> current, K key, V value) {
        if (current == null) {
            size++;
            return new Node<>(key, value);
        }
        int cmp = key.compareTo(current.key);
        if (cmp < 0) {
            current.left = insertRecursive(current.left, key, value);
        } else if (cmp > 0) {
            current.right = insertRecursive(current.right, key, value);
        } else {
            current.value = value; // update existing
        }
        return current;
    }

    /**
     * Searches for a key in the BST.
     * @return value associated with key, or null if not found.
     */
    public V search(K key) {
        if (key == null) return null;
        Node<K, V> curr = root;
        while (curr != null) {
            int cmp = key.compareTo(curr.key);
            if (cmp == 0) return curr.value;
            curr = (cmp < 0) ? curr.left : curr.right;
        }
        return null;
    }

    public boolean contains(K key) {
        return search(key) != null;
    }

    /**
     * Produces the search path traversed to find a key.
     * Returns string formatted as "key1 -> key2 -> ... -> target" or "Not Found".
     */
    public String getSearchPath(K key) {
        if (key == null || root == null) return "Path: [Empty / Null]";
        StringBuilder sb = new StringBuilder("Path: ");
        Node<K, V> curr = root;
        boolean first = true;
        while (curr != null) {
            if (!first) sb.append(" -> ");
            sb.append(curr.key);
            first = false;

            int cmp = key.compareTo(curr.key);
            if (cmp == 0) {
                sb.append(" (Found)");
                return sb.toString();
            }
            curr = (cmp < 0) ? curr.left : curr.right;
        }
        sb.append(" -> NOT_FOUND");
        return sb.toString();
    }

    /**
     * Performs Inorder traversal and returns keys in sorted order.
     */
    @SuppressWarnings("unchecked")
    public K[] getSortedInorderKeys(K[] arrayPrototype) {
        K[] result = (K[]) java.lang.reflect.Array.newInstance(
                arrayPrototype.getClass().getComponentType(), size);
        int[] index = new int[]{0};
        inorderRecursive(root, result, index);
        return result;
    }

    private void inorderRecursive(Node<K, V> node, K[] result, int[] index) {
        if (node == null) return;
        inorderRecursive(node.left, result, index);
        result[index[0]++] = node.key;
        inorderRecursive(node.right, result, index);
    }

    /**
     * Deletes a key from the BST.
     */
    public boolean delete(K key) {
        if (key == null || !contains(key)) return false;
        root = deleteRecursive(root, key);
        size--;
        return true;
    }

    private Node<K, V> deleteRecursive(Node<K, V> current, K key) {
        if (current == null) return null;
        int cmp = key.compareTo(current.key);
        if (cmp < 0) {
            current.left = deleteRecursive(current.left, key);
        } else if (cmp > 0) {
            current.right = deleteRecursive(current.right, key);
        } else {
            // Node with 0 or 1 child
            if (current.left == null) return current.right;
            if (current.right == null) return current.left;

            // Node with 2 children: get inorder successor (smallest in right subtree)
            Node<K, V> smallest = findMin(current.right);
            current.key = smallest.key;
            current.value = smallest.value;
            current.right = deleteRecursive(current.right, smallest.key);
        }
        return current;
    }

    private Node<K, V> findMin(Node<K, V> node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    public Node<K, V> getRoot() {
        return root;
    }
}
