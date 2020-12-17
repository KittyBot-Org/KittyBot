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
public class Requests implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Long          requestId;
    private final Long          userId;
    private final Long          guildId;
    private final String        title;
    private final String        body;
    private final Boolean       answered;
    private final Boolean       accepted;
    private final LocalDateTime creationTime;

    public Requests(Requests value) {
        this.requestId = value.requestId;
        this.userId = value.userId;
        this.guildId = value.guildId;
        this.title = value.title;
        this.body = value.body;
        this.answered = value.answered;
        this.accepted = value.accepted;
        this.creationTime = value.creationTime;
    }

    public Requests(
        Long          requestId,
        Long          userId,
        Long          guildId,
        String        title,
        String        body,
        Boolean       answered,
        Boolean       accepted,
        LocalDateTime creationTime
    ) {
        this.requestId = requestId;
        this.userId = userId;
        this.guildId = guildId;
        this.title = title;
        this.body = body;
        this.answered = answered;
        this.accepted = accepted;
        this.creationTime = creationTime;
    }

    /**
     * Getter for <code>public.requests.request_id</code>.
     */
    public Long getRequestId() {
        return this.requestId;
    }

    /**
     * Getter for <code>public.requests.user_id</code>.
     */
    public Long getUserId() {
        return this.userId;
    }

    /**
     * Getter for <code>public.requests.guild_id</code>.
     */
    public Long getGuildId() {
        return this.guildId;
    }

    /**
     * Getter for <code>public.requests.title</code>.
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Getter for <code>public.requests.body</code>.
     */
    public String getBody() {
        return this.body;
    }

    /**
     * Getter for <code>public.requests.answered</code>.
     */
    public Boolean getAnswered() {
        return this.answered;
    }

    /**
     * Getter for <code>public.requests.accepted</code>.
     */
    public Boolean getAccepted() {
        return this.accepted;
    }

    /**
     * Getter for <code>public.requests.creation_time</code>.
     */
    public LocalDateTime getCreationTime() {
        return this.creationTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Requests (");

        sb.append(requestId);
        sb.append(", ").append(userId);
        sb.append(", ").append(guildId);
        sb.append(", ").append(title);
        sb.append(", ").append(body);
        sb.append(", ").append(answered);
        sb.append(", ").append(accepted);
        sb.append(", ").append(creationTime);

        sb.append(")");
        return sb.toString();
    }
}
