CREATE TABLE IF NOT EXISTS bot_disabled_channels(
  bot_disabled_channel_id bigserial NOT NULL,
  guild_id                bigint NOT NULL,
  channel_id              bigint NOT NULL,
PRIMARY KEY(guild_id, channel_id)
);
