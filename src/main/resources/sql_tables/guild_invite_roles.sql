CREATE TABLE IF NOT EXISTS guild_invite_roles(
  guild_invite_role_id  bigserial NOT NULL,
  guild_invite_id       bigint NOT NULL,
  role_id               bigint NOT NULL,
PRIMARY KEY(guild_invite_role_id)
);
