CREATE TABLE IF NOT EXISTS self_assignable_roles(
  role_id  bigint NOT NULL,
  guild_id bigint NOT NULL,
  emote_id bigint NOT NULL,
PRIMARY KEY(role_id, guild_id)
);
