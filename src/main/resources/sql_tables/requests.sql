CREATE TABLE IF NOT EXISTS requests(
  request_id    serial NOT NULL,
  user_id       bigint NOT NULL,
  guild_id      bigint NOT NULL,
  title         varchar(64) NOT NULL,
  body          text(512) NOT NULL,
  answered      boolean NOT NULL,
  accepted      boolean NOT NULL,
  creation_time timestamp NOT NULL,
PRIMARY KEY(request_id)
);
