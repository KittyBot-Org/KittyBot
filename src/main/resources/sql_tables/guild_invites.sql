CREATE TABLE IF NOT EXISTS guild_invites(
  guild_invite_id bigserial NOT NULL,
  guild_id        bigint NOT NULL,
  code            varchar(8) NOT NULL,
PRIMARY KEY(guild_invite_id)
);
