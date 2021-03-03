CREATE TABLE IF NOT EXISTS stream_users(
  id              BIGSERIAL PRIMARY KEY NOT NULL,
  guild_id        BIGINT NOT NULL REFERENCES guilds(id) ON DELETE CASCADE,
  user_id         BIGINT NOT NULL,
  user_name       VARCHAR(32) NOT NULL,
  stream_type     INT NOT NULL,
  UNIQUE(guild_id, user_id, stream_type)
);
