CREATE TABLE IF NOT EXISTS stream_users(
  stream_user_id bigserial NOT NULL,
  guild_id       bigint NOT NULL,
  user_name      varchar(32) NOT NULL,
  stream_type     int NOT NULL,
PRIMARY KEY(stream_user_id)
);
