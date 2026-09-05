-- PostgreSQL schema. Shared by both the jOOQ and the JPA/ORM data-access layers.
CREATE TABLE time_access_log (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    access_mode VARCHAR(16)              NOT NULL,
    recorded_at TIMESTAMPTZ              NOT NULL
);
