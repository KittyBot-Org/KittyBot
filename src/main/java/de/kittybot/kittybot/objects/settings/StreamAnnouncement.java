package de.kittybot.kittybot.objects.settings;

import de.kittybot.kittybot.jooq.tables.records.StreamUsersRecord;
import de.kittybot.kittybot.objects.streams.StreamType;

public class StreamAnnouncement{

	private final long userId, guildId;
	private final String userName;
	private final StreamType streamType;

	public StreamAnnouncement(long userId, String userName, long guildId, StreamType streamType){
		this.userId = userId;
		this.userName = userName;
		this.guildId = guildId;
		this.streamType = streamType;
	}

	public StreamAnnouncement(StreamUsersRecord record){
		this.userId = record.getUserId();
		this.userName = record.getUserName();
		this.guildId = record.getGuildId();
		this.streamType = StreamType.byId(record.getStreamType());
	}

	public long getUserId(){
		return userId;
	}

	public String getUserName(){
		return this.userName;
	}

	public long getGuildId(){
		return guildId;
	}

	public StreamType getStreamType(){
		return streamType;
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
