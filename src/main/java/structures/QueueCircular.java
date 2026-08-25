package structures;

/**
 * Owner: Wiafe Franklin Asare
 *
 * Fixed-capacity FIFO queue built on a circular (ring) array. Implemented from
 * scratch — no built-in Java collections are used.
 *
 * Project role: holds service requests in strict arrival order while they wait
 * for a truck. A ring array is the right shape for that job because a depot
 * shift handles a bounded number of pending requests, and the array reuses the
 * slots freed by dequeued requests instead of leaking them.
 *
 * Index model
 *   front - index of the request that will leave next
 *   rear  - index of the request that arrived last
 *   size  - number of requests currently held
 *
 * Both indices only ever move forward, wrapping from capacity-1 back to 0.
 * When the queue is empty, rear sits one slot behind front (modulo capacity),
 * which is why a new queue starts at front=0, rear=capacity-1: the very first
 * enqueue advances rear to slot 0.
 *
 * Invariant (holds after every public operation, see checkInvariant()):
 *   rear == (front + size - 1) mod capacity
 *   the size slots starting at front are non-null, every other slot is null
 *
 * Complexity: enqueue, dequeue, peek, size are all O(1) time, O(1) extra space.
 * The whole structure is O(capacity) space. A growable queue doubles its array
 * on overflow, which is O(n) for that one operation and O(1) amortised.
 *
 * Required evidence: normal-case, boundary-case, invalid-input unit tests + trace table.
 */
public class QueueCircular<T> {

    private static final int DEFAULT_CAPACITY = 8;

    private Object[] slots;
    private int front;
    private int rear;
    private int size;
    private final boolean growable;

    // Counters kept for the report's trace table and performance section.
    private long enqueueOps;
    private long dequeueOps;
    private long wrapArounds;
    private long rejectedEnqueues;
    private long growths;

    /** Fixed-capacity queue with the default capacity of 8. */
    public QueueCircular() {
        this(DEFAULT_CAPACITY, false);
    }

    /** Fixed-capacity queue. Enqueueing into a full queue is rejected. */
    public QueueCircular(int capacity) {
        this(capacity, false);
    }

    /**
     * @param capacity number of slots, must be at least 1
     * @param growable when true the array doubles instead of rejecting an
     *                 enqueue into a full queue
     */
    public QueueCircular(int capacity, boolean growable) {
        if (capacity < 1) {
            throw new IllegalArgumentException("Capacity must be at least 1, got " + capacity);
        }
        this.slots = new Object[capacity];
        this.front = 0;
        this.rear = capacity - 1;
        this.size = 0;
        this.growable = growable;
    }

    /** Next index clockwise around the ring. Pure — does not count wraps. */
    private int next(int index) {
        return index + 1 == slots.length ? 0 : index + 1;
    }

    /**
     * Adds an item to the rear.
     *
     * @throws IllegalArgumentException if item is null
     * @throws IllegalStateException    if the queue is full and not growable
     */
    public void enqueue(T item) {
        if (item == null) {
            throw new IllegalArgumentException("Cannot enqueue null - use a real service request");
        }
        if (size == slots.length) {
            if (!growable) {
                rejectedEnqueues++;
                throw new IllegalStateException(
                        "Queue overflow: capacity " + slots.length + " is full");
            }
            grow();
        }
        // A roll only counts as a wrap when it carries rear over live data. On
        // an empty queue rear is merely parked one slot behind front, so the
        // first enqueue rolling capacity-1 -> 0 is not a wrap-around.
        boolean rolled = rear == slots.length - 1 && size > 0;
        rear = next(rear);
        if (rolled) {
            wrapArounds++;
        }
        slots[rear] = item;
        size++;
        enqueueOps++;
    }

    /**
     * Non-throwing enqueue.
     *
     * @return false if the queue is full and not growable, true otherwise
     * @throws IllegalArgumentException if item is null
     */
    public boolean offer(T item) {
        if (item == null) {
            throw new IllegalArgumentException("Cannot enqueue null - use a real service request");
        }
        if (size == slots.length && !growable) {
            rejectedEnqueues++;
            return false;
        }
        enqueue(item);
        return true;
    }

    /**
     * Removes and returns the item at the front.
     *
     * @throws IllegalStateException if the queue is empty
     */
    @SuppressWarnings("unchecked")
    public T dequeue() {
        if (size == 0) {
            throw new IllegalStateException("Queue underflow: cannot dequeue from an empty queue");
        }
        T item = (T) slots[front];
        boolean rolled = front == slots.length - 1;
        slots[front] = null;
        front = next(front);
        size--;
        // Same rule as enqueue: if that was the last item, front is now parked
        // rather than pointing at wrapped data, so it is not a wrap-around.
        if (rolled && size > 0) {
            wrapArounds++;
        }
        dequeueOps++;
        return item;
    }

    /** Non-throwing dequeue. Returns null when the queue is empty. */
    public T poll() {
        return size == 0 ? null : dequeue();
    }

    /** Item at the front without removing it, or null when empty. */
    @SuppressWarnings("unchecked")
    public T peek() {
        return size == 0 ? null : (T) slots[front];
    }

    /** Item at the rear without removing it, or null when empty. */
    @SuppressWarnings("unchecked")
    public T peekRear() {
        return size == 0 ? null : (T) slots[rear];
    }

    /** Doubles the array and re-lays the items out from index 0 in FIFO order. */
    private void grow() {
        Object[] bigger = new Object[slots.length * 2];
        int index = front;
        for (int i = 0; i < size; i++) {
            bigger[i] = slots[index];
            index = next(index);
        }
        slots = bigger;
        front = 0;
        rear = size - 1;
        growths++;
    }

    public boolean contains(T item) {
        if (item == null) {
            return false;
        }
        int index = front;
        for (int i = 0; i < size; i++) {
            if (item.equals(slots[index])) {
                return true;
            }
            index = next(index);
        }
        return false;
    }

    /** Items in FIFO order, front first. Returns an empty array when empty. */
    public Object[] toArray() {
        Object[] out = new Object[size];
        int index = front;
        for (int i = 0; i < size; i++) {
            out[i] = slots[index];
            index = next(index);
        }
        return out;
    }

    /**
     * Physical layout of the backing array, nulls included. Used by the trace
     * demo to show where front and rear actually sit.
     */
    public Object[] rawSlots() {
        Object[] copy = new Object[slots.length];
        for (int i = 0; i < slots.length; i++) {
            copy[i] = slots[i];
        }
        return copy;
    }

    /** Empties the queue and resets both indices to their starting positions. */
    public void clear() {
        for (int i = 0; i < slots.length; i++) {
            slots[i] = null;
        }
        front = 0;
        rear = slots.length - 1;
        size = 0;
    }

    public int size() {
        return size;
    }

    public int capacity() {
        return slots.length;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /** True when every slot is occupied. A growable queue grows rather than reject. */
    public boolean isFull() {
        return size == slots.length;
    }

    public boolean isGrowable() {
        return growable;
    }

    public int frontIndex() {
        return front;
    }

    public int rearIndex() {
        return rear;
    }

    public long enqueueOps() {
        return enqueueOps;
    }

    public long dequeueOps() {
        return dequeueOps;
    }

    /**
     * How many times front or rear has rolled from the last slot back to slot 0
     * while the queue held items. Rolls that only move a parked index — the
     * first enqueue into an empty queue, or the dequeue that empties it — are
     * not counted, because no live element wrapped.
     */
    public long wrapArounds() {
        return wrapArounds;
    }

    public long rejectedEnqueues() {
        return rejectedEnqueues;
    }

    public long growths() {
        return growths;
    }

    /**
     * Verifies the class invariant. Called by the unit tests after every
     * operation so a broken index shows up immediately rather than later.
     */
    public boolean checkInvariant() {
        int capacity = slots.length;
        if (size < 0 || size > capacity) {
            return false;
        }
        if (front < 0 || front >= capacity || rear < 0 || rear >= capacity) {
            return false;
        }
        int expectedRear = ((front + size - 1) % capacity + capacity) % capacity;
        if (rear != expectedRear) {
            return false;
        }
        int index = front;
        for (int i = 0; i < size; i++) {
            if (slots[index] == null) {
                return false;
            }
            index = next(index);
        }
        for (int i = 0; i < capacity - size; i++) {
            if (slots[index] != null) {
                return false;
            }
            index = next(index);
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("front -> [");
        int index = front;
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(slots[index]);
            index = next(index);
        }
        sb.append("] <- rear  (front=").append(front)
          .append(", rear=").append(rear)
          .append(", size=").append(size)
          .append('/').append(slots.length).append(')');
        return sb.toString();
    }
}
