create TABLE IF NOT EXISTS sessions(
  user_id       bigint PRIMARY KEY NOT NULL,
  access_token  varchar(32) NOT NULL,
  refresh_token varchar(32) NOT NULL,
  expiration    timestamp NOT NULL
);
