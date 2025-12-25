CREATE TABLE contact (
    id BIGSERIAL PRIMARY KEY,
    user_id UUID REFERENCES user.id,
    data JSONB DEFAULT null
)