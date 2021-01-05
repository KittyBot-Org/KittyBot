CREATE TABLE IF NOT EXISTS guild_prefixes(
  guild_prefix_id bigserial PRIMARY KEY NOT NULL,
  guild_id        bigint NOT NULL UNIQUE references guilds(guild_id) ON DELETE CASCADE,
  prefix          varchar(4) NOT NULL UNIQUE
);
