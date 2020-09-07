CREATE TABLE IF NOT EXISTS guilds(
  guild_id                varchar(18) NOT NULL,
  command_prefix          varchar(1) NOT NULL,
  request_channel_id      varchar(18) NOT NULL,
  requests_enabled        boolean NOT NULL,
  announcement_channel_id varchar(18) NOT NULL,
  join_messages           text NOT NULL,
  join_messages_enabled   boolean NOT NULL,
  leave_messages          text NOT NULL,
  leave_messages_enabled  boolean NOT NULL,
  boost_messages          text NOT NULL,
  boost_messages_enabled  boolean NOT NULL,
  log_channel_id          varchar(18) NOT NULL,
  log_messages_enabled    boolean NOT NULL,
  nsfw_enabled            boolean NOT NULL,
  dj_role_id              varchar(18) NOT NULL,
  inactive_role_id        varchar(18) NOT NULL,
PRIMARY KEY(guild_id)
);
