CREATE TABLE IF NOT EXISTS user_statistics(
  id            BIGSERIAL PRIMARY KEY NOT NULL,
  member_id     BIGINT NOT NULL UNIQUE REFERENCES members(id) ON DELETE CASCADE,
  xp            INT NOT NULL DEFAULT(0),
  level         INT NOT NULL DEFAULT(0),
  bot_calls     INT NOT NULL DEFAULT(0),
  voice_time    INT NOT NULL DEFAULT(0),
  message_count INT NOT NULL DEFAULT(0),
  emote_count   INT NOT NULL DEFAULT(0),
  last_active   TIMESTAMP NOT NULL DEFAULT(current_timestamp)
);

