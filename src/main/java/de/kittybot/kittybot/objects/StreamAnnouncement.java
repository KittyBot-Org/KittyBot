package de.kittybot.kittybot.objects;

import de.kittybot.kittybot.streams.StreamType;

public class StreamAnnouncement{

	private final String userName;
	private final long guildId;
	private final StreamType streamType;

	public StreamAnnouncement(String userName, long guildId, StreamType streamType){
		this.userName = userName;
		this.guildId = guildId;
		this.streamType = streamType;
	}

	public String getUserName(){
		return userName;
	}

	public long getGuildId(){
		return guildId;
	}

	public StreamType getStreamType(){
		return streamType;
	}

}
