CREATE TABLE IF NOT EXISTS stream_users(
  stream_user_id bigserial PRIMARY KEY NOT NULL,
  guild_id       bigint NOT NULL references guilds(guild_id) ON DELETE CASCADE,
  user_name      varchar(32) NOT NULL,
  stream_type    int NOT NULL
);
