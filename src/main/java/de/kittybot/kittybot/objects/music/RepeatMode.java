package de.kittybot.kittybot.objects.music;

public enum RepeatMode{

	OFF,
	QUEUE,
	SONG;

	public String getName(){
		return name().toLowerCase();
	}
}
