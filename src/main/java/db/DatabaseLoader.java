package db;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DatabaseLoader {

    public static class LoadResult {
        public final String tableName;
        public int successCount = 0;
        public final List<String> failures = new ArrayList<>();

        public LoadResult(String tableName) {
            this.tableName = tableName;
        }

        public String summary() {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("[%s] loaded=%d failed=%d%n", tableName, successCount, failures.size()));
            for (String f : failures) {
                sb.append("  ").append(f).append(System.lineSeparator());
            }
            return sb.toString();
        }
    }

    private static class ValidationException extends Exception {
        ValidationException(String message) {
            super(message);
        }
    }

    private static final DateTimeFormatter ISO_MIN = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private final Connection connection;

    public DatabaseLoader(Connection connection) {
        this.connection = connection;
    }

    public static Connection connect(String jdbcUrl) throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC driver not found on classpath", e);
        }
        Connection c = DriverManager.getConnection(jdbcUrl);
        try (Statement s = c.createStatement()) {
            s.execute("PRAGMA foreign_keys = ON;");
        }
        return c;
    }

    public void initSchema(Path schemaFile) throws IOException, SQLException {
        String sql = Files.readString(schemaFile);
        String[] statements = sql.split(";");
        try (Statement s = connection.createStatement()) {
            for (String raw : statements) {
                String stmt = stripComments(raw).trim();
                if (!stmt.isEmpty()) {
                    s.execute(stmt);
                }
            }
        }
    }

    private String stripComments(String block) {
        StringBuilder out = new StringBuilder();
        for (String line : block.split("\n")) {
            String trimmed = line.trim();
            if (trimmed.startsWith("--") || trimmed.isEmpty()) continue;
            out.append(line).append("\n");
        }
        return out.toString();
    }

    public LoadResult loadLocations(Path csvFile) throws IOException, SQLException {
        LoadResult result = new LoadResult("locations");
        String sql = "INSERT INTO locations (location_id, name, area, location_type, x_coord, y_coord) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        Set<String> seenIds = new HashSet<>();
        try (BufferedReader reader = Files.newBufferedReader(csvFile);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            connection.setAutoCommit(false);
            reader.readLine();
            String line;
            int lineNo = 1;
            while ((line = reader.readLine()) != null) {
                lineNo++;
                if (line.isBlank()) continue;
                try {
                    String[] f = splitCsv(line, 6);
                    String id = requireNonBlank(f[0], "location_id");
                    if (!seenIds.add(id)) {
                        throw new ValidationException("duplicate location_id in file: " + id);
                    }
                    String name = requireNonBlank(f[1], "name");
                    String area = requireNonBlank(f[2], "area");
                    String type = requireNonBlank(f[3], "location_type");
                    double x = parseDouble(f[4], "x_coord");
                    double y = parseDouble(f[5], "y_coord");

                    ps.setString(1, id);
                    ps.setString(2, name);
                    ps.setString(3, area);
                    ps.setString(4, type);
                    ps.setDouble(5, x);
                    ps.setDouble(6, y);
                    ps.executeUpdate();
                    result.successCount++;
                } catch (ValidationException | SQLException | NumberFormatException e) {
                    result.failures.add("line " + lineNo + ": " + e.getMessage() + " -- " + line);
                }
            }
            connection.commit();
            connection.setAutoCommit(true);
        }
        return result;
    }

    public LoadResult loadRoads(Path csvFile) throws IOException, SQLException {
        LoadResult result = new LoadResult("roads");
        String sql = "INSERT INTO roads (road_id, from_location_id, to_location_id, distance_km, " +
                "travel_time_min, road_condition_weight) VALUES (?, ?, ?, ?, ?, ?)";

        Set<String> seenIds = new HashSet<>();
        Set<String> knownLocations = loadKnownLocationIds();

        try (BufferedReader reader = Files.newBufferedReader(csvFile);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            connection.setAutoCommit(false);
            reader.readLine();
            String line;
            int lineNo = 1;
            while ((line = reader.readLine()) != null) {
                lineNo++;
                if (line.isBlank()) continue;
                try {
                    String[] f = splitCsv(line, 6);
                    String id = requireNonBlank(f[0], "road_id");
                    if (!seenIds.add(id)) {
                        throw new ValidationException("duplicate road_id in file: " + id);
                    }
                    String from = requireNonBlank(f[1], "from_location_id");
                    String to = requireNonBlank(f[2], "to_location_id");
                    if (!knownLocations.contains(from)) {
                        throw new ValidationException("from_location_id not found in locations: " + from);
                    }
                    if (!knownLocations.contains(to)) {
                        throw new ValidationException("to_location_id not found in locations: " + to);
                    }
                    double distance = parseDouble(f[3], "distance_km");
                    if (distance < 0) throw new ValidationException("distance_km must be non-negative: " + distance);
                    double time = parseDouble(f[4], "travel_time_min");
                    if (time <= 0) throw new ValidationException("travel_time_min must be positive: " + time);
                    double weight = parseDouble(f[5], "road_condition_weight");
                    if (weight <= 0) throw new ValidationException("road_condition_weight must be positive: " + weight);

                    ps.setString(1, id);
                    ps.setString(2, from);
                    ps.setString(3, to);
                    ps.setDouble(4, distance);
                    ps.setDouble(5, time);
                    ps.setDouble(6, weight);
                    ps.executeUpdate();
                    result.successCount++;
                } catch (ValidationException | SQLException | NumberFormatException e) {
                    result.failures.add("line " + lineNo + ": " + e.getMessage() + " -- " + line);
                }
            }
            connection.commit();
            connection.setAutoCommit(true);
        }
        return result;
    }

    public LoadResult loadServiceRequests(Path csvFile) throws IOException, SQLException {
        LoadResult result = new LoadResult("service_requests");
        String sql = "INSERT INTO service_requests (request_id, source_location_id, destination_location_id, " +
                "category, urgency, time_submitted, deadline, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        Set<String> validCategories = Set.of("Medical", "Security", "Utility", "Maintenance", "IT Support",
                "Document", "Lab Equipment", "Library", "Catering", "Cleaning", "Event Setup", "Transport");
        Set<String> validStatuses = Set.of("NEW", "ASSIGNED", "IN_PROGRESS", "COMPLETED", "CANCELLED");

        Set<String> seenIds = new HashSet<>();
        Set<String> knownLocations = loadKnownLocationIds();

        try (BufferedReader reader = Files.newBufferedReader(csvFile);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            connection.setAutoCommit(false);
            reader.readLine();
            String line;
            int lineNo = 1;
            while ((line = reader.readLine()) != null) {
                lineNo++;
                if (line.isBlank()) continue;
                try {
                    String[] f = splitCsv(line, 8);
                    String id = requireNonBlank(f[0], "request_id");
                    if (!seenIds.add(id)) {
                        throw new ValidationException("duplicate request_id in file: " + id);
                    }
                    String source = requireNonBlank(f[1], "source_location_id");
                    String destination = requireNonBlank(f[2], "destination_location_id");
                    if (!knownLocations.isEmpty()) {
                        if (!knownLocations.contains(source)) {
                            throw new ValidationException("source_location_id not found in locations: " + source);
                        }
                        if (!knownLocations.contains(destination)) {
                            throw new ValidationException("destination_location_id not found in locations: " + destination);
                        }
                    }
                    if (source.equals(destination)) {
                        throw new ValidationException("source_location_id equals destination_location_id: " + source);
                    }
                    String category = requireNonBlank(f[3], "category");
                    if (!validCategories.contains(category)) {
                        throw new ValidationException("invalid category: " + category);
                    }
                    int urgency = parseInt(f[4], "urgency");
                    if (urgency < 1 || urgency > 5) {
                        throw new ValidationException("urgency out of range 1-5: " + urgency);
                    }
                    LocalDateTime submitted = parseTimestamp(f[5], "time_submitted");
                    LocalDateTime deadline = parseTimestamp(f[6], "deadline");
                    if (!deadline.isAfter(submitted)) {
                        throw new ValidationException("deadline must be after time_submitted");
                    }
                    String status = requireNonBlank(f[7], "status");
                    if (!validStatuses.contains(status)) {
                        throw new ValidationException("invalid status: " + status);
                    }

                    ps.setString(1, id);
                    ps.setString(2, source);
                    ps.setString(3, destination);
                    ps.setString(4, category);
                    ps.setInt(5, urgency);
                    ps.setString(6, f[5]);
                    ps.setString(7, f[6]);
                    ps.setString(8, status);
                    ps.executeUpdate();
                    result.successCount++;
                } catch (ValidationException | SQLException | NumberFormatException | DateTimeParseException e) {
                    result.failures.add("line " + lineNo + ": " + e.getMessage() + " -- " + line);
                }
            }
            connection.commit();
            connection.setAutoCommit(true);
        }
        return result;
    }

    public LoadResult loadResources(Path csvFile) throws IOException, SQLException {
        LoadResult result = new LoadResult("resources");
        String sql = "INSERT INTO resources (resource_id, resource_type, home_location_id, capacity, availability_status) " +
                "VALUES (?, ?, ?, ?, ?)";

        Set<String> validStatuses = Set.of("AVAILABLE", "BUSY", "MAINTENANCE", "UNAVAILABLE");
        Set<String> seenIds = new HashSet<>();
        Set<String> knownLocations = loadKnownLocationIds();

        try (BufferedReader reader = Files.newBufferedReader(csvFile);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            connection.setAutoCommit(false);
            reader.readLine();
            String line;
            int lineNo = 1;
            while ((line = reader.readLine()) != null) {
                lineNo++;
                if (line.isBlank()) continue;
                try {
                    String[] f = splitCsv(line, 5);
                    String id = requireNonBlank(f[0], "resource_id");
                    if (!seenIds.add(id)) {
                        throw new ValidationException("duplicate resource_id in file: " + id);
                    }
                    String type = requireNonBlank(f[1], "resource_type");
                    String home = requireNonBlank(f[2], "home_location_id");
                    if (!knownLocations.isEmpty() && !knownLocations.contains(home)) {
                        throw new ValidationException("home_location_id not found in locations: " + home);
                    }
                    double capacity = parseDouble(f[3], "capacity");
                    if (capacity <= 0) throw new ValidationException("capacity must be positive: " + capacity);
                    String status = requireNonBlank(f[4], "availability_status");
                    if (!validStatuses.contains(status)) {
                        throw new ValidationException("invalid availability_status: " + status);
                    }

                    ps.setString(1, id);
                    ps.setString(2, type);
                    ps.setString(3, home);
                    ps.setDouble(4, capacity);
                    ps.setString(5, status);
                    ps.executeUpdate();
                    result.successCount++;
                } catch (ValidationException | SQLException | NumberFormatException e) {
                    result.failures.add("line " + lineNo + ": " + e.getMessage() + " -- " + line);
                }
            }
            connection.commit();
            connection.setAutoCommit(true);
        }
        return result;
    }

    private Set<String> loadKnownLocationIds() throws SQLException {
        Set<String> ids = new HashSet<>();
        try (Statement stmt = connection.createStatement();
             var rs = stmt.executeQuery("SELECT location_id FROM locations")) {
            while (rs.next()) {
                ids.add(rs.getString(1));
            }
        }
        return ids;
    }

    private String[] splitCsv(String line, int expectedFields) throws ValidationException {
        String[] parts = line.split(",", -1);
        if (parts.length != expectedFields) {
            throw new ValidationException("expected " + expectedFields + " fields, found " + parts.length);
        }
        return parts;
    }

    private String requireNonBlank(String value, String fieldName) throws ValidationException {
        if (value == null || value.isBlank()) {
            throw new ValidationException("missing required field: " + fieldName);
        }
        return value.trim();
    }

    private double parseDouble(String value, String fieldName) throws ValidationException {
        try {
            return Double.parseDouble(value.trim());
        } catch (Exception e) {
            throw new ValidationException("invalid numeric value for " + fieldName + ": " + value);
        }
    }

    private int parseInt(String value, String fieldName) throws ValidationException {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            throw new ValidationException("invalid integer value for " + fieldName + ": " + value);
        }
    }

    private LocalDateTime parseTimestamp(String value, String fieldName) throws ValidationException {
        try {
            return LocalDateTime.parse(value.trim(), ISO_MIN);
        } catch (DateTimeParseException e) {
            throw new ValidationException("malformed timestamp for " + fieldName + ": " + value);
        }
    }

    public static void main(String[] args) throws Exception {
        Path dbFile = Path.of("aegis_demo.db");
        Files.deleteIfExists(dbFile);

        try (Connection conn = connect("jdbc:sqlite:" + dbFile)) {
            DatabaseLoader loader = new DatabaseLoader(conn);

            loader.initSchema(Path.of("db/schema.sql"));
            System.out.println("Schema created: locations, roads, service_requests, resources, algorithm_runs, audit_events");

            System.out.println("\n--- Loading locations ---");
            System.out.print(loader.loadLocations(Path.of("db/seed/locations.csv")).summary());

            System.out.println("\n--- Loading roads ---");
            System.out.print(loader.loadRoads(Path.of("db/seed/roads.csv")).summary());

            System.out.println("\n--- Loading service_requests ---");
            System.out.print(loader.loadServiceRequests(Path.of("db/seed/service_requests.csv")).summary());

            System.out.println("\n--- Loading resources ---");
            System.out.print(loader.loadResources(Path.of("db/seed/resources.csv")).summary());

            System.out.println("\n--- Evidence ---");
            printCount(conn, "locations");
            printCount(conn, "roads");
            printCount(conn, "service_requests");
            printCount(conn, "resources");
        }
    }

    private static void printCount(Connection conn, String table) throws SQLException {
        try (Statement s = conn.createStatement();
             var rs = s.executeQuery("SELECT COUNT(*) FROM " + table)) {
            rs.next();
            System.out.println(table + " = " + rs.getInt(1));
        }
    }
}