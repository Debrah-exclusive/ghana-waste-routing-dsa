package structures;

/**
 * Unit tests for DynamicArray — normal case, boundary case, invalid input case.
 * Fill in as part of your module's evidence.
 */
public class DynamicArrayTest {

    private static int total = 0;
    private static int passed = 0;

    public static void main(String[] args) {
        runAllTests();
        System.out.println("\n========== DynamicArrayTest SUMMARY ==========");
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
        testInsertAndGet();
        testInsertAt();
        testSet();
        testRemoveByIndex();
        testRemoveByValue();
    }

    private static void runBoundaryCaseTests() {
        testResize();
        testInsertAtEnds();
        testRemoveLastElement();
        testEmptyArray();
    }

    private static void runInvalidInputTests() {
        testInvalidInitialCapacity();
        testIndexOutOfBounds();
    }

    private static void testInsertAndGet() {
        total++;
        DynamicArray<Integer> arr = new DynamicArray<>();
        arr.insert(10);
        arr.insert(20);
        if (arr.size() == 2 && arr.get(0) == 10 && arr.get(1) == 20) {
            passed++;
            System.out.println("testInsertAndGet PASSED");
        } else {
            System.out.println("testInsertAndGet FAILED");
        }
    }

    private static void testInsertAt() {
        total++;
        DynamicArray<Integer> arr = new DynamicArray<>();
        arr.insert(10);
        arr.insert(30);
        arr.insertAt(1, 20); // 10, 20, 30
        if (arr.size() == 3 && arr.get(1) == 20 && arr.get(2) == 30) {
            passed++;
            System.out.println("testInsertAt PASSED");
        } else {
            System.out.println("testInsertAt FAILED");
        }
    }

    private static void testSet() {
        total++;
        DynamicArray<Integer> arr = new DynamicArray<>();
        arr.insert(10);
        arr.set(0, 100);
        if (arr.get(0) == 100) {
            passed++;
            System.out.println("testSet PASSED");
        } else {
            System.out.println("testSet FAILED");
        }
    }

    private static void testRemoveByIndex() {
        total++;
        DynamicArray<Integer> arr = new DynamicArray<>();
        arr.insert(10);
        arr.insert(20);
        arr.insert(30);
        int removed = arr.remove(1); // remove 20
        if (removed == 20 && arr.size() == 2 && arr.get(1) == 30) {
            passed++;
            System.out.println("testRemoveByIndex PASSED");
        } else {
            System.out.println("testRemoveByIndex FAILED");
        }
    }

    private static void testRemoveByValue() {
        total++;
        DynamicArray<String> arr = new DynamicArray<>();
        arr.insert("A");
        arr.insert("B");
        arr.insert("C");
        boolean removed = arr.remove("B");
        if (removed && arr.size() == 2 && arr.get(1).equals("C") && !arr.remove("Z")) {
            passed++;
            System.out.println("testRemoveByValue PASSED");
        } else {
            System.out.println("testRemoveByValue FAILED");
        }
    }

    private static void testResize() {
        total++;
        DynamicArray<Integer> arr = new DynamicArray<>(2);
        arr.insert(1);
        arr.insert(2);
        int cap1 = arr.capacity();
        arr.insert(3); // should trigger resize
        int cap2 = arr.capacity();
        if (cap1 == 2 && cap2 == 4 && arr.size() == 3 && arr.get(2) == 3) {
            passed++;
            System.out.println("testResize PASSED");
        } else {
            System.out.println("testResize FAILED");
        }
    }

    private static void testInsertAtEnds() {
        total++;
        DynamicArray<Integer> arr = new DynamicArray<>();
        arr.insertAt(0, 20); // front
        arr.insertAt(1, 30); // back
        arr.insertAt(0, 10); // front again
        if (arr.size() == 3 && arr.get(0) == 10 && arr.get(2) == 30) {
            passed++;
            System.out.println("testInsertAtEnds PASSED");
        } else {
            System.out.println("testInsertAtEnds FAILED");
        }
    }

    private static void testRemoveLastElement() {
        total++;
        DynamicArray<Integer> arr = new DynamicArray<>();
        arr.insert(10);
        arr.remove(0);
        if (arr.isEmpty() && arr.size() == 0) {
            passed++;
            System.out.println("testRemoveLastElement PASSED");
        } else {
            System.out.println("testRemoveLastElement FAILED");
        }
    }

    private static void testEmptyArray() {
        total++;
        DynamicArray<Integer> arr = new DynamicArray<>(0);
        arr.insert(1); // should resize from 0 to 1
        if (arr.size() == 1 && arr.capacity() >= 1 && arr.get(0) == 1) {
            passed++;
            System.out.println("testEmptyArray PASSED");
        } else {
            System.out.println("testEmptyArray FAILED");
        }
    }

    private static void testInvalidInitialCapacity() {
        total++;
        try {
            new DynamicArray<>(-1);
            System.out.println("testInvalidInitialCapacity FAILED");
        } catch (IllegalArgumentException e) {
            passed++;
            System.out.println("testInvalidInitialCapacity PASSED");
        }
    }

    private static void testIndexOutOfBounds() {
        total++;
        DynamicArray<Integer> arr = new DynamicArray<>();
        arr.insert(10);
        boolean thrown = false;
        try {
            arr.get(1);
        } catch (IndexOutOfBoundsException e) {
            thrown = true;
        }
        
        try {
            arr.insertAt(2, 20);
            thrown = false;
        } catch (IndexOutOfBoundsException e) {
            // expected
        }

        if (thrown) {
            passed++;
            System.out.println("testIndexOutOfBounds PASSED");
        } else {
            System.out.println("testIndexOutOfBounds FAILED");
        }
    }
}
