CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS events (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(120) NOT NULL,
    annotation VARCHAR(2000) NOT NULL,
    description VARCHAR(7000) NOT NULL,
    category_id BIGINT NOT NULL,
    initiator_id BIGINT NOT NULL,
    event_date TIMESTAMP NOT NULL,
    lat DOUBLE PRECISION,
    lon DOUBLE PRECISION,
    paid BOOLEAN NOT NULL,
    participant_limit INTEGER,
    request_moderation BOOLEAN NOT NULL,
    state VARCHAR(20) NOT NULL,
    created_on TIMESTAMP,
    published_on TIMESTAMP,
    confirmed_requests BIGINT NOT NULL DEFAULT 0,
    views BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_event_category FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT fk_event_initiator FOREIGN KEY (initiator_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS compilations (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    pinned BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS compilation_events (
    compilation_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    PRIMARY KEY (compilation_id, event_id),
    FOREIGN KEY (compilation_id) REFERENCES compilations(id),
    FOREIGN KEY (event_id) REFERENCES events(id)
);

CREATE TABLE IF NOT EXISTS participation_requests (
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL,
    requester_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    created TIMESTAMP NOT NULL,
    CONSTRAINT fk_request_event FOREIGN KEY (event_id) REFERENCES events(id),
    CONSTRAINT fk_request_user FOREIGN KEY (requester_id) REFERENCES users(id)
);
