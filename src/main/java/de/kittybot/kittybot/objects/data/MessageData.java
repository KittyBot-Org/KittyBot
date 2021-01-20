package de.kittybot.kittybot.objects.data;

import net.dv8tion.jda.api.entities.Message;

import java.time.OffsetDateTime;

public class MessageData{

	private final long messageId;
	private final long authorId;
	private final String content;
	private final long channelId;
	private final long guildId;
	private final String jumpUrl;
	private final OffsetDateTime timeCreated;
	private OffsetDateTime timeEdited;

	public MessageData(Message message){
		this.messageId = message.getIdLong();
		this.authorId = message.getAuthor().getIdLong();
		this.content = message.getContentRaw();
		this.channelId = message.getTextChannel().getIdLong();
		this.guildId = message.getGuild().getIdLong();
		this.jumpUrl = message.getJumpUrl();
		this.timeCreated = message.getTimeCreated();
		this.timeEdited = message.getTimeEdited();
	}

	public Long getId(){
		return this.messageId;
	}

	public Long getAuthorId(){
		return this.authorId;
	}

	public String getContent(){
		return this.content;
	}

	public Long getChannelId(){
		return this.channelId;
	}

	public Long getGuildId(){
		return this.guildId;
	}

	public String getJumpUrl(){
		return this.jumpUrl;
	}

	public OffsetDateTime getTimeCreated(){
		return this.timeCreated;
	}

	public OffsetDateTime getTimeEdited(){
		return this.timeEdited;
	}

	public MessageData setTimeEdited(final OffsetDateTime timeEdited){
		this.timeEdited = timeEdited;
		return this;
	}

}