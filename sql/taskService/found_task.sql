CREATE TABLE found_task
(
    id           BIGSERIAL PRIMARY KEY,
    found_id       BIGINT,
    creation_date TIMESTAMP,
    status       VARCHAR(50),
    metadata     TEXT
)