CREATE TABLE found
(
    id               BIGSERIAL PRIMARY KEY,
    founder          UUID NOT NULL,
    description      TEXT,
    place            BIGINT REFERENCES place.id NOT NULL,
    lostPeriod_start  TIMESTAMP WITH TIME ZONE NOT NULL,
    lostPeriod_finish TIMESTAMP WITH TIME ZONE NOT NULL
)