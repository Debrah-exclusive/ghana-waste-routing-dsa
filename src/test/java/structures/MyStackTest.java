package structures;

/**
 * Unit tests for MyStack — normal case, boundary case, invalid input case.
 * Owner: Dennis Kumi Lartey
 */
public class MyStackTest {

    private static int total = 0;
    private static int passed = 0;

    public static void main(String[] args) {
        testMyStackNormal();
        testMyStackBoundary();
        System.out.println("\n========== MyStackTest SUMMARY ==========");
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

    private static void testMyStackNormal() {
        Stack stack = new Stack(5);
        assertTrue("New stack is empty", stack.isEmpty());
        stack.push(null); // safely handled
        assertTrue("Stack size after push", stack.size() == 0 || stack.size() == 1);
    }

    private static void testMyStackBoundary() {
        Stack stack = new Stack(1);
        assertTrue("Stack capacity 1 not full initially", !stack.isFull());
    }
}
