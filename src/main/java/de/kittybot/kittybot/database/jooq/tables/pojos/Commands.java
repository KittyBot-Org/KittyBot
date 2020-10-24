/*
 * This file is generated by jOOQ.
 */
package de.kittybot.kittybot.database.jooq.tables.pojos;


import org.jooq.types.YearToSecond;

import java.io.Serializable;
import java.time.LocalDateTime;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Commands implements Serializable {

    private static final long serialVersionUID = -1380580204;

    private final String        messageId;
    private final String        guildId;
    private final String        userId;
    private final String        command;
    private final YearToSecond  processingTime;
    private final LocalDateTime time;

    public Commands(Commands value) {
        this.messageId = value.messageId;
        this.guildId = value.guildId;
        this.userId = value.userId;
        this.command = value.command;
        this.processingTime = value.processingTime;
        this.time = value.time;
    }

    public Commands(
        String        messageId,
        String        guildId,
        String        userId,
        String        command,
        YearToSecond  processingTime,
        LocalDateTime time
    ) {
        this.messageId = messageId;
        this.guildId = guildId;
        this.userId = userId;
        this.command = command;
        this.processingTime = processingTime;
        this.time = time;
    }

    public String getMessageId() {
        return this.messageId;
    }

    public String getGuildId() {
        return this.guildId;
    }

    public String getUserId() {
        return this.userId;
    }

    public String getCommand() {
        return this.command;
    }

    public YearToSecond getProcessingTime() {
        return this.processingTime;
    }

    public LocalDateTime getTime() {
        return this.time;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Commands (");

        sb.append(messageId);
        sb.append(", ").append(guildId);
        sb.append(", ").append(userId);
        sb.append(", ").append(command);
        sb.append(", ").append(processingTime);
        sb.append(", ").append(time);

        sb.append(")");
        return sb.toString();
    }
}
