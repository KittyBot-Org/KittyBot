CREATE TABLE IF NOT EXISTS guild_invite_roles(
  guild_invite_role_id  bigserial PRIMARY KEY NOT NULL,
  guild_invite_id       bigint NOT NULL references guild_invites(guild_invite_id) ON DELETE CASCADE,
  role_id               bigint NOT NULL
);
