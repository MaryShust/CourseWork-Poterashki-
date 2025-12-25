CREATE TABLE photo
(
    id         BIGSERIAL PRIMARY KEY,
    entity_type VARCHAR NOT NULL,
    entity_id   BIGINT  NOT NULL,
    uri        TEXT

)