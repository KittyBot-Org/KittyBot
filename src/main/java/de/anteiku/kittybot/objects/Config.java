package de.anteiku.kittybot.objects;

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

	public static String BOT_TOKEN;
	public static String BOT_SECRET;
	public static String BOT_ID;
	public static String ADMIN_ID;
	public static String SUPPORT_GUILD;
	public static String LOG_CHANNEL;
	public static String INVITE_LINK;
	public static String REDIRECT_URL;
	public static String ORIGIN_URL;

	public static String DB_HOST;
	public static String DB_PORT;
	public static String DB_DB;
	public static String DB_USER;
	public static String DB_PASSWORD;

	public static List<LavalinkNode> LAVALINK_NODES;

	public static String DEFAULT_PREFIX = ".";

	public static void load(String filePath){
		Yaml yaml = new Yaml();
		try{
			Map<String, Object> config = yaml.load(new FileInputStream(new File(filePath)));

			BOT_TOKEN = String.valueOf(config.get("bot_token"));
			BOT_SECRET = String.valueOf(config.get("bot_secret"));
			BOT_ID = String.valueOf(config.get("bot_id"));
			ADMIN_ID = String.valueOf(config.get("admin_id"));
			SUPPORT_GUILD = String.valueOf(config.get("support_guild"));
			LOG_CHANNEL = String.valueOf(config.get("log_channel"));
			INVITE_LINK = String.valueOf(config.get("invite_link"));
			REDIRECT_URL = String.valueOf(config.get("redirect_url"));
			ORIGIN_URL = String.valueOf(config.get("origin_url"));

			DB_HOST = String.valueOf(config.get("db_host"));
			DB_PORT = String.valueOf(config.get("db_port"));
			DB_DB = String.valueOf(config.get("db_db"));
			DB_USER = String.valueOf(config.get("db_user"));
			DB_PASSWORD = String.valueOf(config.get("db_password"));

			LAVALINK_NODES = new ArrayList<>();
			for(Map.Entry<?, ?> entry : ((Map<?, ?>) config.get("lavalink_nodes")).entrySet()) {
				var node = (Map<?, ?>) entry.getValue();
				LAVALINK_NODES.add(new LavalinkNode(String.valueOf(node.get("host")), String.valueOf(node.get("port")), String.valueOf(node.get("password"))));
			}

		}
		catch(FileNotFoundException e){
			LOG.error("Error while reading config file", e);
		}
	}

}
