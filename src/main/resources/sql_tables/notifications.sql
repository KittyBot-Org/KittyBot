CREATE TABLE IF NOT EXISTS notifications(
  notification_id   bigserial NOT NULL,
  guild_id          bigint NOT NULL,
  channel_id        bigint NOT NULL,
  message_id        bigint NOT NULL,
  user_id           bigint NOT NULL,
  content           text NOT NULL,
  creation_time     timestamp NOT NULL,
  notification_time timestamp NOT NULL,
PRIMARY KEY(notification_id)
);
