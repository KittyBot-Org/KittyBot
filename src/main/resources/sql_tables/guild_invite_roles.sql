CREATE TABLE IF NOT EXISTS guild_invite_roles(
  id              BIGSERIAL PRIMARY KEY NOT NULL,
  guild_invite_id BIGINT NOT NULL REFERENCES guild_invites(id) ON DELETE CASCADE,
  role_id         BIGINT NOT NULL,
  UNIQUE(guild_invite_id, role_id)
);
