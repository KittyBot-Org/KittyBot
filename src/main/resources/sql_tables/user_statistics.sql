CREATE TABLE IF NOT EXISTS user_statistics(
  id            BIGSERIAL PRIMARY KEY NOT NULL,
  guild_id      BIGINT NOT NULL REFERENCES guilds(id) ON DELETE CASCADE,
  user_id       BIGINT NOT NULL,
  xp            INT NOT NULL DEFAULT(0),
  level         INT NOT NULL DEFAULT(0),
  bot_calls     INT NOT NULL DEFAULT(0),
  voice_time    INT NOT NULL DEFAULT(0),
  message_count INT NOT NULL DEFAULT(0),
  emote_count   INT NOT NULL DEFAULT(0),
  last_active   TIMESTAMP NOT NULL DEFAULT(current_timestamp)
);

