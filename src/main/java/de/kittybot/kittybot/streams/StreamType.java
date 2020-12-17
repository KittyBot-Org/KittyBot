package de.kittybot.kittybot.streams;

import java.util.Arrays;

public enum StreamType{

	TWITCH(1, "Twitch.tv"),
	YOUTUBE(2, "YouTube.com");

	private final int id;
	private final String name;

	StreamType(int id, String name){
		this.id = id;
		this.name = name;
	}

	public int getId(){
		return this.id;
	}

	public String getName(){
		return this.name;
	}

	public static StreamType byId(int id){
		return Arrays.stream(values()).filter(streamType -> streamType.getId() == id).findFirst().orElse(null);
	}
}
