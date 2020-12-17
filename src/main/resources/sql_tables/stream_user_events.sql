CREATE TABLE IF NOT EXISTS stream_user_events(
  steam_user_event_id bigserial NOT NULL,
  stream_user_id      bigint NOT NULL,
  event               int NOT NULL,
PRIMARY KEY(steam_user_event_id)
);
