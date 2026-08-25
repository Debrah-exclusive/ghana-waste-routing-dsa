package structures;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Owner: Abdul-Aziz Naeem
 * Implemented from scratch (built-in Java collections not allowed).
 */
@SuppressWarnings("unchecked")
public class DynamicArray<T> implements Iterable<T> {
    
    private static final int INITIAL_CAPACITY = 2;
    private T[] data;
    private int size;

    public DynamicArray() {
        this.data = (T[]) new Object[INITIAL_CAPACITY];
        this.size = 0;
    }

    public DynamicArray(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        }
        this.data = (T[]) new Object[initialCapacity];
        this.size = 0;
    }

    public void insert(T element) {
        if (size == data.length) {
            resize();
        }
        data[size++] = element;
    }

    public void insertAt(int index, T element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        if (size == data.length) {
            resize();
        }
        // Shift elements to the right
        for (int i = size; i > index; i--) {
            data[i] = data[i - 1];
        }
        data[index] = element;
        size++;
    }

    public T get(int index) {
        checkIndex(index);
        return data[index];
    }

    public void set(int index, T element) {
        checkIndex(index);
        data[index] = element;
    }

    public T remove(int index) {
        checkIndex(index);
        T removedElement = data[index];
        // Shift elements to the left
        for (int i = index; i < size - 1; i++) {
            data[i] = data[i + 1];
        }
        data[size - 1] = null; // Clear reference for GC
        size--;
        return removedElement;
    }

    public boolean remove(T element) {
        for (int i = 0; i < size; i++) {
            if (data[i] == null ? element == null : data[i].equals(element)) {
                remove(i);
                return true;
            }
        }
        return false;
    }

    private void resize() {
        int newCapacity = data.length == 0 ? 1 : data.length * 2;
        T[] newData = (T[]) new Object[newCapacity];
        for (int i = 0; i < size; i++) {
            newData[i] = data[i];
        }
        data = newData;
    }
    
    public int size() {
        return size;
    }
    
    public int capacity() {
        return data.length;
    }
    
    public boolean isEmpty() {
        return size == 0;
    }
    
    public void clear() {
        for (int i = 0; i < size; i++) {
            data[i] = null;
        }
        size = 0;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < size;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return data[currentIndex++];
            }
        };
    }
}
