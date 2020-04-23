package de.anteiku.kittybot.database;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.objects.ReactiveMessage;
import de.anteiku.kittybot.objects.ValuePair;
import de.anteiku.kittybot.utils.Utils;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Database{

	private static final Logger LOG = LoggerFactory.getLogger(Database.class);
	private final KittyBot main;
	private final Map<String, String> commandPrefixes;
	public SQL sql;

	public Database(KittyBot main) throws SQLException{
		this.main = main;
		commandPrefixes = new HashMap<>();
		sql = SQL.newInstance(main.MYSQL_HOST, main.MYSQL_PORT, main.MYSQL_USER, main.MYSQL_PASSWORD, main.MYSQL_DB);
		sql.createTable("guilds");
		sql.createTable("self_assignable_roles");
		sql.createTable("commands");
		sql.createTable("reactive_messages");
		sql.createTable("user_statistics");
		//sql.createTable("requests");
		sql.createTable("sessions");
		//sql.execute("CREATE TABLE IF NOT EXISTS `polls` (\n" + "`id` varchar(18) NOT NULL PRIMARY KEY,\n" + "`guild_id` varchar(18) NOT NULL,\n" + "`channel_id` varchar(18) NOT NULL,\n" + "`title` text NOT NULL,\n" + "`created_by` varchar(18) NOT NULL,\n" + "`created_at` timestamp NOT NULL DEFAULT current_timestamp(),\n" + "`goes_until` timestamp NOT NULL DEFAULT current_timestamp()\n" + ")");
		//sql.execute("CREATE TABLE IF NOT EXISTS `poll_votes` (\n" + "`id` varchar(18) NOT NULL PRIMARY KEY,\n" + "`created_by` varchar(18) NOT NULL,\n" + "`created_at` timestamp NOT NULL DEFAULT current_timestamp(),\n" + "`value` varchar(18) NOT NULL\n" + ")");
		//sql.execute("CREATE TABLE IF NOT EXISTS `poll_answers` (\n" + "`id` varchar(18) NOT NULL PRIMARY KEY,\n" + "`answer` text NOT NULL\n" + ")");
	}

	public static Database connect(KittyBot main){
		while(true){
			try{
				return new Database(main);
			}
			catch(SQLException e){
				LOG.error("Could not connect to database...\nRetrying in 5 seconds...", e);
				try{
					Thread.sleep(5000);
				}
				catch(InterruptedException ex){
					LOG.error("Error putting thread to sleep", e);
				}
			}
		}
	}

	public void init(){
		for(Guild guild : main.jda.getGuilds()){
			LOG.debug("Loading Guild: {}...", guild.getName());
			if(!isGuildRegistered(guild)){
				registerGuild(guild);
			}
		}
	}

	public void close(){
		LOG.debug("Closing connection to database...");
		sql.close();
	}

	/*
	 * Command Statistic specified methods
	 */

	public boolean addCommandStatistics(String guildId, String commandId, String userId, String command, long processingTime){
		return sql.execute(
			"INSERT INTO `commands` (id, guild_id, user_id, command, processing_time, time) VALUES ('" + commandId + "', '" + guildId + "', '" + userId + "', '" + command + "', '" + processingTime + "', '" + System.currentTimeMillis() + "')");
	}

	public Map<Long, Long> getCommandStatistics(String guildId, long from, long to){
		Map<Long, Long> map = new LinkedHashMap<>();
		ResultSet result = sql.query(
			"SELECT * FROM `commands` WHERE `guild_id` = '" + guildId + "' and `time` > '" + from + "' and `time` < '" + to + "'");
		try{
			while(result.next()){
				map.put(result.getLong("time"), result.getLong("processing_time"));
			}
			return map;
		}
		catch(SQLException e){
			LOG.error("Error while getting command statistics from guild" + guildId, e);
		}
		return null;
	}


	/*
	 * Guild specified methods
	 */

	private boolean isGuildRegistered(Guild guild){
		return sql.exists("SELECT * FROM `guilds` WHERE `id` = '" + guild.getId() + "'");
	}

	private boolean registerGuild(Guild guild){
		LOG.debug("Registering new guild: {}", guild.getId());
		return sql.execute(
			"INSERT INTO `guilds` (id, command_prefix, request_channel_id, requests_enabled, welcome_channel_id, welcome_message, welcome_message_enabled, nsfw_enabled) VALUES ('" + guild.getId() + "', '" + main.DEFAULT_PREFIX + "', '-1', 0, '" + guild.getDefaultChannel().getId() + "', 'Welcome [username] to this server!', 1, 1)");
	}

	private String get(String guildId, String key){
		ResultSet result = sql.getProperty("guilds", "id", guildId);
		try{
			if(result.absolute(1)){
				return result.getString(key);
			}
		}
		catch(SQLException e){
			LOG.error("Error while getting key " + key + " from guild " + guildId, e);
		}
		return null;
	}

	private boolean set(String guildId, String key, String value){
		return sql.setProperty("guilds", key, value, "id", guildId);
	}

	private boolean set(String guildId, String key, int value){
		return sql.setProperty("guilds", key, value, "id", guildId);
	}

	public String getCommandPrefix(String guildId){
		String prefix = commandPrefixes.get(guildId);
		if(prefix == null){
			prefix = get(guildId, "command_prefix");
			commandPrefixes.put(guildId, prefix);
		}
		return prefix;
	}

	public boolean setCommandPrefix(String guildId, String prefix){
		commandPrefixes.put(guildId, prefix);
		return set(guildId, "command_prefix", prefix);
	}

	public Set<ValuePair<String, String>> getSelfAssignableRoles(String guildId){
		String[] keys = {"id", "guild_id", "emote_id"};
		Set<ValuePair<String, String>> set = new HashSet<>();
		ResultSet result = sql.query("SELECT " + String.join(", ", keys) + " FROM `self_assignable_roles` WHERE `guild_id` = '" + guildId + "'");
		try{
			while(result.next()){
				set.add(new ValuePair<>(result.getString("id"), result.getString("emote_id")));
			}
			return set;
		}
		catch(SQLException e){
			LOG.error("Error while getting self-assignable roles from guild " + guildId, e);
		}
		return null;
	}

	public boolean addSelfAssignableRoles(String guildId, Map<String, String> roles){
		boolean result = true;
		for(Map.Entry<String, String> role : roles.entrySet()){
			boolean r = sql.execute(
				"INSERT INTO `self_assignable_roles` (id, guild_id, emote_id) VALUES ('" + role.getKey() + "', '" + guildId + "', '" + role.getValue() + "')");
			if(!r){
				result = false;
			}
		}
		return result;
	}

	public boolean addSelfAssignableRole(String guildId, String role, String emote){
		return addSelfAssignableRoles(guildId, new HashMap<>(Collections.singletonMap(role, emote)));
	}

	public boolean removeSelfAssignableRoles(String guildId, Set<String> roles){
		boolean result = true;
		for(String role : roles){
			boolean r = sql.execute("DELETE FROM `self_assignable_roles` WHERE `id` = '" + role + "' and `guild_id` = '" + guildId + "'");
			if(!r){
				result = false;
			}
		}
		return result;
	}

	public void removeSelfAssignableRole(String guildId, String role){
		removeSelfAssignableRoles(guildId, new HashSet<>(Collections.singleton(role)));
	}

	public String getWelcomeChannelId(String guildId){
		return get(guildId, "welcome_channel_id");
	}

	public boolean setWelcomeChannelId(String guildId, String channelId){
		return set(guildId, "welcome_channel_id", channelId);
	}

	public String getWelcomeMessage(String guildId){
		return get(guildId, "welcome_message");
	}

	public boolean setWelcomeMessage(String guildId, String message){
		return set(guildId, "welcome_message", message);
	}

	public boolean getWelcomeMessageEnabled(String guildId){
		return Boolean.getBoolean(get(guildId, "welcome_message_enabled"));
	}

	public boolean setWelcomeMessageEnabled(String guildId, boolean enabled){
		return set(guildId, "welcome_message_enabled", enabled ? 1 : 0);
	}

	public boolean getNSFWEnabled(String guildId){
		return Boolean.getBoolean(get(guildId, "nsfw_enabled"));
	}

	public boolean setNSFWEnabled(String guildId, boolean enabled){
		return set(guildId, "nsfw_enabled", enabled ? 1 : 0);
	}

	public boolean addReactiveMessage(String guildId, String userId, String messageId, String commandId, String command, String allowed){
		return sql.execute(
			"INSERT INTO `reactive_messages` (id, command_id, user_id, guild_id, command, allowed) VALUES ('" + messageId + "', '" + commandId + "', '" + userId + "', '" + guildId + "', '" + command + "', '" + allowed + "')");
	}

	public boolean removeReactiveMessage(String guildId, String messageId){
		return sql.execute("DELETE FROM `reactive_messages` WHERE `id` = '" + messageId + "' AND `guild_id` = '" + guildId + "'");
	}

	public ReactiveMessage isReactiveMessage(String guildId, String messageId){
		ResultSet result = sql.query("SELECT * FROM `reactive_messages` WHERE `id` = '" + messageId + "' AND `guild_id` = '" + guildId + "'");
		try{
			if(result.absolute(1)){
				return new ReactiveMessage(
					result.getString("id"), result.getString("user_id"), result.getString("command_id"), result.getString("command"),
					result.getString("allowed")
				);
			}
		}
		catch(SQLException e){
			LOG.error("Error while checking reactive message for guild " + guildId + " message " + messageId, e);
		}
		return null;
	}

	/*
	 * User stats specified methods
	 */

	public long getUserVoiceState(String guildId, String userId){
		ResultSet result = sql.query(
			"SELECT `joined_voice` FROM `user_statistics` WHERE `guild_id` = '" + userId + "' and `user_id` = '" + guildId + "'");
		try{
			return Long.parseLong(result.getString("joined_voice"));
		}
		catch(SQLException e){
			LOG.error("Error while requesting voice state for user " + userId + " in guild: " + guildId, e);
		}
		return -1L;
	}

	public void setUserVoiceState(String guildId, String userId, long joined){
		if(sql.update(
			"UPDATE `user_statistics` SET `joined_voice`='" + joined + "' WHERE `guild_id` = '" + guildId + "' and `user_id` = '" + userId + "'") == 0){
			//addUserStatistics(guildId, userId);
		}
	}

	/*
	 * Session specified methods
	 */

	public boolean sessionExists(String key){
		return sql.exists("SELECT * from `sessions` WHERE `id` = '" + key + "'");
	}

	private String generateUniqueKey(){
		String key = Utils.generate(32);
		while(sessionExists(key)){
			key = Utils.generate(32);
		}
		return key;
	}

	public String addSession(String userId){
		String key = generateUniqueKey();
		sql.query("INSERT INTO `sessions` (id, user_id) VALUES ('" + key + "', '" + userId + "')");
		return key;
	}

	public boolean deleteSession(String key){
		return sql.execute("DELETE FROM `sessions` WHERE `id` = '" + key + "'");
	}

	public String getSession(String key){
		ResultSet result = sql.query("SELECT * from `sessions` WHERE `id` = '" + key + "'");
		try{
			if(result.absolute(1)){
				return result.getString("user_id");
			}
		}
		catch(SQLException e){
			LOG.error("Error while getting session", e);
		}
		return null;
	}

}
