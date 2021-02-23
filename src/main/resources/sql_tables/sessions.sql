create TABLE IF NOT EXISTS sessions(
  id            BIGSERIAL PRIMARY KEY NOT NULL,
  user_id       BIGINT NOT NULL,
  access_token  VARCHAR(32) NOT NULL,
  refresh_token VARCHAR(32) NOT NULL,
  expiration    TIMESTAMP NOT NULL,
  UNIQUE(user_id)
);
