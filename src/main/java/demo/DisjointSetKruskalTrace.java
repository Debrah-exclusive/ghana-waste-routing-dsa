package demo;

import structures.DisjointSet;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Owner: Emmanuel Aseda Kow Bentsil
 *
 * "Produce Kruskal connectivity trace" evidence for the Disjoint Set module:
 * sorts a Roads/edges CSV by weight and runs Kruskal's cycle-detection logic
 * (accept if the two endpoints are in different components, reject if they'd
 * close a cycle) using {@link DisjointSet} directly, printing and exporting
 * every step. This demonstrates HOW the structure drives the algorithm; the
 * full MST optimisation (choosing the actual minimum spanning tree, total
 * cost reporting, etc.) is Adam Mohammed's module
 * (algorithms.graph.PrimKruskal) — this class only owns the trace evidence.
 *
 * <p>Runs against the real {@code data/roads.csv} (104 rows, real UG Legon
 * campus locations already in this repo) if present, so the trace is backed
 * by genuine local data rather than an invented sample. That file's columns
 * (road_id, from_location_id, to_location_id, distance_km, travel_time_min,
 * condition_weight) are auto-detected and mapped; the official
 * {@code db/seed/roads.csv} 5-column format (fromLocationId, toLocationId,
 * distance, travelTime, roadConditionWeight) is also supported once that
 * file is filled in — same loader, no code changes needed either way.
 *
 * <p>NOTE for the team: data/roads.csv / data/locations.csv already contain
 * real Legon campus data (52 locations, 104 roads) but haven't been copied
 * into db/seed/ yet or confirmed as the official dataset — please confirm
 * with Elsie / whoever added them before treating this as the final
 * deliverable dataset for the report and DB load.
 *
 * <p>Run it standalone:
 *   java -cp bin demo.DisjointSetKruskalTrace [path/to/roads.csv]
 *
 * Or call {@link #run(String)} from menu.ConsoleMenu (integration lead: this
 * is the single entry point for this module's trace demo).
 */
public class DisjointSetKruskalTrace {

    /** One road/edge record, weight-normalised regardless of source CSV format. */
    private record RoadEdge(String from, String to, double distanceKm, double travelTimeMin, double conditionWeight) {
        double kruskalWeight() {
            // conditionWeight here is a >=1.0 penalty multiplier (1.0 = good road,
            // higher = worse), consistent with the values already in data/roads.csv.
            return distanceKm * conditionWeight;
        }
    }

    /** One row of the printed/exported trace. */
    private record TraceStep(int step, RoadEdge edge, boolean accepted, String reason,
                              int componentCountAfter, int mstEdgeCountAfter) {
    }

    public static void main(String[] args) throws IOException {
        String csvPath = args.length > 0 ? args[0] : "data/roads.csv";
        run(csvPath);
    }

    public static void run(String csvPath) throws IOException {
        Path path = Path.of(csvPath);
        if (!Files.exists(path)) {
            System.out.println("No roads CSV found at " + csvPath
                    + " -- pass a path, or run from the repo root once data/roads.csv exists.");
            return;
        }

        List<RoadEdge> edges = loadEdges(path);
        System.out.println("Loaded " + edges.size() + " road edges from " + path);
        System.out.println();

        DisjointSet<String> ds = new DisjointSet<>(false); // union by rank
        List<TraceStep> trace = runTrace(edges, ds);
        printSummary(trace, ds);

        Path out = Path.of("results/csv/aseda_disjoint_set_kruskal_trace.csv");
        Files.createDirectories(out.getParent());
        exportCsv(trace, out);
        System.out.println("\nFull trace exported to " + out.toAbsolutePath());
    }

    static List<TraceStep> runTrace(List<RoadEdge> edges, DisjointSet<String> ds) {
        List<RoadEdge> sorted = new ArrayList<>(edges);
        sorted.sort(Comparator.comparingDouble(RoadEdge::kruskalWeight));

        List<TraceStep> trace = new ArrayList<>();
        int mstEdges = 0;
        int step = 0;

        for (RoadEdge e : sorted) {
            step++;
            ds.makeSet(e.from());
            ds.makeSet(e.to());

            String rootBefore = ds.find(e.from());
            boolean accepted = ds.union(e.from(), e.to());
            if (accepted) {
                mstEdges++;
            }
            String reason = accepted
                    ? "different components -> merged"
                    : "same component already (root " + rootBefore + ") -> would close a cycle";

            trace.add(new TraceStep(step, e, accepted, reason, ds.componentCount(), mstEdges));
        }
        return trace;
    }

    private static void printSummary(List<TraceStep> trace, DisjointSet<String> ds) {
        System.out.printf("%-5s %-24s %-9s %-9s %-9s %s%n",
                "step", "edge (weight)", "decision", "#comps", "#MST", "reason");
        double totalWeight = 0;
        int accepted = 0;
        for (TraceStep t : trace) {
            System.out.printf("%-5d %-24s %-9s %-9d %-9d %s%n",
                    t.step(),
                    String.format("%s-%s (%.3f)", t.edge().from(), t.edge().to(), t.edge().kruskalWeight()),
                    t.accepted() ? "ACCEPT" : "reject",
                    t.componentCountAfter(), t.mstEdgeCountAfter(), t.reason());
            if (t.accepted()) {
                totalWeight += t.edge().kruskalWeight();
                accepted++;
            }
        }
        System.out.println();
        System.out.println("Edges processed: " + trace.size()
                + " | accepted (MST edges): " + accepted
                + " | rejected (cycles): " + (trace.size() - accepted));
        System.out.println("Final component count: " + ds.componentCount()
                + " (1 means every location in this CSV is connected into a single network)");
        System.out.println("Total accepted-edge weight: " + String.format("%.3f", totalWeight));
    }

    private static void exportCsv(List<TraceStep> trace, Path outputCsv) throws IOException {
        try (PrintWriter w = new PrintWriter(new FileWriter(outputCsv.toFile()))) {
            w.println("step,from,to,weight,decision,componentCountAfter,mstEdgeCountAfter,reason");
            for (TraceStep t : trace) {
                w.printf("%d,%s,%s,%.4f,%s,%d,%d,\"%s\"%n",
                        t.step(), t.edge().from(), t.edge().to(), t.edge().kruskalWeight(),
                        t.accepted() ? "ACCEPT" : "REJECT",
                        t.componentCountAfter(), t.mstEdgeCountAfter(), t.reason());
            }
        }
    }

    /**
     * Loads edges from either the official db/seed format
     * (fromLocationId,toLocationId,distance,travelTime,roadConditionWeight)
     * or the raw data/roads.csv format
     * (road_id,from_location_id,to_location_id,distance_km,travel_time_min,condition_weight),
     * detected from the header row.
     */
    static List<RoadEdge> loadEdges(Path csvPath) throws IOException {
        List<RoadEdge> edges = new ArrayList<>();
        try (BufferedReader r = Files.newBufferedReader(csvPath)) {
            String header = r.readLine();
            if (header == null) {
                throw new IOException("Empty CSV: " + csvPath);
            }
            List<String> cols = Arrays.asList(header.split(","));
            boolean hasRoadId = cols.get(0).trim().equalsIgnoreCase("road_id");
            int fromIdx = hasRoadId ? 1 : 0;
            int toIdx = hasRoadId ? 2 : 1;
            int distIdx = hasRoadId ? 3 : 2;
            int timeIdx = hasRoadId ? 4 : 3;
            int condIdx = hasRoadId ? 5 : 4;

            String line;
            int lineNo = 1;
            while ((line = r.readLine()) != null) {
                lineNo++;
                if (line.isBlank()) continue;
                String[] parts = line.split(",");
                if (parts.length <= condIdx) {
                    throw new IOException("Malformed row at line " + lineNo + " in " + csvPath + ": " + line);
                }
                edges.add(new RoadEdge(
                        parts[fromIdx].trim(),
                        parts[toIdx].trim(),
                        Double.parseDouble(parts[distIdx].trim()),
                        Double.parseDouble(parts[timeIdx].trim()),
                        Double.parseDouble(parts[condIdx].trim())));
            }
        }
        return edges;
    }
}
