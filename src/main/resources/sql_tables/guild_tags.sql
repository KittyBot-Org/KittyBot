CREATE TABLE IF NOT EXISTS guild_tags(
  id         BIGSERIAL PRIMARY KEY NOT NULL,
  name       VARCHAR(64) NOT NULL UNIQUE,
  member_id  BIGINT NOT NULL UNIQUE REFERENCES members(id) ON DELETE CASCADE,
  content    TEXT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT(current_timestamp),
  updated_at TIMESTAMP
);
