CREATE TABLE IF NOT EXISTS `requests` (
    `request_id` int NOT NULL AUTO_INCREMENT,
    `user_id` varchar(18) NOT NULL
    `guild_id` varchar(18) NOT NULL,
    `title` varchar(32) NOT NULL,
    `body` text(512) NOT NULL,
    `answered` tinyint(1) NOT NULL,
    `accepted` tinyint(1) NOT NULL,
    `creation_time` varchar(20) NOT NULL,
    PRIMARY KEY(request_id)
)
