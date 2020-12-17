CREATE TABLE IF NOT EXISTS snipe_disabled_channels(
  snipe_disabled_channel_id bigserial NOT NULL,
  guild_id                  bigint NOT NULL,
  channel_id                bigint NOT NULL,
PRIMARY KEY(snipe_disabled_channel_id)
);
