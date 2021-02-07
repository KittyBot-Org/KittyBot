CREATE TABLE IF NOT EXISTS self_assignable_role_messages(
  id         BIGSERIAL PRIMARY KEY NOT NULL,
  guild_id   BIGINT NOT NULL REFERENCES guilds(id) ON DELETE CASCADE,
  message_id BIGINT NOT NULL,
  UNIQUE(guild_id, message_id)
);



