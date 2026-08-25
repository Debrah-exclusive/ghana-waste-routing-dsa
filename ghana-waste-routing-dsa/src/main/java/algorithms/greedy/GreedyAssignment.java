package algorithms.greedy;

import structures.PriorityQueueHeap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Owner: Derrick Debrah
 * TODO: implement + produce a trace table for this algorithm.
 * Log runtime results to the algorithm_runs table/CSV when benchmarking.
 */
public class GreedyAssignment {
 
    // ---------- domain model ----------
 
    public static class ServiceRequest {
        public final String requestId;
        public final int urgency;       // 1 (low) .. 5 (critical)
        public final int truckHoursCost; // estimated truck-hours to serve this request
        public final double x, y;        // simplified local coordinates
 
        public ServiceRequest(String requestId, int urgency, int truckHoursCost, double x, double y) {
            this.requestId = requestId;
            this.urgency = urgency;
            this.truckHoursCost = truckHoursCost;
            this.x = x;
            this.y = y;
        }
 
        @Override
        public String toString() {
            return requestId + "(urgency=" + urgency + ", cost=" + truckHoursCost + "h)";
        }
    }
 
    public static class Resource {
        public final String resourceId;
        public double x, y;
        public boolean available = true;
 
        public Resource(String resourceId, double x, double y) {
            this.resourceId = resourceId;
            this.x = x;
            this.y = y;
        }
    }
 
    public static class Assignment {
        public final ServiceRequest request;
        public final Resource resource;
        public final double distance;
 
        public Assignment(ServiceRequest request, Resource resource, double distance) {
            this.request = request;
            this.resource = resource;
            this.distance = distance;
        }
 
        @Override
        public String toString() {
            return String.format("%s -> %s (dist=%.2f)", request.requestId, resource.resourceId, distance);
        }
    }
 
    // ---------- greedy algorithm ----------
 
    /**
     * Assigns requests to resources greedily, highest urgency first.
     * Also returns a step-by-step trace (dispatch order trace, required
     * evidence) via the trace list passed in - each entry is a
     * human-readable line describing that step's decision.
     */
    public static List<Assignment> assign(List<ServiceRequest> requests,
                                           List<Resource> resources,
                                           List<String> trace) {
        if (requests == null || resources == null) {
            throw new IllegalArgumentException("requests and resources must not be null");
        }
 
        // Max-heap: highest urgency comes out first. Ties broken by lower
        // truckHoursCost first (cheaper request served first among equals).
        Comparator<ServiceRequest> byUrgencyDesc = (a, b) -> {
            if (a.urgency != b.urgency) {
                return b.urgency - a.urgency;
            }
            return a.truckHoursCost - b.truckHoursCost;
        };
 
        PriorityQueueHeap<ServiceRequest> pending = new PriorityQueueHeap<>(byUrgencyDesc);
        for (ServiceRequest r : requests) {
            pending.insert(r);
        }
 
        List<Assignment> assignments = new ArrayList<>();
        int step = 1;
 
        while (!pending.isEmpty()) {
            ServiceRequest next = pending.extractTop();
            Resource nearest = nearestAvailable(next, resources);
 
            if (nearest == null) {
                if (trace != null) {
                    trace.add(String.format("Step %d: %s popped (highest remaining urgency) - "
                            + "NO trucks available, request stays pending.", step, next));
                }
                step++;
                continue;
            }
 
            double dist = distance(next, nearest);
            nearest.available = false;
            Assignment a = new Assignment(next, nearest, dist);
            assignments.add(a);
 
            if (trace != null) {
                trace.add(String.format("Step %d: %s popped (highest remaining urgency) -> "
                        + "assigned nearest available truck %s (distance %.2f).",
                        step, next, nearest.resourceId, dist));
            }
            step++;
        }
 
        return assignments;
    }
 
    private static Resource nearestAvailable(ServiceRequest r, List<Resource> resources) {
        Resource best = null;
        double bestDist = Double.MAX_VALUE;
        for (Resource res : resources) {
            if (!res.available) continue;
            double d = distance(r, res);
            if (d < bestDist) {
                bestDist = d;
                best = res;
            }
        }
        return best;
    }
 
    private static double distance(ServiceRequest r, Resource res) {
        double dx = r.x - res.x;
        double dy = r.y - res.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
 
    // ---------- required counterexample: greedy fails ----------
 
    /**
     * Section 10 of the brief requires "at least one counterexample where
     * greedy fails". This method demonstrates it for a *budget-constrained*
     * version of the problem: one truck has only 6 truck-hours left today,
     * and we must choose which requests to serve to maximise total
     * urgency served (a 0/1-knapsack-style selection, NOT a pure
     * "serve nearest highest-urgency first" problem).
     *
     * Data:
     *   R1: urgency 5 (critical), cost 6 hours
     *   R2: urgency 3, cost 3 hours
     *   R3: urgency 3, cost 3 hours
     *
     * Greedy-by-urgency picks R1 first (urgency 5) -> consumes all 6
     * hours -> total urgency served = 5.
     *
     * Optimal picks R2 + R3 (3 + 3 = 6 hours) -> total urgency served
     * = 3 + 3 = 6, which is BETTER than greedy's 5.
     *
     * Conclusion: greedy-by-urgency is correct for today's un-budgeted
     * "serve next request" dispatch rule, but is NOT optimal once a hard
     * truck-hours budget is introduced. That budgeted version is exactly
     * what the Dynamic Programming module (0/1 knapsack) solves instead -
     * this is the documented handoff between the Greedy and DP sections
     * of the report.
     */
    public static void counterexample() {
        int budgetHours = 6;
 
        int[] urgency   = {5, 3, 3};
        int[] costHours = {6, 3, 3};
        String[] ids     = {"R1", "R2", "R3"};
 
        // Greedy: sort by urgency descending, take while budget allows.
        int greedyTotal = 0;
        int remaining = budgetHours;
        // requests already sorted by urgency desc: R1, R2, R3
        for (int i = 0; i < ids.length; i++) {
            if (costHours[i] <= remaining) {
                greedyTotal += urgency[i];
                remaining -= costHours[i];
            }
        }
 
        // Optimal (brute force over 3 items - fine for a small illustrative case)
        int optimalTotal = 0;
        int n = ids.length;
        for (int mask = 0; mask < (1 << n); mask++) {
            int cost = 0, value = 0;
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) {
                    cost += costHours[i];
                    value += urgency[i];
                }
            }
            if (cost <= budgetHours) {
                optimalTotal = Math.max(optimalTotal, value);
            }
        }
 
        System.out.println("Greedy counterexample (budget = " + budgetHours + " truck-hours):");
        System.out.println("  Greedy-by-urgency total urgency served  = " + greedyTotal + " (picks R1 only)");
        System.out.println("  Optimal (brute-force) total urgency served = " + optimalTotal + " (picks R2 + R3)");
        System.out.println("  -> Greedy is SUBOPTIMAL here; see Thelma's DP module for the correct approach.");
    }
}
