CREATE TABLE IF NOT EXISTS self_assignable_role_groups(
  id         BIGSERIAL PRIMARY KEY NOT NULL,
  guild_id   BIGINT NOT NULL REFERENCES guilds(id) ON DELETE CASCADE,
  group_name VARCHAR(255) NOT NULL,
  max_roles  INT NOT NULL DEFAULT(-1)
);
