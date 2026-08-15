import structures.QueueCircular;

/**
 * Unit tests for QueueCircular — normal case, boundary case, invalid input case.
 * Owner: Wiafe Franklin Asare
 *
 * Plain Java, no test framework, so it compiles and runs with nothing but a JDK:
 *
 *   javac -d bin src/main/java/structures/*.java src/test/java/structures/*.java
 *   java -cp bin QueueCircularTest
 *
 * Exit status is 0 when everything passes and 1 when anything fails, so this can
 * be wired into a build later.
 *
 * Every test that mutates the queue also asserts checkInvariant(), so a broken
 * front/rear relationship is caught at the operation that broke it rather than
 * several steps later.
 */
public class QueueCircularTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== QueueCircular unit tests ===\n");

        System.out.println("-- NORMAL CASES --");
        normalFifoOrderIsPreserved();
        normalSizeTracksContents();
        normalPeekDoesNotRemove();
        normalIndicesAdvanceOneStepAtATime();
        normalInterleavedEnqueueDequeue();
        normalContainsAndToArray();
        normalClearResetsToStartingState();

        System.out.println("\n-- BOUNDARY CASES --");
        boundaryEmptyQueue();
        boundaryCapacityOne();
        boundaryFillToExactCapacity();
        boundaryRearWrapsAround();
        boundaryFrontWrapsAround();
        boundaryManyLapsAroundTheRing();
        boundaryDrainThenRefill();
        boundaryGrowablePreservesOrderAcrossWrap();

        System.out.println("\n-- INVALID INPUT CASES --");
        invalidZeroCapacity();
        invalidNegativeCapacity();
        invalidEnqueueNull();
        invalidOfferNull();
        invalidDequeueFromEmpty();
        invalidEnqueueIntoFull();
        invalidRejectedEnqueueLeavesQueueUntouched();
        invalidContainsNullIsFalse();
        invalidNonThrowingVariantsReturnInsteadOfThrowing();

        System.out.println("\n================================");
        System.out.println("passed: " + passed + "   failed: " + failed);
        if (failed > 0) {
            System.out.println("RESULT: FAILED");
            System.exit(1);
        }
        System.out.println("RESULT: ALL TESTS PASSED");
    }

    // ------------------------------------------------------------ normal cases

    private static void normalFifoOrderIsPreserved() {
        // Requests 1-6 arrive in order; the queue must hand them back in order.
        QueueCircular<Integer> q = new QueueCircular<Integer>(10);
        for (int id = 1; id <= 6; id++) {
            q.enqueue(id);
        }
        check("invariant holds after 6 enqueues", q.checkInvariant());
        for (int id = 1; id <= 6; id++) {
            checkEquals("dequeue returns request " + id, id, q.dequeue());
        }
        check("queue is empty after draining", q.isEmpty());
        check("invariant holds after draining", q.checkInvariant());
    }

    private static void normalSizeTracksContents() {
        QueueCircular<String> q = new QueueCircular<String>(5);
        checkEquals("new queue size", 0, q.size());
        q.enqueue("REQ-1");
        checkEquals("size after 1 enqueue", 1, q.size());
        q.enqueue("REQ-2");
        q.enqueue("REQ-3");
        checkEquals("size after 3 enqueues", 3, q.size());
        q.dequeue();
        checkEquals("size after 1 dequeue", 2, q.size());
        checkEquals("capacity never changes on a fixed queue", 5, q.capacity());
        check("invariant", q.checkInvariant());
    }

    private static void normalPeekDoesNotRemove() {
        QueueCircular<Integer> q = new QueueCircular<Integer>(4);
        q.enqueue(11);
        q.enqueue(22);
        q.enqueue(33);
        checkEquals("peek returns the front", 11, q.peek());
        checkEquals("peek again returns the same front", 11, q.peek());
        checkEquals("peekRear returns the last arrival", 33, q.peekRear());
        checkEquals("size unchanged by peeking", 3, q.size());
        check("invariant", q.checkInvariant());
    }

    private static void normalIndicesAdvanceOneStepAtATime() {
        QueueCircular<Integer> q = new QueueCircular<Integer>(6);
        checkEquals("new queue front index", 0, q.frontIndex());
        checkEquals("new queue rear index sits one behind front", 5, q.rearIndex());
        q.enqueue(1);
        checkEquals("rear after first enqueue", 0, q.rearIndex());
        checkEquals("front stays put on enqueue", 0, q.frontIndex());
        q.enqueue(2);
        checkEquals("rear after second enqueue", 1, q.rearIndex());
        q.dequeue();
        checkEquals("front after first dequeue", 1, q.frontIndex());
        checkEquals("rear stays put on dequeue", 1, q.rearIndex());
        check("invariant", q.checkInvariant());
    }

    private static void normalInterleavedEnqueueDequeue() {
        // Trucks collecting while new reports keep coming in.
        QueueCircular<Integer> q = new QueueCircular<Integer>(4);
        q.enqueue(1);
        q.enqueue(2);
        checkEquals("serve 1", 1, q.dequeue());
        q.enqueue(3);
        checkEquals("serve 2", 2, q.dequeue());
        q.enqueue(4);
        q.enqueue(5);
        checkEquals("serve 3", 3, q.dequeue());
        checkEquals("serve 4", 4, q.dequeue());
        checkEquals("serve 5", 5, q.dequeue());
        check("empty after serving everything", q.isEmpty());
        check("invariant", q.checkInvariant());
    }

    private static void normalContainsAndToArray() {
        QueueCircular<Integer> q = new QueueCircular<Integer>(5);
        q.enqueue(80);
        q.enqueue(86);
        q.enqueue(92);
        check("contains a queued request", q.contains(86));
        check("does not contain an absent request", !q.contains(99));
        checkOrder("toArray is front-to-rear", q, new int[] {80, 86, 92});
        q.dequeue();
        check("dequeued request no longer contained", !q.contains(80));
        checkOrder("toArray after dequeue", q, new int[] {86, 92});
        check("invariant", q.checkInvariant());
    }

    private static void normalClearResetsToStartingState() {
        QueueCircular<Integer> q = new QueueCircular<Integer>(4);
        q.enqueue(1);
        q.enqueue(2);
        q.dequeue();
        q.clear();
        checkEquals("size after clear", 0, q.size());
        checkEquals("front reset to 0", 0, q.frontIndex());
        checkEquals("rear reset to capacity-1", 3, q.rearIndex());
        check("empty after clear", q.isEmpty());
        check("clear leaves no stale references", allSlotsNull(q));
        check("invariant", q.checkInvariant());
    }

    // ---------------------------------------------------------- boundary cases

    private static void boundaryEmptyQueue() {
        QueueCircular<Integer> q = new QueueCircular<Integer>(3);
        check("new queue is empty", q.isEmpty());
        check("new queue is not full", !q.isFull());
        checkEquals("size is 0", 0, q.size());
        checkEquals("peek on empty is null", null, q.peek());
        checkEquals("peekRear on empty is null", null, q.peekRear());
        checkEquals("poll on empty is null", null, q.poll());
        checkEquals("toArray on empty has length 0", 0, q.toArray().length);
        check("invariant holds on an empty queue", q.checkInvariant());
    }

    private static void boundaryCapacityOne() {
        // Smallest legal ring: every enqueue wraps.
        QueueCircular<Integer> q = new QueueCircular<Integer>(1);
        check("capacity-1 queue starts empty", q.isEmpty());
        q.enqueue(7);
        check("capacity-1 queue is full after one enqueue", q.isFull());
        checkEquals("front == rear on a single item", q.frontIndex(), q.rearIndex());
        checkEquals("dequeue returns the item", 7, q.dequeue());
        check("empty again", q.isEmpty());
        q.enqueue(8);
        checkEquals("slot is reused", 8, q.peek());
        check("invariant", q.checkInvariant());
    }

    private static void boundaryFillToExactCapacity() {
        QueueCircular<Integer> q = new QueueCircular<Integer>(4);
        for (int i = 1; i <= 4; i++) {
            q.enqueue(i);
            check("invariant during fill " + i, q.checkInvariant());
        }
        check("full at exactly capacity", q.isFull());
        checkEquals("size equals capacity", q.capacity(), q.size());
        checkEquals("rear sits on the last slot", 3, q.rearIndex());
        checkEquals("front still on slot 0", 0, q.frontIndex());
    }

    private static void boundaryRearWrapsAround() {
        // The core case: free the front slots, then let rear wrap into them.
        QueueCircular<Integer> q = new QueueCircular<Integer>(5);
        for (int i = 1; i <= 5; i++) {
            q.enqueue(i);
        }
        q.dequeue();
        q.dequeue();
        checkEquals("front moved to slot 2", 2, q.frontIndex());
        checkEquals("rear still on the last slot", 4, q.rearIndex());
        checkEquals("no wrap yet", 0L, q.wrapArounds());

        q.enqueue(6);
        checkEquals("rear wrapped from 4 to 0", 0, q.rearIndex());
        checkEquals("one wrap counted", 1L, q.wrapArounds());
        check("invariant after wrap", q.checkInvariant());

        q.enqueue(7);
        checkEquals("rear continues to slot 1", 1, q.rearIndex());
        check("queue is full again after reusing freed slots", q.isFull());
        checkOrder("FIFO order survives the wrap", q, new int[] {3, 4, 5, 6, 7});
    }

    private static void boundaryFrontWrapsAround() {
        QueueCircular<Integer> q = new QueueCircular<Integer>(3);
        q.enqueue(1);
        q.enqueue(2);
        q.enqueue(3);
        q.dequeue();
        q.dequeue();
        checkEquals("front on slot 2", 2, q.frontIndex());
        q.dequeue();
        checkEquals("front wrapped back to 0", 0, q.frontIndex());
        check("queue empty after full drain", q.isEmpty());
        check("invariant after front wraps", q.checkInvariant());
    }

    private static void boundaryManyLapsAroundTheRing() {
        // 1000 cycles on a 4-slot ring: indices lap the array many times over.
        QueueCircular<Integer> q = new QueueCircular<Integer>(4);
        q.enqueue(0);
        q.enqueue(1);
        boolean orderOk = true;
        boolean invariantOk = true;
        for (int i = 2; i < 1000; i++) {
            q.enqueue(i);
            if (q.dequeue() != i - 2) {
                orderOk = false;
            }
            if (!q.checkInvariant()) {
                invariantOk = false;
            }
        }
        check("FIFO order held across 1000 cycles", orderOk);
        check("invariant held across 1000 cycles", invariantOk);
        checkEquals("two items still queued", 2, q.size());
        check("indices wrapped many times", q.wrapArounds() > 400L);
        checkEquals("capacity never grew", 4, q.capacity());
    }

    private static void boundaryDrainThenRefill() {
        QueueCircular<Integer> q = new QueueCircular<Integer>(3);
        for (int lap = 0; lap < 4; lap++) {
            for (int i = 1; i <= 3; i++) {
                q.enqueue(lap * 10 + i);
            }
            check("full on lap " + lap, q.isFull());
            for (int i = 1; i <= 3; i++) {
                checkEquals("lap " + lap + " serves " + (lap * 10 + i), lap * 10 + i, q.dequeue());
            }
            check("empty on lap " + lap, q.isEmpty());
            check("invariant on lap " + lap, q.checkInvariant());
        }
    }

    private static void boundaryGrowablePreservesOrderAcrossWrap() {
        // Grow while the contents are physically split across the end of the
        // array — the copy has to re-lay them out in FIFO order, not slot order.
        QueueCircular<Integer> q = new QueueCircular<Integer>(4, true);
        q.enqueue(1);
        q.enqueue(2);
        q.enqueue(3);
        q.enqueue(4);
        q.dequeue();
        q.dequeue();
        q.enqueue(5);
        q.enqueue(6);
        check("wrapped and full before growing", q.isFull() && q.wrapArounds() > 0L);

        q.enqueue(7);
        checkEquals("capacity doubled", 8, q.capacity());
        checkEquals("one growth recorded", 1L, q.growths());
        checkEquals("front re-laid at 0", 0, q.frontIndex());
        checkOrder("FIFO order survives the growth", q, new int[] {3, 4, 5, 6, 7});
        check("invariant after growth", q.checkInvariant());
    }

    // ----------------------------------------------------- invalid input cases

    private static void invalidZeroCapacity() {
        expectThrows("capacity 0 is rejected", IllegalArgumentException.class,
                new Runnable() {
                    public void run() {
                        new QueueCircular<Integer>(0);
                    }
                });
    }

    private static void invalidNegativeCapacity() {
        expectThrows("negative capacity is rejected", IllegalArgumentException.class,
                new Runnable() {
                    public void run() {
                        new QueueCircular<Integer>(-3);
                    }
                });
    }

    private static void invalidEnqueueNull() {
        final QueueCircular<Integer> q = new QueueCircular<Integer>(3);
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
        final QueueCircular<Integer> q = new QueueCircular<Integer>(3);
        expectThrows("offer(null) is rejected too", IllegalArgumentException.class,
                new Runnable() {
                    public void run() {
                        q.offer(null);
                    }
                });
    }

    private static void invalidDequeueFromEmpty() {
        final QueueCircular<Integer> q = new QueueCircular<Integer>(3);
        expectThrows("dequeue on empty throws (underflow)", IllegalStateException.class,
                new Runnable() {
                    public void run() {
                        q.dequeue();
                    }
                });

        // Also underflow *after* use, not just on a fresh queue.
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

    private static void invalidEnqueueIntoFull() {
        final QueueCircular<Integer> q = new QueueCircular<Integer>(2);
        q.enqueue(1);
        q.enqueue(2);
        expectThrows("enqueue on a full fixed queue throws (overflow)", IllegalStateException.class,
                new Runnable() {
                    public void run() {
                        q.enqueue(3);
                    }
                });
        checkEquals("one rejection recorded", 1L, q.rejectedEnqueues());
    }

    private static void invalidRejectedEnqueueLeavesQueueUntouched() {
        QueueCircular<Integer> q = new QueueCircular<Integer>(2);
        q.enqueue(1);
        q.enqueue(2);
        int frontBefore = q.frontIndex();
        int rearBefore = q.rearIndex();
        boolean accepted = q.offer(3);
        check("offer on full returns false", !accepted);
        checkEquals("size unchanged after rejection", 2, q.size());
        checkEquals("front unchanged after rejection", frontBefore, q.frontIndex());
        checkEquals("rear unchanged after rejection", rearBefore, q.rearIndex());
        checkOrder("contents unchanged after rejection", q, new int[] {1, 2});
        check("invariant after rejection", q.checkInvariant());
    }

    private static void invalidContainsNullIsFalse() {
        QueueCircular<Integer> q = new QueueCircular<Integer>(3);
        q.enqueue(1);
        check("contains(null) is false, not an exception", !q.contains(null));
    }

    private static void invalidNonThrowingVariantsReturnInsteadOfThrowing() {
        QueueCircular<Integer> q = new QueueCircular<Integer>(1);
        checkEquals("poll on empty returns null", null, q.poll());
        check("offer on space returns true", q.offer(1));
        check("offer on full returns false", !q.offer(2));
        checkEquals("poll returns the item", 1, q.poll());
        checkEquals("poll on drained queue returns null", null, q.poll());
        check("invariant", q.checkInvariant());
    }

    // ------------------------------------------------------------- test helpers

    private static boolean allSlotsNull(QueueCircular<?> q) {
        Object[] raw = q.rawSlots();
        for (int i = 0; i < raw.length; i++) {
            if (raw[i] != null) {
                return false;
            }
        }
        return true;
    }

    private static void checkOrder(String name, QueueCircular<Integer> q, int[] expected) {
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
