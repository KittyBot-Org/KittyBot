package de.kittybot.kittybot.objects.settings;

import de.kittybot.kittybot.jooq.tables.records.StreamUsersRecord;
import de.kittybot.kittybot.objects.streams.StreamType;

public class StreamAnnouncement{

	private final long id, userId, guildId;
	private final String userName;
	private final StreamType streamType;
	private boolean isLive;

	public StreamAnnouncement(long userId, String userName, long guildId, StreamType streamType){
		this.id = -1;
		this.userId = userId;
		this.userName = userName;
		this.guildId = guildId;
		this.streamType = streamType;
		this.isLive = false;
	}

	public StreamAnnouncement(StreamUsersRecord record){
		this.id = record.getId();
		this.userId = record.getUserId();
		this.userName = record.getUserName();
		this.guildId = record.getGuildId();
		this.streamType = StreamType.byId(record.getStreamType());
		this.isLive = record.getIsLive();
	}

	public long getId(){
		return this.id;
	}

	public long getUserId(){
		return this.userId;
	}

	public String getUserName(){
		return this.userName;
	}

	public long getGuildId(){
		return this.guildId;
	}

	public StreamType getStreamType(){
		return this.streamType;
	}

	public boolean isLive(){
		return this.isLive;
	}

	public void setLive(boolean isLive){
		this.isLive = isLive;
	}

	public String getStreamUrl(){
		if(this.streamType == StreamType.TWITCH){
			return this.streamType.getBaseUrl() + this.userName;
		}
		if(this.streamType == StreamType.YOUTUBE){
			return this.streamType.getBaseUrl() + "c/" + this.userName;
		}
		return null;
	}

}
