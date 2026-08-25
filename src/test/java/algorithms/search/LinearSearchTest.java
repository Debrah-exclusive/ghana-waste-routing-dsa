package algorithms.search;

import structures.DynamicArray;

/**
 * Unit tests for LinearSearch — normal case, boundary case, invalid input case.
 */
public class LinearSearchTest {

    private static int total = 0;
    private static int passed = 0;

    public static void main(String[] args) {
        runAllTests();
        System.out.println("\n========== LinearSearchTest SUMMARY ==========");
        System.out.println(passed + " / " + total + " tests passed");
        if (passed != total) {
            System.out.println((total - passed) + " TEST(S) FAILED");
            System.exit(1);
        } else {
            System.out.println("ALL TESTS PASSED");
        }
    }

    public static void runAllTests() {
        runNormalCaseTests();
        runBoundaryCaseTests();
        runInvalidInputTests();
    }

    private static void runNormalCaseTests() {
        testElementFound();
        testElementNotFound();
    }

    private static void runBoundaryCaseTests() {
        testElementAtFirstIndex();
        testElementAtLastIndex();
        testNullElementFound();
    }

    private static void runInvalidInputTests() {
        testNullArray();
    }

    private static void testElementFound() {
        total++;
        DynamicArray<Integer> arr = new DynamicArray<>();
        arr.insert(10);
        arr.insert(20);
        arr.insert(30);
        int index = LinearSearch.search(arr, 20);
        if (index == 1) {
            passed++;
            System.out.println("testElementFound PASSED");
        } else {
            System.out.println("testElementFound FAILED");
        }
    }

    private static void testElementNotFound() {
        total++;
        DynamicArray<Integer> arr = new DynamicArray<>();
        arr.insert(10);
        arr.insert(20);
        int index = LinearSearch.search(arr, 50);
        if (index == -1) {
            passed++;
            System.out.println("testElementNotFound PASSED");
        } else {
            System.out.println("testElementNotFound FAILED");
        }
    }

    private static void testElementAtFirstIndex() {
        total++;
        DynamicArray<Integer> arr = new DynamicArray<>();
        arr.insert(10);
        arr.insert(20);
        int index = LinearSearch.search(arr, 10);
        if (index == 0) {
            passed++;
            System.out.println("testElementAtFirstIndex PASSED");
        } else {
            System.out.println("testElementAtFirstIndex FAILED");
        }
    }

    private static void testElementAtLastIndex() {
        total++;
        DynamicArray<Integer> arr = new DynamicArray<>();
        arr.insert(10);
        arr.insert(20);
        int index = LinearSearch.search(arr, 20);
        if (index == 1) {
            passed++;
            System.out.println("testElementAtLastIndex PASSED");
        } else {
            System.out.println("testElementAtLastIndex FAILED");
        }
    }

    private static void testNullElementFound() {
        total++;
        DynamicArray<Integer> arr = new DynamicArray<>();
        arr.insert(10);
        arr.insert(null);
        int index = LinearSearch.search(arr, null);
        if (index == 1) {
            passed++;
            System.out.println("testNullElementFound PASSED");
        } else {
            System.out.println("testNullElementFound FAILED");
        }
    }

    private static void testNullArray() {
        total++;
        try {
            LinearSearch.search(null, 10);
            System.out.println("testNullArray FAILED");
        } catch (IllegalArgumentException e) {
            passed++;
            System.out.println("testNullArray PASSED");
        }
    }
}
