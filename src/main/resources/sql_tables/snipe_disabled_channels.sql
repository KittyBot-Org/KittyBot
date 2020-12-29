CREATE TABLE IF NOT EXISTS snipe_disabled_channels(
  snipe_disabled_channel_id bigserial PRIMARY KEY NOT NULL,
  guild_id                  bigint NOT NULL references guilds(guild_id) ON DELETE CASCADE,
  channel_id                bigint NOT NULL
);
