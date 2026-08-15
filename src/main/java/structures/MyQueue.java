package structures;

/**
 * Owner: Wiafe Franklin Asare
 *
 * Unbounded FIFO queue built on a singly linked chain of nodes. Implemented
 * from scratch — no built-in Java collections are used.
 *
 * This is the companion to {@link QueueCircular}. Same FIFO contract, different
 * trade-off:
 *
 *   QueueCircular - fixed slots, no per-item allocation, cache friendly, but a
 *                   capacity ceiling has to be chosen up front.
 *   MyQueue       - grows to whatever arrives, but pays one node object per
 *                   item and scatters those nodes across the heap.
 *
 * Project role: used where the number of waiting items genuinely is not known
 * ahead of time — the BFS/DFS frontier when exploring the road network, or a
 * full day's request backlog. QueueCircular is used for the bounded per-shift
 * dispatch buffer.
 *
 * Index model: head points at the item that leaves next, tail at the item that
 * arrived last.
 *
 * Invariant (see checkInvariant()):
 *   size == 0  <=>  head == null  <=>  tail == null
 *   size >  0  =>   tail.next == null and the chain from head is exactly size long
 *
 * Complexity: enqueue, dequeue, peek, size are all O(1) time. Space is O(n),
 * with a node object of overhead per item.
 */
public class MyQueue<T> {

    private static class Node<T> {
        T value;
        Node<T> next;

        Node(T value) {
            this.value = value;
            this.next = null;
        }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    private long enqueueOps;
    private long dequeueOps;

    public MyQueue() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    /**
     * Adds an item to the rear.
     *
     * @throws IllegalArgumentException if item is null
     */
    public void enqueue(T item) {
        if (item == null) {
            throw new IllegalArgumentException("Cannot enqueue null - use a real service request");
        }
        Node<T> node = new Node<T>(item);
        if (tail == null) {
            head = node;
            tail = node;
        } else {
            tail.next = node;
            tail = node;
        }
        size++;
        enqueueOps++;
    }

    /**
     * Same as enqueue. Present so this class and QueueCircular expose the same
     * API — a caller can swap one for the other without changing call sites.
     *
     * @return always true, this queue has no capacity ceiling
     * @throws IllegalArgumentException if item is null
     */
    public boolean offer(T item) {
        enqueue(item);
        return true;
    }

    /**
     * Removes and returns the item at the front.
     *
     * @throws IllegalStateException if the queue is empty
     */
    public T dequeue() {
        if (size == 0) {
            throw new IllegalStateException("Queue underflow: cannot dequeue from an empty queue");
        }
        T value = head.value;
        Node<T> removed = head;
        head = head.next;
        removed.next = null;
        if (head == null) {
            tail = null;
        }
        size--;
        dequeueOps++;
        return value;
    }

    /** Non-throwing dequeue. Returns null when the queue is empty. */
    public T poll() {
        return size == 0 ? null : dequeue();
    }

    /** Item at the front without removing it, or null when empty. */
    public T peek() {
        return head == null ? null : head.value;
    }

    /** Item at the rear without removing it, or null when empty. */
    public T peekRear() {
        return tail == null ? null : tail.value;
    }

    public boolean contains(T item) {
        if (item == null) {
            return false;
        }
        for (Node<T> current = head; current != null; current = current.next) {
            if (item.equals(current.value)) {
                return true;
            }
        }
        return false;
    }

    /** Items in FIFO order, front first. Returns an empty array when empty. */
    public Object[] toArray() {
        Object[] out = new Object[size];
        int i = 0;
        for (Node<T> current = head; current != null; current = current.next) {
            out[i++] = current.value;
        }
        return out;
    }

    /** Drops every node so the chain can be garbage collected. */
    public void clear() {
        Node<T> current = head;
        while (current != null) {
            Node<T> nextNode = current.next;
            current.next = null;
            current.value = null;
            current = nextNode;
        }
        head = null;
        tail = null;
        size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public long enqueueOps() {
        return enqueueOps;
    }

    public long dequeueOps() {
        return dequeueOps;
    }

    /**
     * Verifies the class invariant. Called by the unit tests after every
     * operation so a dangling head or tail shows up immediately.
     */
    public boolean checkInvariant() {
        if (size < 0) {
            return false;
        }
        if (size == 0) {
            return head == null && tail == null;
        }
        if (head == null || tail == null || tail.next != null) {
            return false;
        }
        int counted = 0;
        Node<T> current = head;
        Node<T> last = null;
        while (current != null && counted <= size) {
            last = current;
            current = current.next;
            counted++;
        }
        return counted == size && last == tail;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("head -> [");
        boolean first = true;
        for (Node<T> current = head; current != null; current = current.next) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(current.value);
            first = false;
        }
        sb.append("] <- tail  (size=").append(size).append(')');
        return sb.toString();
    }
}
