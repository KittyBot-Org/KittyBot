CREATE TABLE IF NOT EXISTS bot_ignored_users(
  bot_ignored_user_id bigserial PRIMARY KEY NOT NULL,
  guild_id            bigint NOT NULL references guilds(guild_id) ON DELETE CASCADE,
  user_id             bigint NOT NULL
);
