package structures;

import java.util.NoSuchElementException;

/** Generic deque backed by a resizable circular array. */
public class MyDeque<E> {
    private static final int DEFAULT_CAPACITY = 8;
    private Object[] elements;
    private int front;
    private int size;

    public MyDeque() { this(DEFAULT_CAPACITY); }

    public MyDeque(int initialCapacity) {
        if (initialCapacity < 0) throw new IllegalArgumentException("initial capacity cannot be negative");
        elements = new Object[Math.max(1, initialCapacity)];
    }

    public void addFront(E value) {
        requireValue(value);
        ensureCapacity();
        front = (front - 1 + elements.length) % elements.length;
        elements[front] = value;
        size++;
    }

    public void addRear(E value) {
        requireValue(value);
        ensureCapacity();
        elements[index(size)] = value;
        size++;
    }

    @SuppressWarnings("unchecked")
    public E removeFront() {
        requireNotEmpty();
        E value = (E) elements[front];
        elements[front] = null;
        front = (front + 1) % elements.length;
        size--;
        if (size == 0) front = 0;
        return value;
    }

    @SuppressWarnings("unchecked")
    public E removeRear() {
        requireNotEmpty();
        int rear = index(size - 1);
        E value = (E) elements[rear];
        elements[rear] = null;
        size--;
        if (size == 0) front = 0;
        return value;
    }

    @SuppressWarnings("unchecked")
    public E peekFront() {
        requireNotEmpty();
        return (E) elements[front];
    }

    @SuppressWarnings("unchecked")
    public E peekRear() {
        requireNotEmpty();
        return (E) elements[index(size - 1)];
    }

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    public Object[] toArrayFrontToRear() {
        Object[] result = new Object[size];
        for (int i = 0; i < size; i++) result[i] = elements[index(i)];
        return result;
    }

    private int index(int offset) { return (front + offset) % elements.length; }

    private void ensureCapacity() {
        if (size < elements.length) return;
        Object[] grown = new Object[elements.length * 2];
        for (int i = 0; i < size; i++) grown[i] = elements[index(i)];
        elements = grown;
        front = 0;
    }

    private static void requireValue(Object value) {
        if (value == null) throw new IllegalArgumentException("deque does not accept null values");
    }

    private void requireNotEmpty() {
        if (isEmpty()) throw new NoSuchElementException("deque is empty");
    }
}
