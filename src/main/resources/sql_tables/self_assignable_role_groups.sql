CREATE TABLE IF NOT EXISTS self_assignable_role_groups(
  self_assignable_role_group_id bigserial PRIMARY KEY NOT NULL,
  guild_id                      bigint NOT NULL references guilds(guild_id) ON DELETE CASCADE,
  group_name                    varchar(255) NOT NULL
);
