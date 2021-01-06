CREATE TABLE IF NOT EXISTS requests(
  id          BIGSERIAL PRIMARY KEY NOT NULL,
  member_id   BIGINT NOT NULL REFERENCES members(id) ON DELETE CASCADE,
  title       VARCHAR(64) NOT NULL,
  body        TEXT NOT NULL,
  answered    BOOLEAN NOT NULL,
  accepted    BOOLEAN NOT NULL,
  creation_at TIMESTAMP NOT NULL DEFAULT(current_timestamp)
);
