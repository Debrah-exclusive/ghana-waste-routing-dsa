package structures;

/**
 * Unit tests for SetMap — normal case, boundary case, invalid input case.
 * Plain Java standalone test runner for live defense evidence.
 * Owner: Kelvin Amaah Mankata
 */
public class SetMapTest {

    private static int total = 0;
    private static int passed = 0;

    public static void main(String[] args) {
        runAllTests();
        System.out.println("\n========== SetMapTest SUMMARY ==========");
        System.out.println(passed + " / " + total + " tests passed");
        if (passed != total) {
            System.out.println((total - passed) + " TEST(S) FAILED");
            System.exit(1);
        } else {
            System.out.println("ALL TESTS PASSED");
        }
    }

    public static void runAllTests() {
        testSetAddAndContains();
        testMapOperations();
        testEmptySetAndMap();
        testSetAddNull();
        testSetContainsNull();
        testMapPutNullKey();
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

    private static void testSetAddAndContains() {
        SetMap.MySet<String> set = new SetMap.MySet<>();
        assertTrue("Set Add First", set.add("L001"));
        assertTrue("Set Add Second", set.add("L002"));
        assertTrue("Set Reject Duplicate", !set.add("L001"));

        assertEquals("Set Size", 2, set.size());
        assertTrue("Set Contains L001", set.contains("L001"));
        assertTrue("Set Contains L002", set.contains("L002"));

        assertTrue("Set Remove L001", set.remove("L001"));
        assertTrue("Set Does Not Contain L001 After Remove", !set.contains("L001"));
    }

    private static void testMapOperations() {
        SetMap<String, String> map = new SetMap<>();
        map.put("LOC_1", "Library");
        map.put("LOC_2", "Hostel");

        assertEquals("SetMap Size", 2, map.size());
        assertEquals("SetMap Get Value", "Library", map.get("LOC_1"));
        assertTrue("SetMap ContainsKey", map.containsKey("LOC_2"));

        SetMap.MySet<String> keys = map.keySet(new String[0]);
        assertEquals("SetMap KeySet Size", 2, keys.size());
        assertTrue("SetMap KeySet Contains LOC_1", keys.contains("LOC_1"));
    }

    private static void testEmptySetAndMap() {
        SetMap.MySet<String> set = new SetMap.MySet<>();
        SetMap<String, String> map = new SetMap<>();

        assertEquals("Empty Set Size", 0, set.size());
        assertTrue("Empty Set Is Empty", set.isEmpty());
        assertTrue("Empty Set Contains Returns False", !set.contains("ANY"));

        assertEquals("Empty Map Size", 0, map.size());
        assertTrue("Empty Map Is Empty", map.isEmpty());
        assertEquals("Empty Map Get Returns Null", null, map.get("ANY"));
    }

    private static void testSetAddNull() {
        SetMap.MySet<String> set = new SetMap.MySet<>();
        boolean caught = false;
        try {
            set.add(null);
        } catch (IllegalArgumentException e) {
            caught = true;
        }
        assertTrue("Set Add Null Throws Exception", caught);
    }

    private static void testSetContainsNull() {
        SetMap.MySet<String> set = new SetMap.MySet<>();
        assertTrue("Set Contains Null Returns False", !set.contains(null));
    }

    private static void testMapPutNullKey() {
        SetMap<String, String> map = new SetMap<>();
        boolean caught = false;
        try {
            map.put(null, "Val");
        } catch (IllegalArgumentException e) {
            caught = true;
        }
        assertTrue("Map Put Null Key Throws Exception", caught);
    }
}
