CREATE TABLE IF NOT EXISTS guild_tags(
  id         BIGSERIAL PRIMARY KEY NOT NULL,
  name       VARCHAR(64) NOT NULL UNIQUE,
  guild_id   BIGINT NOT NULL UNIQUE REFERENCES guilds(id) ON DELETE CASCADE,
  user_id    BIGINT NOT NULL,
  content    TEXT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT(current_timestamp),
  updated_at TIMESTAMP
);
