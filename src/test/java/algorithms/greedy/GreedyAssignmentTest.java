package algorithms.greedy;

import algorithms.greedy.GreedyAssignment.Assignment;
import algorithms.greedy.GreedyAssignment.Resource;
import algorithms.greedy.GreedyAssignment.ServiceRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for GreedyAssignment - normal case, boundary case, invalid input case.
 * Also runs the required greedy counterexample and prints the dispatch trace.
 */
public class GreedyAssignmentTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== RUNNING GREEDY ASSIGNMENT TESTS ===");

        testNormalCase_highestUrgencyAssignedFirst();
        testBoundaryCase_moreRequestsThanTrucks();
        testBoundaryCase_emptyRequestList();
        testInvalidInput_nullListsThrow();

        System.out.println("\n" + passed + " passed, " + failed + " failed.");

        System.out.println("\n--- Dispatch order trace (sample run) ---");
        runSampleTraceDemo();

        System.out.println("\n--- Required greedy counterexample ---");
        GreedyAssignment.counterexample();

        if (failed > 0) {
            System.exit(1);
        }
    }

    // ---- 1. NORMAL CASE ----

    private static void testNormalCase_highestUrgencyAssignedFirst() {
        List<ServiceRequest> requests = Arrays.asList(
                new ServiceRequest("REQ1", 2, 2, 0, 0),
                new ServiceRequest("REQ2", 5, 2, 0, 0),
                new ServiceRequest("REQ3", 3, 2, 0, 0)
        );
        List<Resource> resources = new ArrayList<>(Arrays.asList(
                new Resource("TRUCK1", 0, 0),
                new Resource("TRUCK2", 0, 0),
                new Resource("TRUCK3", 0, 0)
        ));

        List<Assignment> result = GreedyAssignment.assign(requests, resources, null);
        check("first assignment serves the highest-urgency request (REQ2)",
                result.get(0).request.requestId.equals("REQ2"));
        check("all 3 requests get assigned when trucks >= requests", result.size() == 3);
    }

    // ---- 2. BOUNDARY CASES ----

    private static void testBoundaryCase_moreRequestsThanTrucks() {
        List<ServiceRequest> requests = Arrays.asList(
                new ServiceRequest("REQ1", 4, 1, 0, 0),
                new ServiceRequest("REQ2", 5, 1, 0, 0)
        );
        List<Resource> resources = new ArrayList<>(Arrays.asList(
                new Resource("TRUCK1", 0, 0)
        ));

        List<String> trace = new ArrayList<>();
        List<Assignment> result = GreedyAssignment.assign(requests, resources, trace);
        check("only 1 request assigned when only 1 truck is available", result.size() == 1);
        check("the assigned one is the highest-urgency request", result.get(0).request.requestId.equals("REQ2"));
        check("trace records the unassigned request too", trace.size() == 2);
    }

    private static void testBoundaryCase_emptyRequestList() {
        List<ServiceRequest> requests = new ArrayList<>();
        List<Resource> resources = new ArrayList<>(Arrays.asList(new Resource("TRUCK1", 0, 0)));

        List<Assignment> result = GreedyAssignment.assign(requests, resources, null);
        check("empty request list produces empty assignment list", result.isEmpty());
    }

    // ---- 3. INVALID INPUT CASES ----

    private static void testInvalidInput_nullListsThrow() {
        boolean threw1 = false;
        try {
            GreedyAssignment.assign(null, new ArrayList<>(), null);
        } catch (IllegalArgumentException e) {
            threw1 = true;
        }
        check("assign(null requests, ...) throws IllegalArgumentException", threw1);

        boolean threw2 = false;
        try {
            GreedyAssignment.assign(new ArrayList<>(), null, null);
        } catch (IllegalArgumentException e) {
            threw2 = true;
        }
        check("assign(..., null resources, ...) throws IllegalArgumentException", threw2);
    }

    // ---- 4. SAMPLE DISPATCH TRACE DEMO ----

    private static void runSampleTraceDemo() {
        List<ServiceRequest> requests = Arrays.asList(
                new ServiceRequest("BinOverflow-Osu", 4, 2, 1, 2),
                new ServiceRequest("IllegalDump-Madina", 5, 3, 5, 1),
                new ServiceRequest("MissedPickup-Adenta", 2, 1, 8, 8),
                new ServiceRequest("BinOverflow-Tema", 4, 2, 0, 9)
        );
        List<Resource> resources = new ArrayList<>(Arrays.asList(
                new Resource("TRUCK-A", 0, 0),
                new Resource("TRUCK-B", 6, 6)
        ));

        List<String> trace = new ArrayList<>();
        GreedyAssignment.assign(requests, resources, trace);
        for (String line : trace) {
            System.out.println(line);
        }
    }

    // ---- HELPER ----

    private static void check(String description, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("  [PASS] " + description);
        } else {
            failed++;
            System.out.println("  [FAIL] " + description);
        }
    }
}
