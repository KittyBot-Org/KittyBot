CREATE TABLE IF NOT EXISTS reactive_messages(
  id           BIGSERIAL PRIMARY KEY NOT NULL,
  member_id    BIGINT NOT NULL REFERENCES members(id) ON DELETE CASCADE,
  channel_id   BIGINT NOT NULL,
  message_id   BIGINT NOT NULL,
  command_path VARCHAR(255) NOT NULL,
  allowed      BIGINT NOT NULL DEFAULT(-1)
);
