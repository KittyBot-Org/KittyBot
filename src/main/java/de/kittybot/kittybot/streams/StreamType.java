package de.kittybot.kittybot.streams;

public enum StreamType{

	TWITCH("Twitch.tv"),
	YOUTUBE("YouTube.com");

	private final String name;

	StreamType(String name){
		this.name = name;
	}

	public String getName(){
		return this.name;
	}
}
