package graph;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class CampusDataLoader {

    public enum WeightMetric {
        DISTANCE_KM,
        TRAVEL_TIME_MIN,
        CONDITION_WEIGHTED_DISTANCE
    }

    public static final class LocationInfo {
        public final String id;
        public final String name;
        public final String area;
        public final String type;
        public final double x;
        public final double y;

        public LocationInfo(String id, String name, String area, String type, double x, double y) {
            this.id = id; this.name = name; this.area = area; this.type = type;
            this.x = x; this.y = y;
        }
    }

    private final Map<String, LocationInfo> locations = new LinkedHashMap<>();

    public Map<String, LocationInfo> getLocations() {
        return Collections.unmodifiableMap(locations);
    }

    public String nameOf(String id) {
        LocationInfo info = locations.get(id);
        return info != null ? info.name : id;
    }

    public Graph load(Path locationsCsv, Path roadsCsv, WeightMetric metric) throws IOException {
        Graph graph = new Graph(false);

        try (BufferedReader br = Files.newBufferedReader(locationsCsv)) {
            String header = br.readLine();
            requireHeader(header, "locations.csv");
            String line;
            int lineNo = 1;
            while ((line = br.readLine()) != null) {
                lineNo++;
                if (line.isBlank()) continue;
                String[] cols = splitCsvLine(line);
                if (cols.length < 6) {
                    throw new IllegalArgumentException(
                        "locations.csv line " + lineNo + ": expected 6 columns, got " + cols.length);
                }
                String id = cols[0].trim();
                LocationInfo info = new LocationInfo(
                    id, cols[1].trim(), cols[2].trim(), cols[3].trim(),
                    parseDouble(cols[4], "x_coord", lineNo),
                    parseDouble(cols[5], "y_coord", lineNo));
                locations.put(id, info);
                graph.addVertex(id);
            }
        }

        try (BufferedReader br = Files.newBufferedReader(roadsCsv)) {
            String header = br.readLine();
            requireHeader(header, "roads.csv");
            String line;
            int lineNo = 1;
            while ((line = br.readLine()) != null) {
                lineNo++;
                if (line.isBlank()) continue;
                String[] cols = splitCsvLine(line);
                if (cols.length < 6) {
                    throw new IllegalArgumentException(
                        "roads.csv line " + lineNo + ": expected 6 columns, got " + cols.length);
                }
                String roadId = cols[0].trim();
                String from = cols[1].trim();
                String to = cols[2].trim();
                double distanceKm = parseDouble(cols[3], "distance_km", lineNo);
                double travelTimeMin = parseDouble(cols[4], "travel_time_min", lineNo);
                double conditionWeight = parseDouble(cols[5], "condition_weight", lineNo);

                if (!graph.hasVertex(from) || !graph.hasVertex(to)) {
                    throw new IllegalArgumentException(
                        "roads.csv line " + lineNo + ": road " + roadId +
                        " references unknown location(s) " + from + "/" + to);
                }

                double weight = switch (metric) {
                    case DISTANCE_KM -> distanceKm;
                    case TRAVEL_TIME_MIN -> travelTimeMin;
                    case CONDITION_WEIGHTED_DISTANCE -> distanceKm * conditionWeight;
                };

                graph.addEdge(from, to, weight, roadId);
            }
        }

        return graph;
    }

    private static void requireHeader(String header, String fileName) {
        if (header == null || header.isBlank()) {
            throw new IllegalArgumentException(fileName + " is empty or missing a header row");
        }
    }

    private static double parseDouble(String raw, String field, int lineNo) {
        try {
            return Double.parseDouble(raw.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                "Invalid numeric value for '" + field + "' on line " + lineNo + ": '" + raw + "'");
        }
    }

    private static String[] splitCsvLine(String line) {
        return line.split(",", -1);
    }
}
