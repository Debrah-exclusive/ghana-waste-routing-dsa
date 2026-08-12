import structures.MyQueue;

/**
 * Unit tests for MyQueue — normal case, boundary case, invalid input case.
 * Owner: Wiafe Franklin Asare
 *
 *   javac -d bin src/main/java/structures/*.java src/test/java/structures/*.java
 *   java -cp bin MyQueueTest
 *
 * Exit status is 0 when everything passes and 1 when anything fails.
 */
public class MyQueueTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== MyQueue (linked FIFO) unit tests ===\n");

        System.out.println("-- NORMAL CASES --");
        normalFifoOrderIsPreserved();
        normalSizeTracksContents();
        normalPeekDoesNotRemove();
        normalInterleavedEnqueueDequeue();
        normalContainsAndToArray();

        System.out.println("\n-- BOUNDARY CASES --");
        boundaryEmptyQueue();
        boundarySingleItem();
        boundaryDrainThenReuse();
        boundaryNoCapacityCeiling();
        boundaryClearReleasesTheChain();

        System.out.println("\n-- INVALID INPUT CASES --");
        invalidEnqueueNull();
        invalidOfferNull();
        invalidDequeueFromEmpty();
        invalidContainsNullIsFalse();
        invalidPollReturnsNullInsteadOfThrowing();

        System.out.println("\n========================================");
        System.out.println("passed: " + passed + "   failed: " + failed);
        if (failed > 0) {
            System.out.println("RESULT: FAILED");
            System.exit(1);
        }
        System.out.println("RESULT: ALL TESTS PASSED");
    }

    // ------------------------------------------------------------ normal cases

    private static void normalFifoOrderIsPreserved() {
        MyQueue<Integer> q = new MyQueue<Integer>();
        for (int id = 1; id <= 6; id++) {
            q.enqueue(id);
        }
        check("invariant after 6 enqueues", q.checkInvariant());
        for (int id = 1; id <= 6; id++) {
            checkEquals("dequeue returns request " + id, id, q.dequeue());
        }
        check("empty after draining", q.isEmpty());
        check("invariant after draining", q.checkInvariant());
    }

    private static void normalSizeTracksContents() {
        MyQueue<String> q = new MyQueue<String>();
        checkEquals("new queue size", 0, q.size());
        q.enqueue("REQ-1");
        q.enqueue("REQ-2");
        q.enqueue("REQ-3");
        checkEquals("size after 3 enqueues", 3, q.size());
        q.dequeue();
        checkEquals("size after 1 dequeue", 2, q.size());
        check("invariant", q.checkInvariant());
    }

    private static void normalPeekDoesNotRemove() {
        MyQueue<Integer> q = new MyQueue<Integer>();
        q.enqueue(11);
        q.enqueue(22);
        q.enqueue(33);
        checkEquals("peek returns the front", 11, q.peek());
        checkEquals("peek again returns the same front", 11, q.peek());
        checkEquals("peekRear returns the last arrival", 33, q.peekRear());
        checkEquals("size unchanged by peeking", 3, q.size());
        check("invariant", q.checkInvariant());
    }

    private static void normalInterleavedEnqueueDequeue() {
        MyQueue<Integer> q = new MyQueue<Integer>();
        q.enqueue(1);
        q.enqueue(2);
        checkEquals("serve 1", 1, q.dequeue());
        q.enqueue(3);
        checkEquals("serve 2", 2, q.dequeue());
        q.enqueue(4);
        checkEquals("serve 3", 3, q.dequeue());
        checkEquals("serve 4", 4, q.dequeue());
        check("empty after serving everything", q.isEmpty());
        check("invariant", q.checkInvariant());
    }

    private static void normalContainsAndToArray() {
        MyQueue<Integer> q = new MyQueue<Integer>();
        q.enqueue(80);
        q.enqueue(86);
        q.enqueue(92);
        check("contains a queued request", q.contains(86));
        check("does not contain an absent request", !q.contains(99));
        checkOrder("toArray is front-to-rear", q, new int[] {80, 86, 92});
        q.dequeue();
        checkOrder("toArray after dequeue", q, new int[] {86, 92});
        check("invariant", q.checkInvariant());
    }

    // ---------------------------------------------------------- boundary cases

    private static void boundaryEmptyQueue() {
        MyQueue<Integer> q = new MyQueue<Integer>();
        check("new queue is empty", q.isEmpty());
        checkEquals("size is 0", 0, q.size());
        checkEquals("peek on empty is null", null, q.peek());
        checkEquals("peekRear on empty is null", null, q.peekRear());
        checkEquals("poll on empty is null", null, q.poll());
        checkEquals("toArray on empty has length 0", 0, q.toArray().length);
        check("invariant holds on an empty queue", q.checkInvariant());
    }

    private static void boundarySingleItem() {
        // One item means head and tail point at the same node — the case where
        // a dangling tail after dequeue would go unnoticed.
        MyQueue<Integer> q = new MyQueue<Integer>();
        q.enqueue(7);
        checkEquals("peek and peekRear agree on one item", q.peek(), q.peekRear());
        checkEquals("dequeue returns the item", 7, q.dequeue());
        check("empty again", q.isEmpty());
        checkEquals("peekRear is null, tail was cleared", null, q.peekRear());
        check("invariant after emptying a one-item queue", q.checkInvariant());
    }

    private static void boundaryDrainThenReuse() {
        MyQueue<Integer> q = new MyQueue<Integer>();
        for (int lap = 0; lap < 3; lap++) {
            for (int i = 1; i <= 4; i++) {
                q.enqueue(lap * 10 + i);
            }
            for (int i = 1; i <= 4; i++) {
                checkEquals("lap " + lap + " serves " + (lap * 10 + i), lap * 10 + i, q.dequeue());
            }
            check("empty on lap " + lap, q.isEmpty());
            check("invariant on lap " + lap, q.checkInvariant());
        }
    }

    private static void boundaryNoCapacityCeiling() {
        // The whole point of the linked variant: no fixed capacity to overflow.
        MyQueue<Integer> q = new MyQueue<Integer>();
        for (int i = 0; i < 5000; i++) {
            q.enqueue(i);
        }
        checkEquals("5000 items accepted", 5000, q.size());
        check("invariant at 5000 items", q.checkInvariant());
        boolean orderOk = true;
        for (int i = 0; i < 5000; i++) {
            if (q.dequeue() != i) {
                orderOk = false;
                break;
            }
        }
        check("FIFO order held across 5000 items", orderOk);
        check("empty after draining 5000", q.isEmpty());
    }

    private static void boundaryClearReleasesTheChain() {
        MyQueue<Integer> q = new MyQueue<Integer>();
        q.enqueue(1);
        q.enqueue(2);
        q.enqueue(3);
        q.clear();
        checkEquals("size after clear", 0, q.size());
        check("empty after clear", q.isEmpty());
        checkEquals("peek after clear", null, q.peek());
        checkEquals("peekRear after clear", null, q.peekRear());
        check("invariant after clear", q.checkInvariant());
        q.enqueue(9);
        checkEquals("queue is reusable after clear", 9, q.peek());
        check("invariant after reuse", q.checkInvariant());
    }

    // ----------------------------------------------------- invalid input cases

    private static void invalidEnqueueNull() {
        final MyQueue<Integer> q = new MyQueue<Integer>();
        expectThrows("enqueue(null) is rejected", IllegalArgumentException.class,
                new Runnable() {
                    public void run() {
                        q.enqueue(null);
                    }
                });
        checkEquals("rejected null did not change size", 0, q.size());
        check("invariant after rejected null", q.checkInvariant());
    }

    private static void invalidOfferNull() {
        final MyQueue<Integer> q = new MyQueue<Integer>();
        expectThrows("offer(null) is rejected too", IllegalArgumentException.class,
                new Runnable() {
                    public void run() {
                        q.offer(null);
                    }
                });
    }

    private static void invalidDequeueFromEmpty() {
        final MyQueue<Integer> q = new MyQueue<Integer>();
        expectThrows("dequeue on empty throws (underflow)", IllegalStateException.class,
                new Runnable() {
                    public void run() {
                        q.dequeue();
                    }
                });
        q.enqueue(1);
        q.dequeue();
        expectThrows("dequeue after draining throws", IllegalStateException.class,
                new Runnable() {
                    public void run() {
                        q.dequeue();
                    }
                });
        check("invariant after underflow attempts", q.checkInvariant());
    }

    private static void invalidContainsNullIsFalse() {
        MyQueue<Integer> q = new MyQueue<Integer>();
        q.enqueue(1);
        check("contains(null) is false, not an exception", !q.contains(null));
    }

    private static void invalidPollReturnsNullInsteadOfThrowing() {
        MyQueue<Integer> q = new MyQueue<Integer>();
        checkEquals("poll on empty returns null", null, q.poll());
        check("offer always succeeds", q.offer(1));
        checkEquals("poll returns the item", 1, q.poll());
        checkEquals("poll on drained queue returns null", null, q.poll());
        check("invariant", q.checkInvariant());
    }

    // ------------------------------------------------------------- test helpers

    private static void checkOrder(String name, MyQueue<Integer> q, int[] expected) {
        Object[] actual = q.toArray();
        if (actual.length != expected.length) {
            fail(name, "expected " + expected.length + " items, got " + actual.length);
            return;
        }
        for (int i = 0; i < expected.length; i++) {
            if (!Integer.valueOf(expected[i]).equals(actual[i])) {
                fail(name, "expected " + join(expected) + ", got " + join(actual));
                return;
            }
        }
        pass(name + "  " + join(expected));
    }

    private static void check(String name, boolean condition) {
        if (condition) {
            pass(name);
        } else {
            fail(name, "condition was false");
        }
    }

    private static void checkEquals(String name, Object expected, Object actual) {
        boolean same = expected == null ? actual == null : expected.equals(actual);
        if (same) {
            pass(name + "  (" + actual + ")");
        } else {
            fail(name, "expected " + expected + ", got " + actual);
        }
    }

    private static void expectThrows(String name, Class<? extends RuntimeException> expected,
                                     Runnable action) {
        try {
            action.run();
            fail(name, "expected " + expected.getSimpleName() + " but nothing was thrown");
        } catch (RuntimeException e) {
            if (expected.isInstance(e)) {
                pass(name + "  (" + e.getClass().getSimpleName() + ": " + e.getMessage() + ")");
            } else {
                fail(name, "expected " + expected.getSimpleName()
                        + " but got " + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
    }

    private static String join(int[] values) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(values[i]);
        }
        return sb.append(']').toString();
    }

    private static String join(Object[] values) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(values[i]);
        }
        return sb.append(']').toString();
    }

    private static void pass(String name) {
        passed++;
        System.out.println("  PASS  " + name);
    }

    private static void fail(String name, String detail) {
        failed++;
        System.out.println("  FAIL  " + name + "  -> " + detail);
    }
}
