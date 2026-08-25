import graph.*;
import graph.CampusDataLoader.WeightMetric;

import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        String sourceId = args.length > 0 ? args[0] : "L001";
        String targetId = args.length > 1 ? args[1] : "L045";
        WeightMetric metric = switch (args.length > 2 ? args[2] : "distance") {
            case "time" -> WeightMetric.TRAVEL_TIME_MIN;
            case "weighted" -> WeightMetric.CONDITION_WEIGHTED_DISTANCE;
            default -> WeightMetric.DISTANCE_KM;
        };

        CampusDataLoader loader = new CampusDataLoader();
        Graph graph = loader.load(
            Path.of("data/locations.csv"),
            Path.of("data/roads.csv"),
            metric);

        System.out.println("Loaded campus graph: " + graph.vertexCount() +
            " locations, " + graph.edgeCount() + " roads. Weight metric = " + metric);
        System.out.println();

        DijkstraResult result = Dijkstra.shortestPath(graph, sourceId, targetId);

        System.out.println("=== Distance table from " + sourceId +
            " (" + loader.nameOf(sourceId) + ") ===");
        result.printDistanceTable();

        System.out.println();
        System.out.println("=== Shortest route: " + sourceId + " -> " + targetId + " ===");
        if (!result.isReachable(targetId)) {
            System.out.println("No path exists between " + sourceId + " and " + targetId);
            return;
        }
        List<String> path = result.pathTo(targetId);
        System.out.println("Total cost: " + result.distanceTo(targetId) + " (" + metric + ")");
        System.out.println("Hops: " + (path.size() - 1));
        System.out.print("Path: ");
        for (int i = 0; i < path.size(); i++) {
            System.out.print(path.get(i) + " (" + loader.nameOf(path.get(i)) + ")");
            if (i < path.size() - 1) System.out.print("  ->  ");
        }
        System.out.println();
    }
}
