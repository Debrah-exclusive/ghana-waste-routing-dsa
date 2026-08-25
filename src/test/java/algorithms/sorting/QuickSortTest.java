package algorithms.sorting;

import algorithms.greedy.GreedyAssignment.ServiceRequest;

/**
 * Unit tests for QuickSort — normal case, boundary case, invalid input case.
 * Owner: Dennis Kumi Lartey
 */
public class QuickSortTest {

    private static int total = 0;
    private static int passed = 0;

    public static void main(String[] args) {
        testQuickSortNormal();
        testQuickSortBoundary();
        testQuickSortInvalid();
        System.out.println("\n========== QuickSortTest SUMMARY ==========");
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

    private static void testQuickSortNormal() {
        ServiceRequest[] reqs = new ServiceRequest[]{
                new ServiceRequest("SR1", 1, 1, 0, 0),
                new ServiceRequest("SR2", 5, 1, 0, 0),
                new ServiceRequest("SR3", 4, 1, 0, 0)
        };
        QuickSort.sortByUrgency(reqs);
        assertTrue("QuickSort Urgency Descending", reqs[0].urgency == 5 && reqs[1].urgency == 4 && reqs[2].urgency == 1);
    }

    private static void testQuickSortBoundary() {
        ServiceRequest[] reqs = new ServiceRequest[]{
                new ServiceRequest("SR1", 3, 1, 0, 0)
        };
        QuickSort.sortByUrgency(reqs);
        assertTrue("QuickSort Single Request", reqs[0].urgency == 3);

        ServiceRequest[] empty = new ServiceRequest[0];
        QuickSort.sortByUrgency(empty);
        assertTrue("QuickSort Empty Requests", empty.length == 0);
    }

    private static void testQuickSortInvalid() {
        ServiceRequest[] nullReqs = null;
        QuickSort.sortByUrgency(nullReqs);
        assertTrue("QuickSort Handles Null Requests Safely", true);
    }
}
