package de.kittybot.kittybot.streams;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Game{

	private static final Logger LOG = LoggerFactory.getLogger(Game.class);


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
		try{
			// WHATA FUCK GIMME %20 INSTEAD OF +
			return "https://static-cdn.jtvnw.net/ttv-boxart/" + URLEncoder.encode(this.name, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20") + "-" + width + "x" + height + ".jpg";
		}
		catch(UnsupportedEncodingException e){
			LOG.error("WTF IS THIS SHIT", e);
		}
		return null;
	}

}
