-- Owner: Hannah Aidoo (confirm if this is "Nana Aba")
-- Ghana Smart Service Operations Optimizer - Waste/Sanitation Routing
-- Minimum record counts per brief: locations 50, roads 100,
-- service_requests 300, resources 30, algorithm_runs 30

CREATE TABLE locations (
    locationId   INTEGER PRIMARY KEY,
    name         TEXT NOT NULL,
    area         TEXT,
    type         TEXT,
    latitude     REAL,
    longitude    REAL
);

CREATE TABLE roads (
    fromLocationId      INTEGER NOT NULL,
    toLocationId        INTEGER NOT NULL,
    distance             REAL,
    travelTime           REAL,
    roadConditionWeight  REAL,
    FOREIGN KEY (fromLocationId) REFERENCES locations(locationId),
    FOREIGN KEY (toLocationId) REFERENCES locations(locationId)
);

CREATE TABLE service_requests (
    requestId     INTEGER PRIMARY KEY,
    source        INTEGER,
    destination   INTEGER,
    category      TEXT,
    urgency       TEXT,
    timeSubmitted TEXT,
    deadline      TEXT,
    status        TEXT,
    FOREIGN KEY (source) REFERENCES locations(locationId),
    FOREIGN KEY (destination) REFERENCES locations(locationId)
);

CREATE TABLE resources (
    resourceId         INTEGER PRIMARY KEY,
    type               TEXT,
    homeLocation       INTEGER,
    capacity           INTEGER,
    availabilityStatus TEXT,
    FOREIGN KEY (homeLocation) REFERENCES locations(locationId)
);

CREATE TABLE algorithm_runs (
    runId         INTEGER PRIMARY KEY,
    algorithmName TEXT,
    inputSize     INTEGER,
    timeNs        INTEGER,
    memoryKb      INTEGER,
    dateRun       TEXT
);

CREATE TABLE audit_events (
    eventId     INTEGER PRIMARY KEY,
    eventType   TEXT,
    description TEXT,
    createdAt   TEXT
);
