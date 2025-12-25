CREATE TABLE lost
(
    id               BIGSERIAL PRIMARY KEY,
    owner            UUID NOT NULL,
    description      TEXT,
    place            BIGINT REFERENCES place.id,
    lostPeriod_start  TIMESTAMP WITH TIME ZONE,
    lostPeriod_finish TIMESTAMP WITH TIME ZONE,
    found            BOOLEAN DEFAULT false,
)