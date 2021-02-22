CREATE TABLE IF NOT EXISTS requests(
  id          BIGSERIAL PRIMARY KEY NOT NULL,
  guild_id    BIGINT NOT NULL REFERENCES guilds(id) ON DELETE CASCADE,
  user_id     BIGINT NOT NULL,
  title       VARCHAR(64) NOT NULL,
  body        TEXT NOT NULL,
  answered    BOOLEAN NOT NULL,
  accepted    BOOLEAN NOT NULL,
  creation_at TIMESTAMP NOT NULL DEFAULT(current_timestamp)
);
