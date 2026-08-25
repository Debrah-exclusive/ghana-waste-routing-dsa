package algorithms.search;

import structures.DynamicArray;

/**
 * Unit tests for BinarySearch — normal case, boundary case, invalid input case.
 */
public class BinarySearchTest {

    private static int total = 0;
    private static int passed = 0;

    public static void main(String[] args) {
        runAllTests();
        System.out.println("\n========== BinarySearchTest SUMMARY ==========");
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
        runPreconditionTest();
    }

    private static void runNormalCaseTests() {
        testElementFound();
        testElementNotFound();
    }

    private static void runBoundaryCaseTests() {
        testElementAtFirstIndex();
        testElementAtLastIndex();
        testSingleElementArray();
    }

    private static void runInvalidInputTests() {
        testNullArray();
        testNullTarget();
        testNullElementInArray();
    }

    private static void runPreconditionTest() {
        testPreconditionUnsorted();
    }

    private static void testElementFound() {
        total++;
        DynamicArray<Integer> arr = new DynamicArray<>();
        arr.insert(10);
        arr.insert(20);
        arr.insert(30);
        arr.insert(40);
        arr.insert(50);
        int index = BinarySearch.search(arr, 40);
        if (index == 3) {
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
        arr.insert(30);
        int index = BinarySearch.search(arr, 25);
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
        arr.insert(30);
        int index = BinarySearch.search(arr, 10);
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
        arr.insert(30);
        int index = BinarySearch.search(arr, 30);
        if (index == 2) {
            passed++;
            System.out.println("testElementAtLastIndex PASSED");
        } else {
            System.out.println("testElementAtLastIndex FAILED");
        }
    }
    
    private static void testSingleElementArray() {
        total++;
        DynamicArray<Integer> arr = new DynamicArray<>();
        arr.insert(10);
        int index1 = BinarySearch.search(arr, 10);
        int index2 = BinarySearch.search(arr, 20);
        if (index1 == 0 && index2 == -1) {
            passed++;
            System.out.println("testSingleElementArray PASSED");
        } else {
            System.out.println("testSingleElementArray FAILED");
        }
    }

    private static void testNullArray() {
        total++;
        try {
            BinarySearch.search(null, 10);
            System.out.println("testNullArray FAILED");
        } catch (IllegalArgumentException e) {
            passed++;
            System.out.println("testNullArray PASSED");
        }
    }
    
    private static void testNullTarget() {
        total++;
        DynamicArray<Integer> arr = new DynamicArray<>();
        arr.insert(10);
        try {
            BinarySearch.search(arr, null);
            System.out.println("testNullTarget FAILED");
        } catch (IllegalArgumentException e) {
            passed++;
            System.out.println("testNullTarget PASSED");
        }
    }
    
    private static void testNullElementInArray() {
        total++;
        DynamicArray<Integer> arr = new DynamicArray<>();
        arr.insert(10);
        arr.insert(null);
        arr.insert(30);
        try {
            BinarySearch.search(arr, 30);
            System.out.println("testNullElementInArray FAILED");
        } catch (IllegalStateException e) {
            passed++;
            System.out.println("testNullElementInArray PASSED");
        }
    }

    /**
     * Binary Search Precondition Test
     * Verifies that searching an unsorted array may fail to find an existing element.
     */
    private static void testPreconditionUnsorted() {
        total++;
        DynamicArray<Integer> arr = new DynamicArray<>();
        // Unsorted data
        arr.insert(50);
        arr.insert(10);
        arr.insert(30);
        arr.insert(20);
        arr.insert(40);
        
        // Element 10 is at index 1, but binary search might look at 30 (mid),
        // go left, then look at 50, go left, and fail to find 10.
        int index = BinarySearch.search(arr, 10);
        
        // Precondition is violated, so we expect it to fail (return -1) 
        // even though 10 is in the array.
        if (index == -1) {
            passed++;
            System.out.println("testPreconditionUnsorted PASSED");
        } else {
            System.out.println("testPreconditionUnsorted FAILED (unexpectedly found element in unsorted array, logic might just be lucky)");
        }
    }
}
