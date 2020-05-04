CREATE TABLE IF NOT EXISTS self_assignable_roles (
    role_id varchar(18) NOT NULL,
    guild_id varchar(18) NOT NULL,
    emote_id varchar(18) NOT NULL,
    PRIMARY KEY(role_id, guild_id)
)
