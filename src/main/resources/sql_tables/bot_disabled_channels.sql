CREATE TABLE IF NOT EXISTS bot_disabled_channels(
  bot_disabled_channel_id bigserial PRIMARY KEY NOT NULL,
  guild_id                bigint NOT NULL references guilds(guild_id) ON DELETE CASCADE,
  channel_id              bigint NOT NULL
);
