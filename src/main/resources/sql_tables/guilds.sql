CREATE TABLE IF NOT EXISTS guilds(
  guild_id                bigint NOT NULL,
  command_prefix          varchar(8) NOT NULL,
  announcement_channel_id bigint NOT NULL,
  request_channel_id      bigint NOT NULL default(-1),
  requests_enabled        boolean NOT NULL default(false),
  join_message            text NOT NULL default('Welcome ${user}!'),
  join_messages_enabled   boolean NOT NULL default(false),
  leave_message           text NOT NULL default('Bye ${user}!'),
  leave_messages_enabled  boolean NOT NULL default(false),
  log_channel_id          bigint NOT NULL default(-1),
  log_messages_enabled    boolean NOT NULL default(false),
  nsfw_enabled            boolean NOT NULL default(true),
  inactive_role_id        bigint NOT NULL default(-1),
  inactive_duration       interval NOT NULL,
  dj_role_id              bigint NOT NULL default(-1),
PRIMARY KEY(guild_id)
);
