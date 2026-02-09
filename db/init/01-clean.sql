TRUNCATE TABLE
    categories,
    users,
    events,
    compilations,
    participation_requests,
    compilation_events
RESTART IDENTITY CASCADE;

CREATE TABLE IF NOT EXISTS compilation_events (
    compilation_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    PRIMARY KEY (compilation_id, event_id),
    FOREIGN KEY (compilation_id) REFERENCES compilations(id),
    FOREIGN KEY (event_id) REFERENCES events(id)
);
