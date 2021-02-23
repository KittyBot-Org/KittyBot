CREATE TABLE IF NOT EXISTS bot_disabled_channels(
  id         BIGSERIAL PRIMARY KEY NOT NULL,
  guild_id   BIGINT NOT NULL REFERENCES guilds(id) ON DELETE CASCADE,
  channel_id BIGINT NOT NULL,
  UNIQUE(guild_id, channel_id)
);
