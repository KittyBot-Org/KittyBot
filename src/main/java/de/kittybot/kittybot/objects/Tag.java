package de.kittybot.kittybot.objects;

import de.kittybot.kittybot.jooq.tables.records.GuildTagsRecord;

import java.time.LocalDateTime;

public class Tag{

	private final long id, guildId, userId;
	private final String name, content;
	private final LocalDateTime createdAt;

	public Tag(GuildTagsRecord record){
		this.id = record.getTagId();
		this.name = record.getName();
		this.guildId = record.getGuildId();
		this.userId = record.getUserId();
		this.content = record.getContent();
		this.createdAt = record.getCreatedAt();
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

}
