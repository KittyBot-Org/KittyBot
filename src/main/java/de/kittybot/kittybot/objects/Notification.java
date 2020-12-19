package de.kittybot.kittybot.objects;

import java.time.LocalDateTime;

public class Notification{

	private final long id, guildId, channelId, messageId, userId;
	private final String content;
	private final LocalDateTime notificationTime;
	private final LocalDateTime creationTime;

	public Notification(long id, long guildId, long channelId, long messageId, long userId, String content, LocalDateTime creationTime, LocalDateTime notificationTime){
		this.id = id;
		this.guildId = guildId;
		this.channelId = channelId;
		this.messageId = messageId;
		this.userId = userId;
		this.content = content;
		this.creationTime = creationTime;
		this.notificationTime = notificationTime;
	}

	public long getId(){
		return this.id;
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

	public long getUserId(){
		return this.userId;
	}

	public String getContent(){
		return this.content;
	}

	public LocalDateTime getCreationTime(){
		return this.creationTime;
	}

	public LocalDateTime getNotificationTime(){
		return this.notificationTime;
	}

}
