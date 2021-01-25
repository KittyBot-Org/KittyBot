CREATE TABLE IF NOT EXISTS notifications(
  id                BIGSERIAL PRIMARY KEY NOT NULL,
  guild_id          BIGINT NOT NULL REFERENCES guilds(id) ON DELETE CASCADE,
  user_id           BIGINT NOT NULL,
  channel_id        BIGINT NOT NULL,
  message_id        BIGINT NOT NULL,
  content           TEXT NOT NULL,
  created_at        TIMESTAMP NOT NULL DEFAULT(current_timestamp),
  notification_time TIMESTAMP NOT NULL
);
