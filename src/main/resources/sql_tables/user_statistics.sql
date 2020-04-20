CREATE TABLE IF NOT EXISTS `user_statistics` (
    `user_id` varchar(18) NOT NULL,
    `guild_id` varchar(18) NOT NULL,
    `xp` int NOT NULL,
    `level` int NOT NULL,
    `bot_calls` int NOT NULL,
    `voice_time` int NOT NULL,
    `message_count` int NOT NULL,
    `emote_count` int NOT NULL,
    `last_active` varchar(20) NOT NULL,
    PRIMARY KEY(user_id, guild_id)
)