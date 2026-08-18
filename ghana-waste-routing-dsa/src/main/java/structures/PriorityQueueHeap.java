package structures;

import java.util.Comparator;
import java.util.NoSuchElementException;

/**
 * Owner: Derrick Debrah
 * Implementation of a Min/Max Priority Queue Heap built from scratch using dynamic arrays.
 */
public class PriorityQueueHeap<T> {
    private Object[] data;
    private int size;
    private final Comparator<? super T> comparator;
    private static final int DEFAULT_CAPACITY = 16;

    /** Natural ordering heap: requires T implements Comparable<T>. */
    public PriorityQueueHeap() {
        this(null, DEFAULT_CAPACITY);
    }

    /** Custom order heap using a Comparator. */
    public PriorityQueueHeap(Comparator<? super T> comparator) {
        this(comparator, DEFAULT_CAPACITY);
    }

    public PriorityQueueHeap(Comparator<? super T> comparator, int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("initialCapacity must be > 0");
        }
        this.data = new Object[initialCapacity];
        this.size = 0;
        this.comparator = comparator;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }
    private void ensureCapacity() {
        if (size == data.length) {
            Object[] bigger = new Object[data.length * 2];
            System.arraycopy(data, 0, bigger, 0, data.length);
            data = bigger;
        }
    }

    @SuppressWarnings("unchecked")
    private int compare(Object a, Object b) {
        if (comparator != null) {
            return comparator.compare((T) a, (T) b);
        }
        // Fallback to natural ordering if no comparator was supplied
        return ((Comparable<T>) a).compareTo((T) b);
    }

    private void swap(int i, int j) {
        Object tmp = data[i];
        data[i] = data[j];
        data[j] = tmp;
    }

     public void insert(T value) {
        if (value == null) {
            throw new IllegalArgumentException("Cannot insert null into the heap");
        }
        ensureCapacity();
        data[size] = value;
        siftUp(size);
        size++;
    }

    private void siftUp(int i) {
        while (i > 0) {
            int parent = (i - 1) / 2;
            if (compare(data[i], data[parent]) < 0) {
                swap(i, parent);
                i = parent; // Move up to parent's index
            } else {
                break; // Heap property satisfied
            }
        }
    }


    @SuppressWarnings("unchecked")
    public T peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("peek() on empty heap");
        }
        return (T) data[0];
    }

    @SuppressWarnings("unchecked")
    public T extractTop() {
        if (isEmpty()) {
            throw new NoSuchElementException("extractTop() on empty heap");
        }
        T top = (T) data[0];
        size--;
        data[0] = data[size];
        data[size] = null; // help garbage collection
        if (size > 0) {
            siftDown(0);
        }
        return top;
    }

    private void siftDown(int i) {
        while (true) {
            int left = 2 * i + 1;
            int right = 2 * i + 2;
            int smallest = i;

            if (left < size && compare(data[left], data[smallest]) < 0) {
                smallest = left;
            }
            if (right < size && compare(data[right], data[smallest]) < 0) {
                smallest = right;
            }
            if (smallest == i) {
                break; // Neither child is smaller, stop
            }
            swap(i, smallest);
            i = smallest; // Move down to child's index
        }
    }


    public static <T> PriorityQueueHeap<T> heapify(T[] items, Comparator<? super T> comparator) {
        PriorityQueueHeap<T> heap = new PriorityQueueHeap<>(comparator, Math.max(items.length, 1));
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                throw new IllegalArgumentException("heapify(): input array contains null at index " + i);
            }
            heap.data[i] = items[i];
        }
        heap.size = items.length;
        // Sift down all non-leaf nodes in reverse order
        for (int i = heap.size / 2 - 1; i >= 0; i--) {
            heap.siftDown(i);
        }
        return heap;
    }
}
