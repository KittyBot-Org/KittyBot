CREATE TABLE IF NOT EXISTS reactive_messages (
    message_id varchar(18) NOT NULL,
    user_id varchar(18) NOT NULL,
    guild_id varchar(18) NOT NULL,
    command_id varchar(18) NOT NULL,
    command varchar(18) NOT NULL,
    allowed varchar(18) NOT NULL,
    PRIMARY KEY(message_id, user_id, guild_id)
);
