CREATE TABLE IF NOT EXISTS reactive_messages(
  guild_id     bigint NOT NULL,
  channel_id   bigint NOT NULL,
  message_id   bigint NOT NULL,
  user_id      bigint NOT NULL,
  command_path varchar(255) NOT NULL,
  allowed      bigint NOT NULL,
PRIMARY KEY(guild_id, channel_id, message_id, user_id)
);
