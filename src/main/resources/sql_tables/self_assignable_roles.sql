CREATE TABLE IF NOT EXISTS self_assignable_roles(
  self_assignable_role_id bigserial NOT NULL,
  role_id                 bigint NOT NULL,
  guild_id                bigint NOT NULL,
  emote_id                bigint NOT NULL,
  group_id                bigint NOT NULL,
PRIMARY KEY(self_assignable_role_id)
);
