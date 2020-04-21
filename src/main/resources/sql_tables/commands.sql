CREATE TABLE IF NOT EXISTS `commands` (
    `id` varchar(18) NOT NULL,
    `guild_id` varchar(18) NOT NULL,
    `user_id` varchar(18) NOT NULL,
    `command` varchar(18) NOT NULL,
    `processing_time` int NOT NULL,
    `time` varchar(20) NOT NULL,
    PRIMARY KEY(id, guild_id)
)