CREATE TABLE IF NOT EXISTS self_assignable_roles(
  group_id   serial NOT NULL,
  guild_id   bigint NOT NULL,
  group_name varchar(255) NOT NULL,
PRIMARY KEY(role_id, guild_id)
);
