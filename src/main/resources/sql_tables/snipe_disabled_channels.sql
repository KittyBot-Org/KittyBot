CREATE TABLE IF NOT EXISTS snipe_disabled_channels(
  id         BIGSERIAL PRIMARY KEY NOT NULL,
  guild_id   BIGINT NOT NULL REFERENCES guilds(id) ON DELETE CASCADE,
  channel_id BIGINT NOT NULL
);
