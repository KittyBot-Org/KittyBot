CREATE TABLE IF NOT EXISTS user_statistics(
  id            BIGSERIAL PRIMARY KEY NOT NULL,
  guild_id      BIGINT NOT NULL REFERENCES guilds(id) ON DELETE CASCADE,
  user_id       BIGINT NOT NULL,
  xp            BIGINT NOT NULL DEFAULT(0),
  commands_used INT NOT NULL DEFAULT(0),
  voice_time    INTERVAL NOT NULL DEFAULT('0 seconds'),
  stream_time   INTERVAL NOT NULL DEFAULT('0 seconds'),
  messages_sent INT NOT NULL DEFAULT(0),
  emotes_sent   INT NOT NULL DEFAULT(0),
  stickers_sent INT NOT NULL DEFAULT(0),
  last_active   TIMESTAMP NOT NULL DEFAULT(current_timestamp),
  UNIQUE(guild_id, user_id)
);
