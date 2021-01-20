package de.kittybot.kittybot.objects.streams;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Game{

	private final int id;
	private final String name;

	public Game(int id, String name){
		this.id = id;
		this.name = name;
	}

	public static Game getUnknown(){
		return new Game(0, "unknown");
	}

	public int getId(){
		return this.id;
	}

	public String getName(){
		return this.name;
	}

	public String getThumbnailUrl(int width, int height){
		// WHATA FUCK GIMME %20 INSTEAD OF +
		return "https://static-cdn.jtvnw.net/ttv-boxart/" + URLEncoder.encode(this.name, StandardCharsets.UTF_8).replace("+", "%20") + "-" + width + "x" + height + ".jpg?v=" + System.currentTimeMillis();
	}

}
