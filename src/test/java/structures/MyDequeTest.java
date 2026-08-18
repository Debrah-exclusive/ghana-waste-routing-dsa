package structures;

/**
 * Unit tests for MyDeque — normal case, boundary case, invalid input case.
 * Fill in as part of your module's evidence.
 */
public class MyDequeTest {
 
    private static int passed = 0;
    private static int failed = 0;
 
    public static void main(String[] args) {
        testAddRearAndRemoveFront_normal();
        testAddFrontAndRemoveRear_normal();
        testMixedFrontRearOperations_normal();
        testEmptyDeque_boundary();
        testSingleElement_boundary();
        testResizeGrowth_boundary();
        testWrapAroundAfterMixedRemovals_boundary();
        testRemoveFrontOnEmpty_invalid();
        testRemoveRearOnEmpty_invalid();
        testPeekFrontOnEmpty_invalid();
        testPeekRearOnEmpty_invalid();
        testNegativeCapacity_invalid();
        testNullValue_invalid();
 
        System.out.println();
        System.out.println("=== MyDequeTest results: " + passed + " passed, " + failed + " failed ===");
        if (failed > 0) {
            System.exit(1);
        }
    }
 
    // ---------- NORMAL CASES ----------
 
    private static void testAddRearAndRemoveFront_normal() {
        MyDeque<String> dq = new MyDeque<>();
        dq.addRear("A");
        dq.addRear("B");
        dq.addRear("C");
        check("addRear x3 then removeFront order",
                dq.removeFront().equals("A") &&
                dq.removeFront().equals("B") &&
                dq.removeFront().equals("C"));
    }
 
    private static void testAddFrontAndRemoveRear_normal() {
        MyDeque<String> dq = new MyDeque<>();
        dq.addFront("A");
        dq.addFront("B");
        dq.addFront("C");
        // front -> C, B, A <- rear
        check("addFront x3 then removeRear order",
                dq.removeRear().equals("A") &&
                dq.removeRear().equals("B") &&
                dq.removeRear().equals("C"));
    }
 
    private static void testMixedFrontRearOperations_normal() {
        MyDeque<Integer> dq = new MyDeque<>();
        dq.addRear(1);   // [1]
        dq.addFront(0);  // [0,1]
        dq.addRear(2);   // [0,1,2]
        dq.addFront(-1); // [-1,0,1,2]
        Object[] snap = dq.toArrayFrontToRear();
        check("mixed addFront/addRear produces correct order",
                snap.length == 4 &&
                snap[0].equals(-1) && snap[1].equals(0) &&
                snap[2].equals(1) && snap[3].equals(2));
    }
 
    // ---------- BOUNDARY CASES ----------
 
    private static void testEmptyDeque_boundary() {
        MyDeque<Integer> dq = new MyDeque<>();
        check("new deque is empty", dq.isEmpty());
        check("new deque size is 0", dq.size() == 0);
    }
 
    private static void testSingleElement_boundary() {
        MyDeque<Integer> dq = new MyDeque<>();
        dq.addFront(42);
        boolean frontRearSame = dq.peekFront().equals(42) && dq.peekRear().equals(42);
        check("single element: front equals rear", frontRearSame);
        dq.removeFront();
        check("after removing only element, deque is empty again", dq.isEmpty());
    }
 
    private static void testResizeGrowth_boundary() {
        // default capacity is 8; push past it to force resize()
        MyDeque<Integer> dq = new MyDeque<>(4);
        for (int i = 0; i < 20; i++) {
            dq.addRear(i);
        }
        boolean orderPreserved = true;
        for (int i = 0; i < 20; i++) {
            if (!dq.removeFront().equals(i)) {
                orderPreserved = false;
                break;
            }
        }
        check("resize() preserves FIFO order across growth", orderPreserved);
    }
 
    private static void testWrapAroundAfterMixedRemovals_boundary() {
        // force the circular index to wrap by alternating add/remove near capacity
        MyDeque<Integer> dq = new MyDeque<>(4);
        dq.addRear(1);
        dq.addRear(2);
        dq.removeFront();       // front pointer advances, now near end of array
        dq.addRear(3);
        dq.addRear(4);
        dq.addRear(5);          // forces wrap-around within capacity, then resize
        Object[] snap = dq.toArrayFrontToRear();
        check("wrap-around then resize keeps correct order",
                snap.length == 4 &&
                snap[0].equals(2) && snap[1].equals(3) &&
                snap[2].equals(4) && snap[3].equals(5));
    }
 
    // ---------- INVALID INPUT CASES ----------
 
    private static void testRemoveFrontOnEmpty_invalid() {
        MyDeque<Integer> dq = new MyDeque<>();
        boolean threw = false;
        try {
            dq.removeFront();
        } catch (java.util.NoSuchElementException e) {
            threw = true;
        }
        check("removeFront on empty deque throws NoSuchElementException", threw);
    }
 
    private static void testRemoveRearOnEmpty_invalid() {
        MyDeque<Integer> dq = new MyDeque<>();
        boolean threw = false;
        try {
            dq.removeRear();
        } catch (java.util.NoSuchElementException e) {
            threw = true;
        }
        check("removeRear on empty deque throws NoSuchElementException", threw);
    }
 
    private static void testPeekFrontOnEmpty_invalid() {
        MyDeque<Integer> dq = new MyDeque<>();
        boolean threw = false;
        try {
            dq.peekFront();
        } catch (java.util.NoSuchElementException e) {
            threw = true;
        }
        check("peekFront on empty deque throws NoSuchElementException", threw);
    }
 
    private static void testPeekRearOnEmpty_invalid() {
        MyDeque<Integer> dq = new MyDeque<>();
        boolean threw = false;
        try {
            dq.peekRear();
        } catch (java.util.NoSuchElementException e) {
            threw = true;
        }
        check("peekRear on empty deque throws NoSuchElementException", threw);
    }
 
    private static void testNegativeCapacity_invalid() {
        boolean threw = false;
        try {
            new MyDeque<Integer>(-5);
        } catch (IllegalArgumentException e) {
            threw = true;
        }
        check("negative initial capacity throws IllegalArgumentException", threw);
    }
 
    private static void testNullValue_invalid() {
        MyDeque<String> dq = new MyDeque<>();
        boolean frontThrew = false;
        boolean rearThrew = false;
        try { dq.addFront(null); } catch (IllegalArgumentException e) { frontThrew = true; }
        try { dq.addRear(null); } catch (IllegalArgumentException e) { rearThrew = true; }
        check("null insertions are rejected without changing size",
                frontThrew && rearThrew && dq.size() == 0);
    }

    // ---------- helper ----------
 
    private static void check(String description, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("[PASS] " + description);
        } else {
            failed++;
            System.out.println("[FAIL] " + description);
        }
    }
}
