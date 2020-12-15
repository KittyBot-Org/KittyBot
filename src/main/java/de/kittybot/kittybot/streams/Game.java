package de.kittybot.kittybot.streams;

import net.dv8tion.jda.api.utils.data.DataObject;

public class Game{

	private final int id;
	private final String name;
	private final String thumbnailUrl;

	public Game(int id, String name, String thumbnailUrl){
		this.id = id;
		this.name = name;
		this.thumbnailUrl = thumbnailUrl;
	}

	public static Game getUnknown(){
		return new Game(0, "unknown", "");
	}

	public static Game fromTwitchJSON(DataObject json){
		return new Game(json.getInt("id"), json.getString("name"), json.getString("box_art_url"));
	}

	public int getId(){
		return this.id;
	}

	public String getName(){
		return this.name;
	}

	public String getThumbnailUrl(){
		return this.thumbnailUrl;
	}

}
