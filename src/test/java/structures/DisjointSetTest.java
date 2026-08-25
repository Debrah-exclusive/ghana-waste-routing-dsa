package structures;

/**
 * Unit tests for DisjointSet — normal case, boundary case, invalid input case.
 * Owner: Emmanuel Aseda Kow Bentsil
 */
public class DisjointSetTest {

    private static int total = 0;
    private static int passed = 0;

    public static void main(String[] args) {
        testDisjointSetNormal();
        testDisjointSetBoundary();
        testDisjointSetInvalid();
        System.out.println("\n========== DisjointSetTest SUMMARY ==========");
        System.out.println(passed + " / " + total + " tests passed");
    }

    private static void assertTrue(String name, boolean cond) {
        total++;
        if (cond) {
            passed++;
            System.out.println("  [PASS] " + name);
        } else {
            System.err.println("  [FAIL] " + name);
        }
    }

    private static void testDisjointSetNormal() {
        DisjointSet ds = new DisjointSet(10);
        assertTrue("Union 1 and 2", ds.union(1, 2));
        assertTrue("Union 2 and 3", ds.union(2, 3));
        assertTrue("1 and 3 connected", ds.connected(1, 3));
        assertTrue("Redundant union returns false", !ds.union(1, 3));
    }

    private static void testDisjointSetBoundary() {
        DisjointSet ds = new DisjointSet(5);
        assertTrue("Self find equals self", ds.find(0) == 0);
        assertTrue("0 and 4 not connected initially", !ds.connected(0, 4));
    }

    private static void testDisjointSetInvalid() {
        DisjointSet ds = new DisjointSet(5);
        boolean caught = false;
        try {
            ds.find(99);
        } catch (IllegalArgumentException e) {
            caught = true;
        }
        assertTrue("Find out of bounds throws exception", caught);
    }
}
