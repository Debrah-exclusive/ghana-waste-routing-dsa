package structures;

/**
 * Unit tests for MyHashTable — normal case, boundary case, invalid input case.
 * Plain Java standalone test runner for live defense evidence.
 * Owner: Desmond Kimi Bilabia
 */
public class MyHashTableTest {

    private static int total = 0;
    private static int passed = 0;

    public static void main(String[] args) {
        runAllTests();
        System.out.println("\n========== MyHashTableTest SUMMARY ==========");
        System.out.println(passed + " / " + total + " tests passed");
        if (passed != total) {
            System.out.println((total - passed) + " TEST(S) FAILED");
            System.exit(1);
        } else {
            System.out.println("ALL TESTS PASSED");
        }
    }

    public static void runAllTests() {
        testPutGetRemoveNormal();
        testLoadFactorAndResizing();
        testCollisionStats();
        testEmptyTable();
        testUpdateKey();
        testPutNullKey();
        testGetNullKey();
        testInvalidCapacityConstructor();
    }

    private static void assertTrue(String name, boolean condition) {
        total++;
        if (condition) {
            passed++;
            System.out.println("  [PASS] " + name);
        } else {
            System.err.println("  [FAIL] " + name);
        }
    }

    private static void assertEquals(String name, Object expected, Object actual) {
        total++;
        boolean match = (expected == null && actual == null) || (expected != null && expected.equals(actual));
        if (match) {
            passed++;
            System.out.println("  [PASS] " + name);
        } else {
            System.err.println("  [FAIL] " + name + " - Expected: " + expected + ", Actual: " + actual);
        }
    }

    private static void testPutGetRemoveNormal() {
        MyHashTable<String, String> map = new MyHashTable<>(8, 0.75);
        map.put("L001", "Balme Library");
        map.put("L002", "CS Dept");
        map.put("L003", "UG Hospital");

        assertEquals("HashTable Size", 3, map.size());
        assertEquals("HashTable Get L001", "Balme Library", map.get("L001"));
        assertEquals("HashTable Get L002", "CS Dept", map.get("L002"));

        assertEquals("HashTable Remove L002", "CS Dept", map.remove("L002"));
        assertEquals("HashTable Get Removed L002", null, map.get("L002"));
        assertEquals("HashTable Size After Remove", 2, map.size());
    }

    private static void testLoadFactorAndResizing() {
        MyHashTable<String, String> map = new MyHashTable<>(8, 0.75);
        int initialCap = map.capacity();
        for (int i = 0; i < 20; i++) {
            map.put("K" + i, "V" + i);
        }
        assertTrue("HashTable Resized Capacity", map.capacity() > initialCap);
        assertEquals("HashTable Size After Multi Insert", 20, map.size());
        assertEquals("HashTable Retrieve Inserted Value", "V15", map.get("K15"));
    }

    private static void testCollisionStats() {
        MyHashTable<Integer, String> smallTable = new MyHashTable<>(4, 0.9);
        smallTable.put(1, "A");
        smallTable.put(5, "B");
        assertTrue("HashTable Collisions Recorded", smallTable.getTotalCollisions() > 0);
    }

    private static void testEmptyTable() {
        MyHashTable<String, String> map = new MyHashTable<>(8, 0.75);
        assertEquals("HashTable Empty Size", 0, map.size());
        assertTrue("HashTable Is Empty", map.isEmpty());
        assertEquals("HashTable Search Empty", null, map.get("UNKNOWN"));
        assertEquals("HashTable Remove Empty", null, map.remove("UNKNOWN"));
    }

    private static void testUpdateKey() {
        MyHashTable<String, String> map = new MyHashTable<>(8, 0.75);
        map.put("KEY", "Val1");
        map.put("KEY", "Val2");
        assertEquals("HashTable Size Update Key", 1, map.size());
        assertEquals("HashTable Updated Value", "Val2", map.get("KEY"));
    }

    private static void testPutNullKey() {
        MyHashTable<String, String> map = new MyHashTable<>(8, 0.75);
        boolean caught = false;
        try {
            map.put(null, "Value");
        } catch (IllegalArgumentException e) {
            caught = true;
        }
        assertTrue("HashTable Put Null Key Throws Exception", caught);
    }

    private static void testGetNullKey() {
        MyHashTable<String, String> map = new MyHashTable<>(8, 0.75);
        assertEquals("HashTable Get Null Key Returns Null", null, map.get(null));
    }

    private static void testInvalidCapacityConstructor() {
        boolean caught = false;
        try {
            new MyHashTable<>(-5, 0.75);
        } catch (IllegalArgumentException e) {
            caught = true;
        }
        assertTrue("HashTable Invalid Capacity Throws Exception", caught);
    }
}
