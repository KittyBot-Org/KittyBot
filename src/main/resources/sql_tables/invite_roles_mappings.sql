CREATE TABLE IF NOT EXISTS invite_roles(
  invite_role_id int NOT NULL,
  role_id bigint NOT NULL,
PRIMARY KEY(invite_role_id, role_id)
);
