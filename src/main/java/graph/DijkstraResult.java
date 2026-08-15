package graph;

import java.util.*;

public class DijkstraResult {

    private final String source;
    private final Map<String, Double> distances;
    private final Map<String, String> predecessors;

    public DijkstraResult(String source, Map<String, Double> distances,
                           Map<String, String> predecessors) {
        this.source = source;
        this.distances = distances;
        this.predecessors = predecessors;
    }

    public String getSource() { return source; }
    public Map<String, Double> getDistances() { return Collections.unmodifiableMap(distances); }
    public Map<String, String> getPredecessors() { return Collections.unmodifiableMap(predecessors); }

    public double distanceTo(String target) {
        return distances.getOrDefault(target, Double.POSITIVE_INFINITY);
    }

    public boolean isReachable(String target) {
        return distances.containsKey(target) && distances.get(target) != Double.POSITIVE_INFINITY;
    }

    public List<String> pathTo(String target) {
        if (!distances.containsKey(target) || distances.get(target) == Double.POSITIVE_INFINITY) {
            return Collections.emptyList();
        }
        LinkedList<String> path = new LinkedList<>();
        String current = target;
        while (current != null) {
            path.addFirst(current);
            if (current.equals(source)) break;
            current = predecessors.get(current);
        }
        if (!path.getFirst().equals(source)) {
            return Collections.emptyList();
        }
        return path;
    }

    public void printDistanceTable() {
        System.out.printf("%-10s %-12s %-12s%n", "Vertex", "Distance", "Predecessor");
        distances.keySet().stream().sorted().forEach(v -> {
            double d = distances.get(v);
            String dist = (d == Double.POSITIVE_INFINITY) ? "INF" : String.format("%.3f", d);
            String pred = predecessors.getOrDefault(v, "-");
            if (pred == null) pred = "-";
            System.out.printf("%-10s %-12s %-12s%n", v, dist, pred);
        });
    }
}
