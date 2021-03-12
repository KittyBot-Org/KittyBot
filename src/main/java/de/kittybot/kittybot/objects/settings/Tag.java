package de.kittybot.kittybot.objects.settings;

import de.kittybot.kittybot.jooq.tables.records.GuildTagsRecord;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.time.LocalDateTime;

public class Tag{

	private final long id, guildId, userId, commandId;
	private final String name, content;
	private final LocalDateTime createdAt, updatedAt;

	public Tag(GuildTagsRecord record){
		this.id = record.getId();
		this.name = record.getName();
		this.guildId = record.getGuildId();
		this.userId = record.getUserId();
		this.content = record.getContent();
		this.createdAt = record.getCreatedAt();
		this.updatedAt = record.getUpdatedAt();
		this.commandId = record.getCommandId();
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

	public long getCommandId(){
		return this.commandId;
	}

	public void process(SlashCommandEvent event){
		event.reply(this.content).queue();
	}

}
