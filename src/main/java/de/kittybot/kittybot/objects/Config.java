package de.kittybot.kittybot.objects;

import net.dv8tion.jda.api.utils.data.DataObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class Config{

	private static final Logger LOG = LoggerFactory.getLogger(Config.class);

	public static String BOT_TOKEN;
	public static String BOT_SECRET;
	public static String BOT_ID;
	public static String SUPPORT_GUILD_ID;
	public static String LOG_CHANNEL_ID;
	public static String INVITE_URL;
	public static String REDIRECT_URL;
	public static String ORIGIN_URL;
	public static String HASTEBIN_URL;
	public static String DISCORD_BOTS_TOKEN;
	public static String DISCORD_BOT_LIST_TOKEN;

	public static String DB_HOST;
	public static String DB_PORT;
	public static String DB_DB;
	public static String DB_USER;
	public static String DB_PASSWORD;

	public static final List<String> ADMIN_IDS = new ArrayList<>();
	public static final List<LavalinkNode> LAVALINK_NODES = new ArrayList<>();

	public static String DEFAULT_PREFIX = ".";

	private Config(){}

	static{
		try{
			var json = DataObject.fromJson(new FileInputStream("config.json"));

			BOT_TOKEN = json.getString("bot_token");
			BOT_SECRET = json.getString("bot_secret");
			BOT_ID = json.getString("bot_id");
			var adminIds = json.getArray("admin_ids");
			for(var i = 0; i < adminIds.length(); i++){
				ADMIN_IDS.add(adminIds.getString(i));
			}
			SUPPORT_GUILD_ID = json.getString("support_guild_id");
			LOG_CHANNEL_ID = json.getString("log_channel_id");
			INVITE_URL = json.getString("invite_url");
			REDIRECT_URL = json.getString("redirect_url");
			ORIGIN_URL = json.getString("origin_url");
			HASTEBIN_URL = json.getString("hastebin_url");
			DISCORD_BOTS_TOKEN = json.getString("discord_bots_token");
			DISCORD_BOT_LIST_TOKEN = json.getString("discord_bot_list_token");

			var db = json.getObject("db");
			DB_HOST = db.getString("host");
			DB_PORT = db.getString("port");
			DB_DB = db.getString("db");
			DB_USER = db.getString("user");
			DB_PASSWORD = db.getString("password");

			var lavalinkNodes = json.getArray("lavalink_nodes");
			for(var i = 0; i < lavalinkNodes.length(); i++){
				var node = lavalinkNodes.getObject(i);
				LAVALINK_NODES.add(new LavalinkNode(node.getString("host"), node.getString("port"), node.getString("password")));
			}
		}
		catch(FileNotFoundException e){
			LOG.error("Error while reading config file", e);
		}
	}

	public static boolean isSet(String setting){
		return setting != null && !setting.isEmpty();
	}

}
