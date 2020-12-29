CREATE TABLE IF NOT EXISTS stream_user_events(
  steam_user_event_id bigserial PRIMARY KEY NOT NULL,
  stream_user_id      bigint NOT NULL references stream_users(stream_user_id) ON DELETE CASCADE,
  event               int NOT NULL
);
