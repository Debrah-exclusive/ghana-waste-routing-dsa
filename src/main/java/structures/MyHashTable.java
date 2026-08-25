package structures;

/**
 * Owner: Desmond Kimi Bilabia
 * Custom Hash Table implementation using Separate Chaining.
 * Built-in Java collections are NOT used.
 *
 * Supported operations:
 * - put(K, V), get(K), remove(K), containsKey(K)
 * - Dynamic resizing when load factor exceeds threshold (default 0.75)
 * - Collision statistics collection for different load factors
 */
public class MyHashTable<K, V> {

    public static class Entry<K, V> {
        public K key;
        public V value;
        public Entry<K, V> next;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }
    }

    private Entry<K, V>[] table;
    private int capacity;
    private int size;
    private double loadFactorThreshold;
    private long totalCollisions;

    @SuppressWarnings("unchecked")
    public MyHashTable(int initialCapacity, double loadFactorThreshold) {
        if (initialCapacity <= 0 || loadFactorThreshold <= 0) {
            throw new IllegalArgumentException("Invalid initial capacity or load factor threshold");
        }
        this.capacity = initialCapacity;
        this.loadFactorThreshold = loadFactorThreshold;
        this.table = (Entry<K, V>[]) new Entry[capacity];
        this.size = 0;
        this.totalCollisions = 0;
    }

    public MyHashTable() {
        this(16, 0.75);
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int capacity() {
        return capacity;
    }

    public double getLoadFactor() {
        return (double) size / capacity;
    }

    public long getTotalCollisions() {
        return totalCollisions;
    }

    private int hash(K key) {
        if (key == null) return 0;
        int h = key.hashCode();
        return (h & 0x7fffffff) % capacity;
    }

    public V get(K key) {
        if (key == null) return null;
        int idx = hash(key);
        Entry<K, V> curr = table[idx];
        while (curr != null) {
            if (curr.key.equals(key)) {
                return curr.value;
            }
            curr = curr.next;
        }
        return null;
    }

    public boolean containsKey(K key) {
        return get(key) != null;
    }

    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null");
        }

        if (getLoadFactor() >= loadFactorThreshold) {
            resize(capacity * 2);
        }

        int idx = hash(key);
        Entry<K, V> head = table[idx];

        if (head != null) {
            totalCollisions++; // Collision occurred
        }

        Entry<K, V> curr = head;
        while (curr != null) {
            if (curr.key.equals(key)) {
                curr.value = value; // update
                return;
            }
            curr = curr.next;
        }

        // Insert at head of chain
        Entry<K, V> newEntry = new Entry<>(key, value);
        newEntry.next = head;
        table[idx] = newEntry;
        size++;
    }

    public V remove(K key) {
        if (key == null) return null;
        int idx = hash(key);
        Entry<K, V> curr = table[idx];
        Entry<K, V> prev = null;

        while (curr != null) {
            if (curr.key.equals(key)) {
                if (prev == null) {
                    table[idx] = curr.next;
                } else {
                    prev.next = curr.next;
                }
                size--;
                return curr.value;
            }
            prev = curr;
            curr = curr.next;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private void resize(int newCapacity) {
        Entry<K, V>[] oldTable = table;
        capacity = newCapacity;
        table = (Entry<K, V>[]) new Entry[capacity];
        size = 0;

        for (Entry<K, V> head : oldTable) {
            Entry<K, V> curr = head;
            while (curr != null) {
                put(curr.key, curr.value);
                curr = curr.next;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public K[] keyArray(K[] prototype) {
        K[] keys = (K[]) java.lang.reflect.Array.newInstance(prototype.getClass().getComponentType(), size);
        int idx = 0;
        for (Entry<K, V> head : table) {
            Entry<K, V> curr = head;
            while (curr != null) {
                keys[idx++] = curr.key;
                curr = curr.next;
            }
        }
        return keys;
    }
}
