CREATE TABLE IF NOT EXISTS voters(
  id          BIGSERIAL PRIMARY KEY NOT NULL,
  user_id     BIGINT NOT NULL,
  vote_expiry TIMESTAMP NOT NULL,
  UNIQUE(user_id)
);
