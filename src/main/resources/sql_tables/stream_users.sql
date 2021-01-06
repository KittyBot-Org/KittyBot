CREATE TABLE IF NOT EXISTS stream_users(
  id          BIGSERIAL PRIMARY KEY NOT NULL,
  guild_id    BIGINT NOT NULL REFERENCES guilds(id) ON DELETE CASCADE,
  user_name   VARCHAR(32) NOT NULL,
  stream_type INT NOT NULL
);
