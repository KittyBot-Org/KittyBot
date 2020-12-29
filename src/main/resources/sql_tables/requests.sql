CREATE TABLE IF NOT EXISTS requests(
  request_id    bigserial PRIMARY KEY NOT NULL,
  user_id       bigint NOT NULL,
  guild_id      bigint NOT NULL references guilds(guild_id) ON DELETE CASCADE,
  title         varchar(64) NOT NULL,
  body          text NOT NULL,
  answered      boolean NOT NULL,
  accepted      boolean NOT NULL,
  creation_time timestamp NOT NULL
);
