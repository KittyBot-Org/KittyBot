CREATE TABLE IF NOT EXISTS reactive_messages(
  reactive_message_id bigserial NOT NULL,
  guild_id            bigint NOT NULL,
  channel_id          bigint NOT NULL,
  message_id          bigint NOT NULL,
  user_id             bigint NOT NULL,
  command_path        varchar(255) NOT NULL,
  allowed             bigint NOT NULL default(-1),
PRIMARY KEY(reactive_message_id)
);
