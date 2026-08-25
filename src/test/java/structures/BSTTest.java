package structures;

/**
 * Unit tests for BST — normal case, boundary case, invalid input case.
 * Plain Java standalone test runner for live defense evidence.
 * Owner: Sadiq Moro Ayariga
 */
public class BSTTest {

    private static int total = 0;
    private static int passed = 0;

    public static void main(String[] args) {
        runAllTests();
        System.out.println("\n========== BSTTest SUMMARY ==========");
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
        testInorderSortedOutput();
        testSearchPath();
        testDeleteNormal();
        testEmptyTree();
        testSingleNode();
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
        BST<Integer, String> bst = new BST<>();
        bst.insert(50, "Root");
        bst.insert(30, "Left");
        bst.insert(70, "Right");
        bst.insert(20, "Left-Left");

        assertEquals("Insert/Search Root", "Root", bst.search(50));
        assertEquals("Insert/Search Left", "Left", bst.search(30));
        assertEquals("Insert/Search Right", "Right", bst.search(70));
        assertEquals("Insert/Search Left-Left", "Left-Left", bst.search(20));
        assertEquals("Size check", 4, bst.size());
    }

    private static void testInorderSortedOutput() {
        BST<Integer, String> bst = new BST<>();
        int[] keys = {50, 30, 70, 20, 40, 60, 80};
        for (int k : keys) bst.insert(k, "V" + k);

        Integer[] sorted = bst.getSortedInorderKeys(new Integer[0]);
        boolean isSorted = sorted.length == 7 &&
                sorted[0] == 20 && sorted[1] == 30 && sorted[2] == 40 &&
                sorted[3] == 50 && sorted[4] == 60 && sorted[5] == 70 && sorted[6] == 80;
        assertTrue("Inorder sorted output", isSorted);
    }

    private static void testSearchPath() {
        BST<Integer, String> bst = new BST<>();
        bst.insert(50, "A");
        bst.insert(30, "B");
        bst.insert(40, "C");

        String path = bst.getSearchPath(40);
        assertTrue("Search path formatting", path.contains("50 -> 30 -> 40 (Found)"));
    }

    private static void testDeleteNormal() {
        BST<Integer, String> bst = new BST<>();
        bst.insert(50, "A");
        bst.insert(30, "B");
        bst.insert(70, "C");

        boolean deleted = bst.delete(30);
        assertTrue("Delete node", deleted);
        assertEquals("Search deleted node", null, bst.search(30));
        assertEquals("Size after delete", 2, bst.size());
    }

    private static void testEmptyTree() {
        BST<Integer, String> bst = new BST<>();
        assertTrue("Empty check", bst.isEmpty());
        assertEquals("Empty size", 0, bst.size());
        assertEquals("Search empty", null, bst.search(10));
    }

    private static void testSingleNode() {
        BST<Integer, String> bst = new BST<>();
        bst.insert(100, "Single");
        assertEquals("Single node search", "Single", bst.search(100));
        assertTrue("Delete single node", bst.delete(100));
        assertTrue("Empty after delete", bst.isEmpty());
    }

    private static void testUpdateExistingKey() {
        BST<Integer, String> bst = new BST<>();
        bst.insert(10, "Original");
        bst.insert(10, "Updated");
        assertEquals("Update existing key", "Updated", bst.search(10));
        assertEquals("Size after update", 1, bst.size());
    }

    private static void testInsertNullKey() {
        BST<Integer, String> bst = new BST<>();
        boolean caught = false;
        try {
            bst.insert(null, "Val");
        } catch (IllegalArgumentException e) {
            caught = true;
        }
        assertTrue("Null key insert throws exception", caught);
    }

    private static void testSearchNullKey() {
        BST<Integer, String> bst = new BST<>();
        assertEquals("Search null key returns null", null, bst.search(null));
    }
}
