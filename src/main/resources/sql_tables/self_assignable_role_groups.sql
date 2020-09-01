CREATE TABLE IF NOT EXISTS self_assignable_role_groups(
  group_id  varchar(18) NOT NULL AUTO_INCREMENT,
  guild_id  varchar(18) NOT NULL,
  group_name varchar(18) NOT NULL,
PRIMARY KEY(group_id)
);
