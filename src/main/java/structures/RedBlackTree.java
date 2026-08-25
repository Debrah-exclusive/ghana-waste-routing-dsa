package structures;

/**
 * Owner: Adam Mohammed
 * Self-balancing Red-Black Tree implementation from scratch.
 * Built-in Java collections are NOT used.
 *
 * Properties:
 * 1. Every node is RED or BLACK.
 * 2. Root is always BLACK.
 * 3. All leaves (NIL) are BLACK.
 * 4. If a node is RED, both children are BLACK (no consecutive red nodes).
 * 5. Every path from a node to descendant leaves contains the same number of BLACK nodes.
 */
public class RedBlackTree<K extends Comparable<K>, V> {

    public static final boolean RED = true;
    public static final boolean BLACK = false;

    public static class Node<K, V> {
        public K key;
        public V value;
        public Node<K, V> left, right, parent;
        public boolean color;

        public Node(K key, V value, boolean color, Node<K, V> nil) {
            this.key = key;
            this.value = value;
            this.color = color;
            this.left = nil;
            this.right = nil;
            this.parent = nil;
        }
    }

    private final Node<K, V> NIL;
    private Node<K, V> root;
    private int size;
    private StringBuilder rotationLog;

    public RedBlackTree() {
        NIL = new Node<>(null, null, BLACK, null);
        NIL.left = NIL;
        NIL.right = NIL;
        NIL.parent = NIL;
        root = NIL;
        size = 0;
        rotationLog = new StringBuilder();
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public String getRotationLog() {
        return rotationLog.toString();
    }

    public void clearRotationLog() {
        rotationLog.setLength(0);
    }

    public V search(K key) {
        if (key == null) return null;
        Node<K, V> current = root;
        while (current != NIL) {
            int cmp = key.compareTo(current.key);
            if (cmp == 0) return current.value;
            current = (cmp < 0) ? current.left : current.right;
        }
        return null;
    }

    public void insert(K key, V value) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null");

        Node<K, V> z = new Node<>(key, value, RED, NIL);
        Node<K, V> y = NIL;
        Node<K, V> x = root;

        while (x != NIL) {
            y = x;
            int cmp = z.key.compareTo(x.key);
            if (cmp < 0) {
                x = x.left;
            } else if (cmp > 0) {
                x = x.right;
            } else {
                x.value = value; // update value
                return;
            }
        }

        z.parent = y;
        if (y == NIL) {
            root = z;
        } else if (z.key.compareTo(y.key) < 0) {
            y.left = z;
        } else {
            y.right = z;
        }

        z.left = NIL;
        z.right = NIL;
        z.color = RED;
        size++;

        insertFixup(z);
    }

    private void insertFixup(Node<K, V> z) {
        while (z.parent.color == RED) {
            if (z.parent == z.parent.parent.left) {
                Node<K, V> y = z.parent.parent.right;
                if (y.color == RED) {
                    z.parent.color = BLACK;
                    y.color = BLACK;
                    z.parent.parent.color = RED;
                    z = z.parent.parent;
                } else {
                    if (z == z.parent.right) {
                        z = z.parent;
                        leftRotate(z);
                    }
                    z.parent.color = BLACK;
                    z.parent.parent.color = RED;
                    rightRotate(z.parent.parent);
                }
            } else {
                Node<K, V> y = z.parent.parent.left;
                if (y.color == RED) {
                    z.parent.color = BLACK;
                    y.color = BLACK;
                    z.parent.parent.color = RED;
                    z = z.parent.parent;
                } else {
                    if (z == z.parent.left) {
                        z = z.parent;
                        rightRotate(z);
                    }
                    z.parent.color = BLACK;
                    z.parent.parent.color = RED;
                    leftRotate(z.parent.parent);
                }
            }
        }
        root.color = BLACK;
    }

    private void leftRotate(Node<K, V> x) {
        rotationLog.append("Left-Rotate around ").append(x.key).append("\n");
        Node<K, V> y = x.right;
        x.right = y.left;
        if (y.left != NIL) {
            y.left.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == NIL) {
            root = y;
        } else if (x == x.parent.left) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }
        y.left = x;
        x.parent = y;
    }

    private void rightRotate(Node<K, V> y) {
        rotationLog.append("Right-Rotate around ").append(y.key).append("\n");
        Node<K, V> x = y.left;
        y.left = x.right;
        if (x.right != NIL) {
            x.right.parent = y;
        }
        x.parent = y.parent;
        if (y.parent == NIL) {
            root = x;
        } else if (y == y.parent.right) {
            y.parent.right = x;
        } else {
            y.parent.left = x;
        }
        x.right = y;
        y.parent = x;
    }

    public String toDiagram() {
        if (root == NIL) return "Empty Tree";
        StringBuilder sb = new StringBuilder();
        buildDiagram(root, "", true, sb);
        return sb.toString();
    }

    private void buildDiagram(Node<K, V> node, String prefix, boolean isTail, StringBuilder sb) {
        if (node == NIL) return;
        sb.append(prefix).append(isTail ? "└── " : "├── ")
          .append(node.key).append(node.color == RED ? " (R)" : " (B)").append("\n");
        buildDiagram(node.left, prefix + (isTail ? "    " : "│   "), false, sb);
        buildDiagram(node.right, prefix + (isTail ? "    " : "│   "), true, sb);
    }
}
