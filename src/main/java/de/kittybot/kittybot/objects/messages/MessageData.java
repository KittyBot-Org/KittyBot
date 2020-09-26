package de.kittybot.kittybot.objects.messages;

import net.dv8tion.jda.api.entities.Message;

import java.time.OffsetDateTime;

public class MessageData{

	private final String messageId;
	private final String authorId;
	private final String content;
	private final String channelId;
	private final String guildId;
	private final String jumpUrl;
	private final OffsetDateTime timeCreated;
	private OffsetDateTime timeEdited;

	public MessageData(final Message message){
		this.messageId = message.getId();
		this.authorId = message.getAuthor().getId();
		this.content = message.getContentRaw();
		this.channelId = message.getTextChannel().getId();
		this.guildId = message.getGuild().getId();
		this.jumpUrl = message.getJumpUrl();
		this.timeCreated = message.getTimeCreated();
		this.timeEdited = message.getTimeEdited();
	}

	public String getId(){
		return messageId;
	}

	public String getAuthorId(){
		return authorId;
	}

	public String getContent(){
		return content;
	}

	public String getChannelId(){
		return channelId;
	}

	public String getGuildId(){
		return guildId;
	}

	public String getJumpUrl(){
		return jumpUrl;
	}

	public OffsetDateTime getTimeCreated(){
		return timeCreated;
	}

	public OffsetDateTime getTimeEdited(){
		return timeEdited;
	}

	public MessageData setTimeEdited(final OffsetDateTime timeEdited){
		this.timeEdited = timeEdited;
		return this;
	}

}