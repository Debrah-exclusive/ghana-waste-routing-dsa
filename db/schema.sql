DROP TABLE IF EXISTS audit_events;
DROP TABLE IF EXISTS algorithm_runs;
DROP TABLE IF EXISTS resources;
DROP TABLE IF EXISTS service_requests;
DROP TABLE IF EXISTS roads;
DROP TABLE IF EXISTS locations;

CREATE TABLE locations (
    location_id     TEXT     PRIMARY KEY,
    name            TEXT     NOT NULL,
    area            TEXT     NOT NULL,
    location_type   TEXT     NOT NULL,
    x_coord         REAL     NOT NULL,
    y_coord         REAL     NOT NULL
);

CREATE TABLE roads (
    road_id                 TEXT     PRIMARY KEY,
    from_location_id        TEXT     NOT NULL,
    to_location_id          TEXT     NOT NULL,
    distance_km             REAL     NOT NULL,
    travel_time_min         REAL     NOT NULL,
    road_condition_weight   REAL     NOT NULL,

    CONSTRAINT fk_roads_from FOREIGN KEY (from_location_id) REFERENCES locations(location_id),
    CONSTRAINT fk_roads_to   FOREIGN KEY (to_location_id)   REFERENCES locations(location_id),

    CONSTRAINT chk_road_distance   CHECK (distance_km >= 0),
    CONSTRAINT chk_road_time       CHECK (travel_time_min > 0),
    CONSTRAINT chk_road_weight     CHECK (road_condition_weight > 0)
);

CREATE TABLE service_requests (
    request_id              TEXT     PRIMARY KEY,
    source_location_id      TEXT     NOT NULL,
    destination_location_id TEXT     NOT NULL,
    category                TEXT     NOT NULL,
    urgency                 INTEGER  NOT NULL,
    time_submitted           TEXT     NOT NULL,
    deadline                TEXT     NOT NULL,
    status                  TEXT     NOT NULL,

    CONSTRAINT fk_source
        FOREIGN KEY (source_location_id)      REFERENCES locations(location_id),
    CONSTRAINT fk_destination
        FOREIGN KEY (destination_location_id) REFERENCES locations(location_id),

    CONSTRAINT chk_urgency   CHECK (urgency BETWEEN 1 AND 5),
    CONSTRAINT chk_endpoints CHECK (source_location_id <> destination_location_id),
    CONSTRAINT chk_deadline  CHECK (deadline > time_submitted),
    CONSTRAINT chk_status    CHECK (status IN
        ('NEW', 'ASSIGNED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
    CONSTRAINT chk_category  CHECK (category IN
        ('Medical', 'Security', 'Utility', 'Maintenance', 'IT Support',
         'Document', 'Lab Equipment', 'Library', 'Catering', 'Cleaning',
         'Event Setup', 'Transport'))
);

CREATE INDEX idx_sr_status         ON service_requests (status);
CREATE INDEX idx_sr_urgency_time   ON service_requests (urgency DESC, time_submitted ASC);
CREATE INDEX idx_sr_source         ON service_requests (source_location_id);
CREATE INDEX idx_sr_destination    ON service_requests (destination_location_id);
CREATE INDEX idx_sr_category       ON service_requests (category);
CREATE INDEX idx_sr_deadline       ON service_requests (deadline);

CREATE TABLE resources (
    resource_id          TEXT     PRIMARY KEY,
    resource_type        TEXT     NOT NULL,
    home_location_id     TEXT     NOT NULL,
    capacity             REAL     NOT NULL,
    availability_status  TEXT     NOT NULL,

    CONSTRAINT fk_resource_home FOREIGN KEY (home_location_id) REFERENCES locations(location_id),
    CONSTRAINT chk_capacity     CHECK (capacity > 0),
    CONSTRAINT chk_availability CHECK (availability_status IN
        ('AVAILABLE', 'BUSY', 'MAINTENANCE', 'UNAVAILABLE'))
);

CREATE TABLE algorithm_runs (
    run_id           INTEGER  PRIMARY KEY AUTOINCREMENT,
    algorithm_name   TEXT     NOT NULL,
    input_size       INTEGER  NOT NULL,
    time_ns          INTEGER  NOT NULL,
    memory_kb        REAL,
    date_run         TEXT     NOT NULL,

    CONSTRAINT chk_run_input_size CHECK (input_size >= 0),
    CONSTRAINT chk_run_time       CHECK (time_ns >= 0)
);

CREATE TABLE audit_events (
    event_id     INTEGER  PRIMARY KEY AUTOINCREMENT,
    event_type   TEXT     NOT NULL,
    description  TEXT,
    entity_type  TEXT     NOT NULL,
    entity_id    TEXT,
    created_at   TEXT     NOT NULL DEFAULT (datetime('now'))
);