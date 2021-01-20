package de.kittybot.kittybot.objects.streams;

import java.util.Arrays;

public enum StreamType{

	TWITCH(0, "Twitch", "twitch.tv"),
	YOUTUBE(1, "YouTube", "youtube.com");

	private final int id;
	private final String name, baseUrl;

	StreamType(int id, String name, String baseUrl){
		this.id = id;
		this.name = name;
		this.baseUrl = baseUrl;
	}

	public static StreamType byId(int id){
		return Arrays.stream(values()).filter(streamType -> streamType.getId() == id).findFirst().orElse(null);
	}

	public int getId(){
		return this.id;
	}

	public static StreamType byName(String name){
		return Arrays.stream(values()).filter(streamType -> streamType.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}

	public String getName(){
		return this.name;
	}

	public String getBaseUrl(){
		return "https://" + this.baseUrl + "/";
	}

}
