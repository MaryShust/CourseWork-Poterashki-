CREATE TABLE completed (
    id SERIAL PRIMARY KEY,
    lost_id BIGINT,
    found_id BIGINT,
    completion_time TIMESTAMP WITH TIME ZONE
)