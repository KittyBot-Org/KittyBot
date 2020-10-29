create TABLE IF NOT EXISTS sessions(
  user_id       varchar(18) NOT NULL,
  access_token  varchar(32) NOT NULL,
  refresh_token varchar(32) NOT NULL,
  expiration    timestamp NOT NULL,
PRIMARY KEY(user_id)
);
