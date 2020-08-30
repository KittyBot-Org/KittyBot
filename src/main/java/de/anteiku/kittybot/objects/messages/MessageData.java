package de.anteiku.kittybot.objects.messages;

import net.dv8tion.jda.api.entities.Message;

import java.time.Instant;

public class MessageData{

	private final String messageId;
	private final String authorId;
	private final Instant creation;
	private final String content;
	private final String channelId;
	private final String guildId;

	public MessageData(final Message message){
		this.messageId = message.getId();
		this.authorId = message.getAuthor().getId();
		this.creation = message.getTimeCreated().toInstant();
		this.content = message.getContentRaw();
		this.channelId = message.getTextChannel().getId();
		this.guildId = message.getGuild().getId();
	}

	public String getId(){
		return messageId;
	}

	public String getAuthorId(){
		return authorId;
	}

	public Instant getCreation(){
		return creation;
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

}