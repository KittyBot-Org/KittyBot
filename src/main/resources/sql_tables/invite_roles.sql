CREATE TABLE IF NOT EXISTS invite_roles(
  invite_role_id serial NOT NULL,
  guild_id bigint NOT NULL,
  code     varchar(8) NOT NULL,
PRIMARY KEY(invite_role_id)
);
