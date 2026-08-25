package structures;

/**
 * Owner: Kelvin Amaah Mankata
 * Custom Set and Map ADT implementation built on top of MyHashTable.
 * Built-in Java collections are NOT used.
 *
 * Exposes:
 * - MySet<E>: add(E), contains(E), remove(E), size(), toArray()
 * - SetMap<K, V>: put(K, V), get(K), remove(K), containsKey(K), keySet()
 */
public class SetMap<K, V> {

    public static class MySet<E> {
        private MyHashTable<E, Boolean> internalTable;

        public MySet() {
            this.internalTable = new MyHashTable<>();
        }

        public MySet(int initialCapacity) {
            this.internalTable = new MyHashTable<>(initialCapacity, 0.75);
        }

        public boolean add(E element) {
            if (element == null) throw new IllegalArgumentException("Set cannot contain null");
            if (internalTable.containsKey(element)) return false;
            internalTable.put(element, Boolean.TRUE);
            return true;
        }

        public boolean contains(E element) {
            if (element == null) return false;
            return internalTable.containsKey(element);
        }

        public boolean remove(E element) {
            if (element == null) return false;
            return internalTable.remove(element) != null;
        }

        public int size() {
            return internalTable.size();
        }

        public boolean isEmpty() {
            return internalTable.isEmpty();
        }

        public E[] toArray(E[] prototype) {
            return internalTable.keyArray(prototype);
        }
    }

    private MyHashTable<K, V> map;

    public SetMap() {
        this.map = new MyHashTable<>();
    }

    public SetMap(int initialCapacity) {
        this.map = new MyHashTable<>(initialCapacity, 0.75);
    }

    public void put(K key, V value) {
        map.put(key, value);
    }

    public V get(K key) {
        return map.get(key);
    }

    public V remove(K key) {
        return map.remove(key);
    }

    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public MySet<K> keySet(K[] prototype) {
        MySet<K> set = new MySet<>(map.capacity());
        K[] keys = map.keyArray(prototype);
        for (K k : keys) {
            set.add(k);
        }
        return set;
    }
}
