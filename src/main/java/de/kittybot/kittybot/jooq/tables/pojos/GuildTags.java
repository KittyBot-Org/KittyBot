/*
 * This file is generated by jOOQ.
 */
package de.kittybot.kittybot.jooq.tables.pojos;


import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class GuildTags implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Long          tagId;
    private final String        name;
    private final Long          guildId;
    private final Long          userId;
    private final String        content;
    private final LocalDateTime createdAt;

    public GuildTags(GuildTags value) {
        this.tagId = value.tagId;
        this.name = value.name;
        this.guildId = value.guildId;
        this.userId = value.userId;
        this.content = value.content;
        this.createdAt = value.createdAt;
    }

    public GuildTags(
        Long          tagId,
        String        name,
        Long          guildId,
        Long          userId,
        String        content,
        LocalDateTime createdAt
    ) {
        this.tagId = tagId;
        this.name = name;
        this.guildId = guildId;
        this.userId = userId;
        this.content = content;
        this.createdAt = createdAt;
    }

    /**
     * Getter for <code>public.guild_tags.tag_id</code>.
     */
    public Long getTagId() {
        return this.tagId;
    }

    /**
     * Getter for <code>public.guild_tags.name</code>.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Getter for <code>public.guild_tags.guild_id</code>.
     */
    public Long getGuildId() {
        return this.guildId;
    }

    /**
     * Getter for <code>public.guild_tags.user_id</code>.
     */
    public Long getUserId() {
        return this.userId;
    }

    /**
     * Getter for <code>public.guild_tags.content</code>.
     */
    public String getContent() {
        return this.content;
    }

    /**
     * Getter for <code>public.guild_tags.created_at</code>.
     */
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("GuildTags (");

        sb.append(tagId);
        sb.append(", ").append(name);
        sb.append(", ").append(guildId);
        sb.append(", ").append(userId);
        sb.append(", ").append(content);
        sb.append(", ").append(createdAt);

        sb.append(")");
        return sb.toString();
    }
}