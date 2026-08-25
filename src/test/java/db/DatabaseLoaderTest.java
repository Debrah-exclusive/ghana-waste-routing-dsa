import db.DatabaseLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseLoaderTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) throws Exception {
        System.out.println("=== DatabaseLoader tests ===");

        System.out.println("\n-- normal cases --");
        validLocationsCsvLoadsSuccessfully();
        validRoadsCsvLoadsSuccessfully();
        validServiceRequestCsvLoadsSuccessfully();
        validResourceCsvLoadsSuccessfully();

        System.out.println("\n-- boundary cases --");
        emptyCsvLoadsZeroRowsWithoutError();
        singleRecordCsvLoadsExactlyOneRow();
        duplicatePrimaryKeyIsRejected();
        boundaryUrgencyValuesAccepted();
        boundaryStatusValuesAccepted();
        zeroDistanceRoadAccepted();

        System.out.println("\n-- invalid cases --");
        missingRequiredFieldIsRejected();
        nonexistentForeignKeyIsRejected();
        negativeRoadDistanceIsRejected();
        nonPositiveResourceCapacityIsRejected();
        malformedTimestampIsRejected();
        invalidCategoryEnumIsRejected();
        invalidStatusEnumIsRejected();
        sourceEqualsDestinationIsRejected();
        deadlineBeforeSubmittedIsRejected();

        System.out.println("\n============================================");
        System.out.printf("RESULTS: %d passed, %d failed%n", passed, failed);
        System.out.println("============================================");
        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void check(String name, boolean condition, String detail) {
        if (condition) {
            passed++;
            System.out.println("[PASS] " + name);
        } else {
            failed++;
            System.out.println("[FAIL] " + name + " -- " + detail);
        }
    }

    private static Path tmpCsv(String name, String content) throws Exception {
        Path p = Files.createTempFile(name, ".csv");
        Files.writeString(p, content);
        p.toFile().deleteOnExit();
        return p;
    }

    private static Connection freshDb() throws Exception {
        Connection conn = DatabaseLoader.connect("jdbc:sqlite:file:test" + System.nanoTime() + "?mode=memory&cache=shared");
        DatabaseLoader loader = new DatabaseLoader(conn);
        loader.initSchema(Path.of("db/schema.sql"));
        try (Statement s = conn.createStatement()) {
            s.execute("INSERT INTO locations (location_id, name, area, location_type, x_coord, y_coord) " +
                    "VALUES ('L001','Test A','Area 1','Hall',0.1,0.1)," +
                    "('L002','Test B','Area 1','Hall',0.2,0.2)," +
                    "('L003','Test C','Area 2','Hall',0.3,0.3)");
        }
        return conn;
    }

    private static void validLocationsCsvLoadsSuccessfully() throws Exception {
        try (Connection conn = freshDb()) {
            Path csv = tmpCsv("locs", """
                    location_id,name,area,location_type,x_coord,y_coord
                    L010,New Hall,Area 3,Hall,0.4,0.4
                    L011,New Lab,Area 3,Lab,0.5,0.5
                    """);
            DatabaseLoader.LoadResult r = new DatabaseLoader(conn).loadLocations(csv);
            check("valid locations CSV loads successfully", r.successCount == 2 && r.failures.isEmpty(), r.summary());
        }
    }

    private static void validRoadsCsvLoadsSuccessfully() throws Exception {
        try (Connection conn = freshDb()) {
            Path csv = tmpCsv("roads", """
                    road_id,from_location_id,to_location_id,distance_km,travel_time_min,road_condition_weight
                    R001,L001,L002,1.2,5.0,1.0
                    """);
            DatabaseLoader.LoadResult r = new DatabaseLoader(conn).loadRoads(csv);
            check("valid road CSV loads successfully", r.successCount == 1 && r.failures.isEmpty(), r.summary());
        }
    }

    private static void validServiceRequestCsvLoadsSuccessfully() throws Exception {
        try (Connection conn = freshDb()) {
            Path csv = tmpCsv("sr", """
                    request_id,source_location_id,destination_location_id,category,urgency,time_submitted,deadline,status
                    Q001,L001,L002,Medical,5,2026-07-01T08:15,2026-07-01T09:00,COMPLETED
                    """);
            DatabaseLoader.LoadResult r = new DatabaseLoader(conn).loadServiceRequests(csv);
            check("valid service request loads successfully", r.successCount == 1 && r.failures.isEmpty(), r.summary());
        }
    }

    private static void validResourceCsvLoadsSuccessfully() throws Exception {
        try (Connection conn = freshDb()) {
            Path csv = tmpCsv("res", """
                    resource_id,resource_type,home_location_id,capacity,availability_status
                    RS01,Compactor Truck,L001,10,AVAILABLE
                    """);
            DatabaseLoader.LoadResult r = new DatabaseLoader(conn).loadResources(csv);
            check("valid resource CSV loads successfully", r.successCount == 1 && r.failures.isEmpty(), r.summary());
        }
    }

    private static void emptyCsvLoadsZeroRowsWithoutError() throws Exception {
        try (Connection conn = freshDb()) {
            Path csv = tmpCsv("empty", "request_id,source_location_id,destination_location_id,category,urgency,time_submitted,deadline,status\n");
            DatabaseLoader.LoadResult r = new DatabaseLoader(conn).loadServiceRequests(csv);
            check("empty CSV (header only) loads 0 rows without error", r.successCount == 0 && r.failures.isEmpty(), r.summary());
        }
    }

    private static void singleRecordCsvLoadsExactlyOneRow() throws Exception {
        try (Connection conn = freshDb()) {
            Path csv = tmpCsv("single", """
                    resource_id,resource_type,home_location_id,capacity,availability_status
                    RS01,Waste Container,L001,5,AVAILABLE
                    """);
            DatabaseLoader.LoadResult r = new DatabaseLoader(conn).loadResources(csv);
            check("single-record CSV loads exactly 1 row", r.successCount == 1, r.summary());
        }
    }

    private static void duplicatePrimaryKeyIsRejected() throws Exception {
        try (Connection conn = freshDb()) {
            Path csv = tmpCsv("dup", """
                    request_id,source_location_id,destination_location_id,category,urgency,time_submitted,deadline,status
                    Q001,L001,L002,Medical,5,2026-07-01T08:15,2026-07-01T09:00,COMPLETED
                    Q001,L001,L003,Document,2,2026-07-01T08:20,2026-07-01T11:00,NEW
                    """);
            DatabaseLoader.LoadResult r = new DatabaseLoader(conn).loadServiceRequests(csv);
            check("duplicate request_id: first row loads, second rejected",
                    r.successCount == 1 && r.failures.size() == 1, r.summary());
        }
    }

    private static void boundaryUrgencyValuesAccepted() throws Exception {
        try (Connection conn = freshDb()) {
            Path csv = tmpCsv("urg", """
                    request_id,source_location_id,destination_location_id,category,urgency,time_submitted,deadline,status
                    Q001,L001,L002,Medical,1,2026-07-01T08:15,2026-07-01T09:00,NEW
                    Q002,L001,L002,Medical,5,2026-07-01T08:15,2026-07-01T09:00,NEW
                    """);
            DatabaseLoader.LoadResult r = new DatabaseLoader(conn).loadServiceRequests(csv);
            check("boundary urgency values 1 and 5 both accepted", r.successCount == 2 && r.failures.isEmpty(), r.summary());
        }
    }

    private static void boundaryStatusValuesAccepted() throws Exception {
        try (Connection conn = freshDb()) {
            Path csv = tmpCsv("status", """
                    request_id,source_location_id,destination_location_id,category,urgency,time_submitted,deadline,status
                    Q001,L001,L002,Medical,3,2026-07-01T08:15,2026-07-01T09:00,NEW
                    Q002,L001,L002,Medical,3,2026-07-01T08:15,2026-07-01T09:00,CANCELLED
                    """);
            DatabaseLoader.LoadResult r = new DatabaseLoader(conn).loadServiceRequests(csv);
            check("boundary status values NEW and CANCELLED both accepted",
                    r.successCount == 2 && r.failures.isEmpty(), r.summary());
        }
    }

    private static void zeroDistanceRoadAccepted() throws Exception {
        try (Connection conn = freshDb()) {
            Path csv = tmpCsv("zerodist", """
                    road_id,from_location_id,to_location_id,distance_km,travel_time_min,road_condition_weight
                    R001,L001,L002,0,1,1.1
                    """);
            DatabaseLoader.LoadResult r = new DatabaseLoader(conn).loadRoads(csv);
            check("road with distance_km=0 (duplicate-coordinate pair) is accepted",
                    r.successCount == 1 && r.failures.isEmpty(), r.summary());
        }
    }

    private static void missingRequiredFieldIsRejected() throws Exception {
        try (Connection conn = freshDb()) {
            Path csv = tmpCsv("missing", """
                    resource_id,resource_type,home_location_id,capacity,availability_status
                    RS01,,L001,5,AVAILABLE
                    """);
            DatabaseLoader.LoadResult r = new DatabaseLoader(conn).loadResources(csv);
            check("resource with missing resource_type field is rejected", r.failures.size() == 1, r.summary());
        }
    }

    private static void nonexistentForeignKeyIsRejected() throws Exception {
        try (Connection conn = freshDb()) {
            Path csv = tmpCsv("badfk", """
                    request_id,source_location_id,destination_location_id,category,urgency,time_submitted,deadline,status
                    Q001,L999,L002,Medical,3,2026-07-01T08:15,2026-07-01T09:00,NEW
                    """);
            DatabaseLoader.LoadResult r = new DatabaseLoader(conn).loadServiceRequests(csv);
            check("service request referencing nonexistent location (L999) is rejected",
                    r.failures.size() == 1 && r.successCount == 0, r.summary());
        }
    }

    private static void negativeRoadDistanceIsRejected() throws Exception {
        try (Connection conn = freshDb()) {
            Path csv = tmpCsv("negdist", """
                    road_id,from_location_id,to_location_id,distance_km,travel_time_min,road_condition_weight
                    R001,L001,L002,-1.5,5.0,1.0
                    """);
            DatabaseLoader.LoadResult r = new DatabaseLoader(conn).loadRoads(csv);
            check("road with negative distance_km is rejected", r.failures.size() == 1, r.summary());
        }
    }

    private static void nonPositiveResourceCapacityIsRejected() throws Exception {
        try (Connection conn = freshDb()) {
            Path csv = tmpCsv("badcap", """
                    resource_id,resource_type,home_location_id,capacity,availability_status
                    RS01,Compactor Truck,L001,0,AVAILABLE
                    """);
            DatabaseLoader.LoadResult r = new DatabaseLoader(conn).loadResources(csv);
            check("resource with zero/non-positive capacity is rejected", r.failures.size() == 1, r.summary());
        }
    }

    private static void malformedTimestampIsRejected() throws Exception {
        try (Connection conn = freshDb()) {
            Path csv = tmpCsv("badts", """
                    request_id,source_location_id,destination_location_id,category,urgency,time_submitted,deadline,status
                    Q001,L001,L002,Medical,3,01-07-2026 08:15,2026-07-01T09:00,NEW
                    """);
            DatabaseLoader.LoadResult r = new DatabaseLoader(conn).loadServiceRequests(csv);
            check("malformed time_submitted (wrong format) is rejected", r.failures.size() == 1, r.summary());
        }
    }

    private static void invalidCategoryEnumIsRejected() throws Exception {
        try (Connection conn = freshDb()) {
            Path csv = tmpCsv("badcat", """
                    request_id,source_location_id,destination_location_id,category,urgency,time_submitted,deadline,status
                    Q001,L001,L002,Plumbing,3,2026-07-01T08:15,2026-07-01T09:00,NEW
                    """);
            DatabaseLoader.LoadResult r = new DatabaseLoader(conn).loadServiceRequests(csv);
            check("invalid category value ('Plumbing') is rejected", r.failures.size() == 1, r.summary());
        }
    }

    private static void invalidStatusEnumIsRejected() throws Exception {
        try (Connection conn = freshDb()) {
            Path csv = tmpCsv("badstatus", """
                    request_id,source_location_id,destination_location_id,category,urgency,time_submitted,deadline,status
                    Q001,L001,L002,Medical,3,2026-07-01T08:15,2026-07-01T09:00,DONE
                    """);
            DatabaseLoader.LoadResult r = new DatabaseLoader(conn).loadServiceRequests(csv);
            check("invalid status value ('DONE') is rejected", r.failures.size() == 1, r.summary());
        }
    }

    private static void sourceEqualsDestinationIsRejected() throws Exception {
        try (Connection conn = freshDb()) {
            Path csv = tmpCsv("sameend", """
                    request_id,source_location_id,destination_location_id,category,urgency,time_submitted,deadline,status
                    Q001,L001,L001,Medical,3,2026-07-01T08:15,2026-07-01T09:00,NEW
                    """);
            DatabaseLoader.LoadResult r = new DatabaseLoader(conn).loadServiceRequests(csv);
            check("source_location_id == destination_location_id is rejected", r.failures.size() == 1, r.summary());
        }
    }

    private static void deadlineBeforeSubmittedIsRejected() throws Exception {
        try (Connection conn = freshDb()) {
            Path csv = tmpCsv("baddeadline", """
                    request_id,source_location_id,destination_location_id,category,urgency,time_submitted,deadline,status
                    Q001,L001,L002,Medical,3,2026-07-01T09:00,2026-07-01T08:15,NEW
                    """);
            DatabaseLoader.LoadResult r = new DatabaseLoader(conn).loadServiceRequests(csv);
            check("deadline earlier than time_submitted is rejected", r.failures.size() == 1, r.summary());
        }
    }
}