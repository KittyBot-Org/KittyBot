CREATE TABLE IF NOT EXISTS user_statistics(
  user_statistic_id bigserial NOT NULL,
  user_id           bigint NOT NULL,
  guild_id          bigint NOT NULL,
  xp                int NOT NULL default(0),
  level             int NOT NULL default(0),
  bot_calls         int NOT NULL default(0),
  voice_time        int NOT NULL default(0),
  message_count     int NOT NULL default(0),
  emote_count       int NOT NULL default(0),
  last_active       timestamp NOT NULL,
PRIMARY KEY(user_statistic_id)
);
