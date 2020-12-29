CREATE TABLE IF NOT EXISTS reactive_messages(
  reactive_message_id bigserial PRIMARY KEY NOT NULL,
  guild_id            bigint NOT NULL references guilds(guild_id) ON DELETE CASCADE,
  channel_id          bigint NOT NULL,
  message_id          bigint NOT NULL,
  user_id             bigint NOT NULL,
  command_path        varchar(255) NOT NULL,
  allowed             bigint NOT NULL default(-1)
);
