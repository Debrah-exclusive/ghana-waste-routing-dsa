package structures;

/**
 * Unit tests for BTree — normal case, boundary case, invalid input case.
 * Plain Java standalone test runner for live defense evidence.
 * Owner: Thelma Osei-Fiagbor
 */
public class BTreeTest {

    private static int total = 0;
    private static int passed = 0;

    public static void main(String[] args) {
        runAllTests();
        System.out.println("\n========== BTreeTest SUMMARY ==========");
        System.out.println(passed + " / " + total + " tests passed");
        if (passed != total) {
            System.out.println((total - passed) + " TEST(S) FAILED");
            System.exit(1);
        } else {
            System.out.println("ALL TESTS PASSED");
        }
    }

    public static void runAllTests() {
        testInsertAndSearchNormal();
        testSplitLog();
        testSearchTrace();
        testEmptyTree();
        testUpdateExistingKey();
        testInsertNullKey();
        testSearchNullKey();
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

    private static void testInsertAndSearchNormal() {
        BTree<Integer, String> btree = new BTree<>();
        btree.insert(10, "Ten");
        btree.insert(20, "Twenty");
        btree.insert(5, "Five");
        btree.insert(6, "Six");

        assertEquals("BTree Size", 4, btree.size());
        assertEquals("BTree Search 10", "Ten", btree.search(10));
        assertEquals("BTree Search 20", "Twenty", btree.search(20));
        assertEquals("BTree Search 5", "Five", btree.search(5));
        assertEquals("BTree Search 6", "Six", btree.search(6));
    }

    private static void testSplitLog() {
        BTree<Integer, String> btree = new BTree<>();
        btree.insert(1, "A");
        btree.insert(2, "B");
        btree.insert(3, "C");
        btree.insert(4, "D");

        String log = btree.getSplitLog();
        assertTrue("BTree Split Log Recorded", log.contains("Splitting node"));
    }

    private static void testSearchTrace() {
        BTree<Integer, String> btree = new BTree<>();
        btree.insert(10, "A");
        btree.insert(20, "B");

        String trace = btree.getSearchTrace(10);
        assertTrue("BTree Search Trace", trace.contains("Found 10"));
    }

    private static void testEmptyTree() {
        BTree<Integer, String> btree = new BTree<>();
        assertEquals("BTree Empty Size", 0, btree.size());
        assertTrue("BTree Is Empty", btree.isEmpty());
        assertEquals("BTree Search Empty", null, btree.search(100));
    }

    private static void testUpdateExistingKey() {
        BTree<Integer, String> btree = new BTree<>();
        btree.insert(10, "Old");
        btree.insert(10, "New");
        assertEquals("BTree Size After Update", 1, btree.size());
        assertEquals("BTree Value Updated", "New", btree.search(10));
    }

    private static void testInsertNullKey() {
        BTree<Integer, String> btree = new BTree<>();
        boolean caught = false;
        try {
            btree.insert(null, "Value");
        } catch (IllegalArgumentException e) {
            caught = true;
        }
        assertTrue("BTree Insert Null Key Throws Exception", caught);
    }

    private static void testSearchNullKey() {
        BTree<Integer, String> btree = new BTree<>();
        assertEquals("BTree Search Null Key Returns Null", null, btree.search(null));
    }
}
