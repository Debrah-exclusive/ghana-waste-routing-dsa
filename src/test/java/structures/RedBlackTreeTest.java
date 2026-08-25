package structures;

/**
 * Unit tests for RedBlackTree — normal case, boundary case, invalid input case.
 * Plain Java standalone test runner for live defense evidence.
 * Owner: Adam Mohammed
 */
public class RedBlackTreeTest {

    private static int total = 0;
    private static int passed = 0;

    public static void main(String[] args) {
        runAllTests();
        System.out.println("\n========== RedBlackTreeTest SUMMARY ==========");
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
        testRotationLog();
        testDiagramGeneration();
        testEmptyTree();
        testSingleNode();
        testDuplicateInsert();
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
        RedBlackTree<Integer, String> rbt = new RedBlackTree<>();
        rbt.insert(10, "Ten");
        rbt.insert(20, "Twenty");
        rbt.insert(30, "Thirty");

        assertEquals("RBT Size", 3, rbt.size());
        assertEquals("RBT Search 10", "Ten", rbt.search(10));
        assertEquals("RBT Search 20", "Twenty", rbt.search(20));
        assertEquals("RBT Search 30", "Thirty", rbt.search(30));
    }

    private static void testRotationLog() {
        RedBlackTree<Integer, String> rbt = new RedBlackTree<>();
        rbt.insert(10, "A");
        rbt.insert(20, "B");
        rbt.insert(30, "C");

        String log = rbt.getRotationLog();
        assertTrue("RBT Rotation Log Recorded", log.contains("Rotate"));
    }

    private static void testDiagramGeneration() {
        RedBlackTree<Integer, String> rbt = new RedBlackTree<>();
        rbt.insert(15, "Root");
        rbt.insert(10, "Left");
        rbt.insert(20, "Right");

        String diagram = rbt.toDiagram();
        assertTrue("RBT Diagram Root Contains Key", diagram.contains("15"));
        assertTrue("RBT Diagram Color Tag", diagram.contains("(B)"));
    }

    private static void testEmptyTree() {
        RedBlackTree<Integer, String> rbt = new RedBlackTree<>();
        assertEquals("RBT Empty Size", 0, rbt.size());
        assertTrue("RBT Is Empty", rbt.isEmpty());
        assertEquals("RBT Search Empty", null, rbt.search(100));
    }

    private static void testSingleNode() {
        RedBlackTree<Integer, String> rbt = new RedBlackTree<>();
        rbt.insert(100, "Single");
        assertEquals("RBT Single Node Size", 1, rbt.size());
        assertEquals("RBT Search Single Node", "Single", rbt.search(100));
    }

    private static void testDuplicateInsert() {
        RedBlackTree<Integer, String> rbt = new RedBlackTree<>();
        rbt.insert(10, "V1");
        rbt.insert(10, "V2");
        assertEquals("RBT Duplicate Insert Size", 1, rbt.size());
        assertEquals("RBT Duplicate Update Value", "V2", rbt.search(10));
    }

    private static void testInsertNullKey() {
        RedBlackTree<Integer, String> rbt = new RedBlackTree<>();
        boolean caught = false;
        try {
            rbt.insert(null, "NullKey");
        } catch (IllegalArgumentException e) {
            caught = true;
        }
        assertTrue("RBT Insert Null Key Throws Exception", caught);
    }

    private static void testSearchNullKey() {
        RedBlackTree<Integer, String> rbt = new RedBlackTree<>();
        assertEquals("RBT Search Null Key Returns Null", null, rbt.search(null));
    }
}
