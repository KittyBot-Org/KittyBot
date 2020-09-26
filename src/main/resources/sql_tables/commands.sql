CREATE TABLE IF NOT EXISTS commands(
  message_id      varchar(18) NOT NULL,
  guild_id        varchar(18) NOT NULL,
  user_id         varchar(18) NOT NULL,
  command         varchar(18) NOT NULL,
  processing_time timestamp NOT NULL,
  time            timestamp NOT NULL,
PRIMARY KEY(message_id, guild_id)
);
