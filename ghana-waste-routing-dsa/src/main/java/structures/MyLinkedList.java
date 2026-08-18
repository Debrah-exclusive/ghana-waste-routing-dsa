package structures;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Owner: Emmanuel Thisara Otoo
 *
 * Doubly linked list built from scratch — no built-in Java collection is used.
 * (java.util.Iterator is an interface, not a collection; the exception types are
 * plain exceptions. Every node, link and traversal below is hand-written.)
 *
 * Project role: holds the ordered working set of service requests / collection
 * points that the dispatcher walks through. A linked list is used where the
 * cost that matters is inserting or removing at a known position (O(1) relink)
 * rather than random access by index (O(n) walk).
 *
 * Structure (sentinel-free, head and tail pointers):
 *
 *   null <- [ prev | A | next ] <-> [ prev | B | next ] <-> [ prev | C | next ] -> null
 *            ^head                                          ^tail
 *
 * Invariants maintained by every mutator:
 *   I1. size == number of reachable nodes from head via next
 *   I2. head == null  <=>  tail == null  <=>  size == 0
 *   I3. head.prev == null and tail.next == null
 *   I4. for every node n with n.next != null: n.next.prev == n
 *   I5. no node holds a null value (nulls are rejected on insert)
 *
 * Complexity: addFirst/addLast/removeFirst/removeLast O(1);
 *             get/set/add(index)/remove(index) O(n) but the walk starts from
 *             whichever end is closer, so it is O(min(i, n-i));
 *             contains/indexOf/remove(value) O(n).
 */
public class MyLinkedList<T> implements Iterable<T> {

    /** A single cell of the list. Package-private so tests can reason about it, never exposed. */
    private static class Node<T> {
        T value;
        Node<T> prev;
        Node<T> next;

        Node(T value) {
            this.value = value;
            this.prev = null;
            this.next = null;
        }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    /** Bumped on every structural change so a live iterator can fail fast. */
    private int modCount;

    public MyLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
        this.modCount = 0;
    }

    // ---------------------------------------------------------------- insert

    /** Inserts at the front. O(1). */
    public void addFirst(T value) {
        requireNonNullValue(value);
        Node<T> node = new Node<>(value);
        if (head == null) {
            head = node;
            tail = node;
        } else {
            node.next = head;
            head.prev = node;
            head = node;
        }
        size++;
        modCount++;
    }

    /** Inserts at the back. O(1). */
    public void addLast(T value) {
        requireNonNullValue(value);
        Node<T> node = new Node<>(value);
        if (tail == null) {
            head = node;
            tail = node;
        } else {
            node.prev = tail;
            tail.next = node;
            tail = node;
        }
        size++;
        modCount++;
    }

    /** Alias for addLast, so the class reads naturally at call sites. O(1). */
    public void add(T value) {
        addLast(value);
    }

    /**
     * Inserts so the new element ends up at position {@code index}.
     * Valid range is 0..size (size means "append"). O(min(index, size-index)).
     */
    public void add(int index, T value) {
        requireNonNullValue(value);
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException(
                    "add index " + index + " outside 0.." + size);
        }
        if (index == 0) {
            addFirst(value);
            return;
        }
        if (index == size) {
            addLast(value);
            return;
        }
        Node<T> after = nodeAt(index);
        Node<T> before = after.prev;
        Node<T> node = new Node<>(value);

        node.prev = before;
        node.next = after;
        before.next = node;
        after.prev = node;

        size++;
        modCount++;
    }

    // ---------------------------------------------------------------- access

    /** O(min(index, size-index)). */
    public T get(int index) {
        checkElementIndex(index);
        return nodeAt(index).value;
    }

    public T getFirst() {
        if (head == null) {
            throw new NoSuchElementException("getFirst on an empty list");
        }
        return head.value;
    }

    public T getLast() {
        if (tail == null) {
            throw new NoSuchElementException("getLast on an empty list");
        }
        return tail.value;
    }

    /** Overwrites the value at index and returns the old one. O(min(index, size-index)). */
    public T set(int index, T value) {
        requireNonNullValue(value);
        checkElementIndex(index);
        Node<T> node = nodeAt(index);
        T old = node.value;
        node.value = value;
        return old;
    }

    // ---------------------------------------------------------------- remove

    /** O(1). */
    public T removeFirst() {
        if (head == null) {
            throw new NoSuchElementException("removeFirst on an empty list");
        }
        T value = head.value;
        unlink(head);
        return value;
    }

    /** O(1). */
    public T removeLast() {
        if (tail == null) {
            throw new NoSuchElementException("removeLast on an empty list");
        }
        T value = tail.value;
        unlink(tail);
        return value;
    }

    /** Removes by position. O(min(index, size-index)). */
    public T remove(int index) {
        checkElementIndex(index);
        Node<T> node = nodeAt(index);
        T value = node.value;
        unlink(node);
        return value;
    }

    /** Removes the first node whose value equals {@code value}. Returns false if absent. O(n). */
    public boolean removeValue(T value) {
        if (value == null) {
            return false;
        }
        for (Node<T> cur = head; cur != null; cur = cur.next) {
            if (value.equals(cur.value)) {
                unlink(cur);
                return true;
            }
        }
        return false;
    }

    /** Drops every node. O(n) — links are cleared so the nodes can be collected. */
    public void clear() {
        Node<T> cur = head;
        while (cur != null) {
            Node<T> next = cur.next;
            cur.value = null;
            cur.prev = null;
            cur.next = null;
            cur = next;
        }
        head = null;
        tail = null;
        size = 0;
        modCount++;
    }

    // ---------------------------------------------------------------- search

    /** Position of the first equal value, or -1. O(n). */
    public int indexOf(T value) {
        if (value == null) {
            return -1;
        }
        int i = 0;
        for (Node<T> cur = head; cur != null; cur = cur.next) {
            if (value.equals(cur.value)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public boolean contains(T value) {
        return indexOf(value) >= 0;
    }

    // ---------------------------------------------------------------- shape

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /** Reverses the list in place by swapping each node's prev/next. O(n), no extra list. */
    public void reverse() {
        Node<T> cur = head;
        while (cur != null) {
            Node<T> next = cur.next;
            cur.next = cur.prev;
            cur.prev = next;
            cur = next;
        }
        Node<T> oldHead = head;
        head = tail;
        tail = oldHead;
        modCount++;
    }

    /** Snapshot of the values, front to back. Used by the sort algorithms. O(n). */
    public Object[] toArray() {
        Object[] out = new Object[size];
        int i = 0;
        for (Node<T> cur = head; cur != null; cur = cur.next) {
            out[i++] = cur.value;
        }
        return out;
    }

    /**
     * Same snapshot, typed for the comparison-based sorts.
     * Throws if any element is not Comparable.
     */
    public Comparable[] toComparableArray() {
        Comparable[] out = new Comparable[size];
        int i = 0;
        for (Node<T> cur = head; cur != null; cur = cur.next) {
            if (!(cur.value instanceof Comparable)) {
                throw new IllegalStateException(
                        "element at index " + i + " is not Comparable: " + cur.value);
            }
            out[i++] = (Comparable) cur.value;
        }
        return out;
    }

    // ---------------------------------------------------------------- iterate

    /** Forward iterator. Fails fast if the list is structurally changed mid-walk. */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> cursor = head;
            private Node<T> lastReturned = null;
            private int expectedModCount = modCount;

            @Override
            public boolean hasNext() {
                return cursor != null;
            }

            @Override
            public T next() {
                checkForComodification();
                if (cursor == null) {
                    throw new NoSuchElementException("iterator walked past the end");
                }
                lastReturned = cursor;
                cursor = cursor.next;
                return lastReturned.value;
            }

            @Override
            public void remove() {
                checkForComodification();
                if (lastReturned == null) {
                    throw new IllegalStateException("remove() before next(), or twice in a row");
                }
                unlink(lastReturned);
                lastReturned = null;
                expectedModCount = modCount;
            }

            private void checkForComodification() {
                if (expectedModCount != modCount) {
                    throw new ConcurrentModificationException(
                            "list changed while iterating");
                }
            }
        };
    }

    /** Backward iterator — the payoff of keeping prev pointers. */
    public Iterator<T> descendingIterator() {
        return new Iterator<T>() {
            private Node<T> cursor = tail;
            private final int expectedModCount = modCount;

            @Override
            public boolean hasNext() {
                return cursor != null;
            }

            @Override
            public T next() {
                if (expectedModCount != modCount) {
                    throw new ConcurrentModificationException("list changed while iterating");
                }
                if (cursor == null) {
                    throw new NoSuchElementException("iterator walked past the front");
                }
                T value = cursor.value;
                cursor = cursor.prev;
                return value;
            }
        };
    }

    // ---------------------------------------------------------------- output

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (Node<T> cur = head; cur != null; cur = cur.next) {
            sb.append(cur.value);
            if (cur.next != null) {
                sb.append(", ");
            }
        }
        return sb.append(']').toString();
    }

    /**
     * ASCII picture of the current links — used for the report diagrams and the
     * live defense, so the pointer structure can be shown rather than described.
     *
     *   null <- [Accra Central] <-> [Madina] <-> [Kaneshie] -> null
     */
    public String diagram() {
        if (head == null) {
            return "null (empty list: head == tail == null, size == 0)";
        }
        StringBuilder sb = new StringBuilder("null <- ");
        for (Node<T> cur = head; cur != null; cur = cur.next) {
            sb.append('[').append(cur.value).append(']');
            sb.append(cur.next != null ? " <-> " : " -> null");
        }
        return sb.toString();
    }

    /**
     * Re-checks invariants I1-I5 by walking both directions.
     * Called by the unit tests after every mutating scenario.
     */
    public boolean invariantsHold() {
        if (size == 0) {
            return head == null && tail == null;
        }
        if (head == null || tail == null) {
            return false;
        }
        if (head.prev != null || tail.next != null) {
            return false;
        }
        int forward = 0;
        Node<T> last = null;
        for (Node<T> cur = head; cur != null; cur = cur.next) {
            if (cur.value == null) {
                return false;
            }
            if (cur.prev != last) {
                return false;
            }
            last = cur;
            forward++;
            if (forward > size) {
                return false;
            }
        }
        if (last != tail || forward != size) {
            return false;
        }
        int backward = 0;
        for (Node<T> cur = tail; cur != null; cur = cur.prev) {
            backward++;
            if (backward > size) {
                return false;
            }
        }
        return backward == size;
    }

    // ---------------------------------------------------------------- helpers

    /** Walks from whichever end is closer to index. */
    private Node<T> nodeAt(int index) {
        if (index < size / 2) {
            Node<T> cur = head;
            for (int i = 0; i < index; i++) {
                cur = cur.next;
            }
            return cur;
        }
        Node<T> cur = tail;
        for (int i = size - 1; i > index; i--) {
            cur = cur.prev;
        }
        return cur;
    }

    /** Detaches one node and repairs its neighbours' links. O(1). */
    private void unlink(Node<T> node) {
        Node<T> before = node.prev;
        Node<T> after = node.next;

        if (before == null) {
            head = after;
        } else {
            before.next = after;
        }
        if (after == null) {
            tail = before;
        } else {
            after.prev = before;
        }

        node.prev = null;
        node.next = null;
        node.value = null;
        size--;
        modCount++;
    }

    private void checkElementIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(
                    "index " + index + " outside 0.." + (size - 1) + " (size " + size + ")");
        }
    }

    private void requireNonNullValue(T value) {
        if (value == null) {
            throw new IllegalArgumentException(
                    "null is not a valid list element (a service request is never null)");
        }
    }
}
