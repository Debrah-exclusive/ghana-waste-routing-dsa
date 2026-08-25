package algorithms.dp;

/**
 * Unit tests for KnapsackDP — normal case, boundary case, invalid input case.
 * Plain Java standalone test runner for live defense evidence.
 * Owner: Thelma Osei-Fiagbor
 */
public class KnapsackDPTest {

    private static int total = 0;
    private static int passed = 0;

    public static void main(String[] args) {
        runAllTests();
        System.out.println("\n========== KnapsackDPTest SUMMARY ==========");
        System.out.println(passed + " / " + total + " tests passed");
        if (passed != total) {
            System.out.println((total - passed) + " TEST(S) FAILED");
            System.exit(1);
        } else {
            System.out.println("ALL TESTS PASSED");
        }
    }

    public static void runAllTests() {
        testSolveKnapsackNormal();
        testZeroCapacityBoundary();
        testInvalidNegativeCapacity();
        testNullItemsArray();
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

    private static void testSolveKnapsackNormal() {
        KnapsackDP.RequestItem[] items = new KnapsackDP.RequestItem[]{
                new KnapsackDP.RequestItem("REQ01", 10, 60),
                new KnapsackDP.RequestItem("REQ02", 20, 100),
                new KnapsackDP.RequestItem("REQ03", 30, 120)
        };

        KnapsackDP.DPResult res = KnapsackDP.solveKnapsack(items, 50);
        assertEquals("Knapsack Max Priority Value", 220, res.maxPriority);
        assertEquals("Knapsack Selected Count", 2, res.selectedItems.length);
        assertTrue("DP Tabulation Table Output", res.getTableFormatted().contains("Tabulation Table"));
    }

    private static void testZeroCapacityBoundary() {
        KnapsackDP.RequestItem[] items = new KnapsackDP.RequestItem[]{
                new KnapsackDP.RequestItem("REQ01", 10, 60)
        };
        KnapsackDP.DPResult res = KnapsackDP.solveKnapsack(items, 0);
        assertEquals("Knapsack Zero Capacity Value", 0, res.maxPriority);
        assertEquals("Knapsack Zero Capacity Selected Count", 0, res.selectedItems.length);
    }

    private static void testInvalidNegativeCapacity() {
        boolean caught = false;
        try {
            KnapsackDP.solveKnapsack(new KnapsackDP.RequestItem[0], -5);
        } catch (IllegalArgumentException e) {
            caught = true;
        }
        assertTrue("Negative Capacity Throws Exception", caught);
    }

    private static void testNullItemsArray() {
        boolean caught = false;
        try {
            KnapsackDP.solveKnapsack(null, 10);
        } catch (IllegalArgumentException e) {
            caught = true;
        }
        assertTrue("Null Items Throws Exception", caught);
    }
}
