CREATE TABLE IF NOT EXISTS guilds(
  id                             BIGINT PRIMARY KEY NOT NULL,
  announcement_channel_id        BIGINT NOT NULL DEFAULT(-1),
  join_message                   TEXT NOT NULL DEFAULT('Welcome ${user}!'),
  join_messages_enabled          BOOLEAN NOT NULL DEFAULT(false),

  leave_message                  TEXT NOT NULL DEFAULT('Bye ${user}!'),
  leave_messages_enabled         BOOLEAN NOT NULL DEFAULT(false),

  log_channel_id                 BIGINT NOT NULL DEFAULT(-1),
  log_messages_enabled           BOOLEAN NOT NULL DEFAULT(false),

  request_channel_id             BIGINT NOT NULL DEFAULT(-1),
  requests_enabled               BOOLEAN NOT NULL DEFAULT(false),

  stream_announcement_channel_id BIGINT NOT NULL DEFAULT(-1),
  stream_announcement_message    TEXT NOT NULL DEFAULT('${user} is now live!'),

  nsfw_enabled                   BOOLEAN NOT NULL DEFAULT(true),

  inactive_role_id               BIGINT NOT NULL DEFAULT(-1),
  inactive_duration              INTERVAL NOT NULL,
  inactive_role_enabled          BOOLEAN NOT NULL DEFAULT(false),

  dj_role_id                     BIGINT NOT NULL DEFAULT(-1),
  snipes_enabled                 BOOLEAN NOT NULL DEFAULT(true),
  role_saver_enabled             BOOLEAN NOT NULL DEFAULT(true)
);
