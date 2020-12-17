CREATE TABLE IF NOT EXISTS self_assignable_role_groups(
  self_assignable_role_group_id bigserial NOT NULL,
  guild_id                      bigint NOT NULL,
  group_name                    varchar(255) NOT NULL,
PRIMARY KEY(self_assignable_role_group_id)
);
