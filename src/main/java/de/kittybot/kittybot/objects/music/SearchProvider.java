package de.kittybot.kittybot.objects.music;

import java.util.Arrays;

public enum SearchProvider{

	YOUTUBE("youtube", "yt"),
	SPOTIFY("spotify", "sp"),
	SOUNDCLOUD("soundcloud", "sc");

	private final String name, shortName;

	SearchProvider(String name, String shortName){
		this.name = name;
		this.shortName = shortName;
	}

	public static SearchProvider getByShortname(String shortName){
		return Arrays.stream(values()).filter(searchProvider -> searchProvider.shortName.equalsIgnoreCase(shortName)).findFirst().orElse(null);
	}

	public String getName(){
		return this.name;
	}

	public String getShortName(){
		return this.shortName;
	}

}
