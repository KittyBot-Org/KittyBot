CREATE TABLE IF NOT EXISTS stream_user_events(
  id             BIGSERIAL PRIMARY KEY NOT NULL,
  stream_user_id BIGINT NOT NULL REFERENCES stream_users(id) ON DELETE CASCADE,
  event          INT NOT NULL,
  UNIQUE(stream_user_id, event)
);
