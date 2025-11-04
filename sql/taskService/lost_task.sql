CREATE TABLE lost_task(
    id BIGSERIAL PRIMARY KEY,
    lost_id BIGINT,
    creation_date TIMESTAMP,
    status VARCHAR(50),
    metadata TEXT
)