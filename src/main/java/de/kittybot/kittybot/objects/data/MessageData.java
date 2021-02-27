package de.kittybot.kittybot.objects.data;

import net.dv8tion.jda.api.entities.Message;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class MessageData{

	private final long messageId;
	private final long authorId;
	private final String content;
	private final long channelId;
	private final long guildId;
	private final String jumpUrl;
	private final OffsetDateTime timeCreated;
	private OffsetDateTime timeEdited;
	private final List<String> attachments;

	public MessageData(Message message){
		this.messageId = message.getIdLong();
		this.authorId = message.getAuthor().getIdLong();
		this.content = message.getContentRaw();
		this.channelId = message.getTextChannel().getIdLong();
		this.guildId = message.getGuild().getIdLong();
		this.jumpUrl = message.getJumpUrl();
		this.timeCreated = message.getTimeCreated();
		this.timeEdited = message.getTimeEdited();
		this.attachments = message.getAttachments().stream().map(Message.Attachment::getProxyUrl).collect(Collectors.toList());
	}

	public long getId(){
		return this.messageId;
	}

	public long getAuthorId(){
		return this.authorId;
	}

	public String getContent(){
		return this.content;
	}

	public long getChannelId(){
		return this.channelId;
	}

	public long getGuildId(){
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

	public List<String> getAttachments(){
		return this.attachments;
	}

}