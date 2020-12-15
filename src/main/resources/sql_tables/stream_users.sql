CREATE TABLE IF NOT EXISTS snipe_disabled_channels(
  guild_id   bigint NOT NULL,
  user_name  varchar(64) NOT NULL,
  service_id int NOT NULL,
PRIMARY KEY(guild_id, channel_id)
);
