package de.kittybot.kittybot.command;

public class ReactiveMessage{

	private final long guildId, channelId, messageId, responseId, userId, allowed;
	private final String path;

	public ReactiveMessage(long guildId, long channelId, long messageId, long responseId, long userId, String path, long allowed){
		this.guildId = guildId;
		this.channelId = channelId;
		this.messageId = messageId;
		this.responseId = responseId;
		this.userId = userId;
		this.path = path;
		this.allowed = allowed;
	}

	public long getGuildId(){
		return this.guildId;
	}

	public long getChannelId(){
		return this.channelId;
	}

	public long getMessageId(){
		return this.messageId;
	}

	public long getResponseId(){
		return this.responseId;
	}

	public long getUserId(){
		return this.userId;
	}

	public String getPath(){
		return this.path;
	}

	public long getAllowed(){
		return this.allowed;
	}

}
