CREATE TABLE IF NOT EXISTS self_assignable_role_groups(
  group_id  SERIAL NOT NULL,
  guild_id  varchar(18) NOT NULL,
  group_name varchar(18) NOT NULL,
  max_roles int NOT NULL,
PRIMARY KEY(group_id)
);
