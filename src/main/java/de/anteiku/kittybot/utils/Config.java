package de.anteiku.kittybot.utils;

import de.anteiku.kittybot.objects.LavalinkNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Config{
	
	private static final Logger LOG = LoggerFactory.getLogger(Config.class);
	
	public static String DISCORD_BOT_TOKEN;
	public static String DISCORD_BOT_SECRET;
	public static String DISCORD_BOT_ID;
	public static String DISCORD_ADMIN_ID;
	public static String DISCORD_INVITE_LINK;
	
	public static String DB_HOST;
	public static String DB_PORT;
	public static String DB_DB;
	public static String DB_USER;
	public static String DB_PASSWORD;
	
	protected static List<LavalinkNode> LAVALINK_NODES;
	
	public static String DEFAULT_PREFIX = ".";
	
	public static void load(String filePath){
		Yaml yaml = new Yaml();
		try{
			Map<String, Object> config = yaml.load(new FileInputStream(new File(filePath)));
			
			Map<String, Object> discord = (Map<String, Object>) config.get("discord");
			DISCORD_BOT_TOKEN = String.valueOf(discord.get("bot_token"));
			DISCORD_BOT_SECRET = String.valueOf(discord.get("bot_secret"));
			DISCORD_BOT_ID = String.valueOf(discord.get("bot_id"));
			DISCORD_ADMIN_ID = String.valueOf(discord.get("admin_id"));
			DISCORD_INVITE_LINK = String.valueOf(discord.get("invite_link"));
			
			Map<String, Object> postgres = (Map<String, Object>) config.get("postgres");
			DB_HOST = String.valueOf(postgres.get("host"));
			DB_PORT = String.valueOf(postgres.get("port"));
			DB_DB = String.valueOf(postgres.get("database"));
			DB_USER = String.valueOf(postgres.get("user"));
			DB_PASSWORD = String.valueOf(postgres.get("password"));
			
			LAVALINK_NODES = new ArrayList<>();
			for(Map.Entry<String, Object> entry : ((Map<String, Object>) config.get("lavalink_nodes")).entrySet()){
				Map<String, Object> node = (Map<String, Object>) entry.getValue();
				LAVALINK_NODES.add(new LavalinkNode(String.valueOf(node.get("host")), String.valueOf(node.get("port")), String.valueOf(node.get("password"))));
			}
		}
		catch(FileNotFoundException e){
			LOG.error("Error while reading config file", e);
		}
	}
	
}
