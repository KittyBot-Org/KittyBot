CREATE TABLE IF NOT EXISTS snipe_disabled_channels(
  guild_id                bigint NOT NULL,
  channel_id              bigint NOT NULL,
PRIMARY KEY(guild_id, channel_id)
);
