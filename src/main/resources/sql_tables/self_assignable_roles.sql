CREATE TABLE IF NOT EXISTS self_assignable_roles(
  self_assignable_role_id bigserial PRIMARY KEY NOT NULL,
  group_id                bigint NOT NULL references self_assignable_role_groups(self_assignable_role_group_id) ON DELETE CASCADE,
  guild_id                bigint NOT NULL references guilds(guild_id) ON DELETE CASCADE,
  role_id                 bigint NOT NULL,
  emote_id                bigint NOT NULL
);
