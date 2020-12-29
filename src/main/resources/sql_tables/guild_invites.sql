CREATE TABLE IF NOT EXISTS guild_invites(
  guild_invite_id bigserial PRIMARY KEY NOT NULL,
  guild_id        bigint NOT NULL references guilds(guild_id) ON DELETE CASCADE,
  code            varchar(8) NOT NULL UNIQUE
);
