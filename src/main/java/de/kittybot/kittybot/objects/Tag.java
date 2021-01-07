package de.kittybot.kittybot.objects;

import org.jooq.Record;

import java.time.LocalDateTime;

import static de.kittybot.kittybot.jooq.Tables.*;

public class Tag{

	private final long id, guildId, userId;
	private final String name, content;
	private final LocalDateTime createdAt, updatedAt;

	public Tag(Record record){
		this.id = record.get(GUILD_TAGS.ID);
		this.name = record.get(GUILD_TAGS.NAME);
		this.guildId = record.get(MEMBERS.GUILD_ID);
		this.userId = record.get(MEMBERS.USER_ID);
		this.content = record.get(GUILD_TAGS.CONTENT);
		this.createdAt = record.get(GUILD_TAGS.CREATED_AT);
		this.updatedAt = record.get(GUILD_TAGS.UPDATED_AT);
	}

	public long getId(){
		return this.id;
	}

	public String getName(){
		return this.name;
	}

	public String getContent(){
		return this.content;
	}

	public long getGuildId(){
		return this.guildId;
	}

	public long getUserId(){
		return this.userId;
	}

	public LocalDateTime getCreatedAt(){
		return this.createdAt;
	}

	public LocalDateTime getUpdatedAt(){
		return this.updatedAt;
	}

}
