package algorithms.sorting;

import algorithms.greedy.GreedyAssignment.ServiceRequest;

/**
 * Unit tests for MergeSort — normal case, boundary case, invalid input case.
 * Owner: Dennis Kumi Lartey
 */
public class MergeSortTest {

    private static int total = 0;
    private static int passed = 0;

    public static void main(String[] args) {
        testMergeSortNormal();
        testMergeSortBoundary();
        testMergeSortInvalid();
        System.out.println("\n========== MergeSortTest SUMMARY ==========");
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

    private static void testMergeSortNormal() {
        ServiceRequest[] reqs = new ServiceRequest[]{
                new ServiceRequest("SR1", 2, 1, 0, 0),
                new ServiceRequest("SR2", 5, 1, 0, 0),
                new ServiceRequest("SR3", 3, 1, 0, 0)
        };
        MergeSort.sortByUrgency(reqs);
        assertTrue("MergeSort Urgency Descending", reqs[0].urgency == 5 && reqs[1].urgency == 3 && reqs[2].urgency == 2);
    }

    private static void testMergeSortBoundary() {
        ServiceRequest[] reqs = new ServiceRequest[]{
                new ServiceRequest("SR1", 4, 1, 0, 0)
        };
        MergeSort.sortByUrgency(reqs);
        assertTrue("MergeSort Single Request", reqs[0].urgency == 4);

        ServiceRequest[] empty = new ServiceRequest[0];
        MergeSort.sortByUrgency(empty);
        assertTrue("MergeSort Empty Requests", empty.length == 0);
    }

    private static void testMergeSortInvalid() {
        ServiceRequest[] nullReqs = null;
        MergeSort.sortByUrgency(nullReqs);
        assertTrue("MergeSort Handles Null Requests Safely", true);
    }
}
