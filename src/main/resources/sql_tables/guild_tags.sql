CREATE TABLE IF NOT EXISTS guild_tags(
  tag_id     bigserial PRIMARY KEY NOT NULL,
  name       varchar(64) NOT NULL UNIQUE,
  guild_id   bigint NOT NULL UNIQUE references guilds(guild_id) ON DELETE CASCADE,
  user_id    bigint NOT NULL,
  content    text NOT NULL,
  created_at timestamp NOT NULL
);
