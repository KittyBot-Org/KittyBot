CREATE TABLE IF NOT EXISTS member_roles(
  id       BIGSERIAL PRIMARY KEY NOT NULL,
  guild_id BIGINT NOT NULL REFERENCES guilds(id) ON DELETE CASCADE,
  user_id  BIGINT NOT NULL,
  role_id  BIGINT NOT NULL,
  UNIQUE(guild_id, user_id, role_id)
);