package de.anteiku.kittybot.database;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.utils.Config;
import de.anteiku.kittybot.utils.ReactiveMessage;
import de.anteiku.kittybot.utils.Utils;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Database{

	private static final Logger LOG = LoggerFactory.getLogger(Database.class);
	private static final Map<String, String> commandPrefixes = new HashMap<>();

	private Database(){}

	public static void init(KittyBot main){
		SQL.createTable("guilds");
		SQL.createTable("self_assignable_roles");
		SQL.createTable("commands");
		SQL.createTable("reactive_messages");
		SQL.createTable("user_statistics");
		SQL.createTable("sessions");
		for(Guild guild : main.jda.getGuilds()){
			LOG.debug("Loading Guild: {}...", guild.getName());
			if(!isGuildRegistered(guild)){
				registerGuild(guild);
			}
		}
	}

	private static boolean isGuildRegistered(Guild guild){
		var stmt = SQL.prepStatement("SELECT * FROM guilds WHERE guild_id = ?");
		try{
			stmt.setString(1, guild.getId());
			return SQL.exists(stmt);
		}
		catch(SQLException | NullPointerException e){
			LOG.error("Error while getting warehouse permissions", e);
		}
		return false;
	}


	public static boolean registerGuild(Guild guild){
		LOG.debug("Registering new guild: {}", guild.getId());
		var stmt = SQL.prepStatement("INSERT INTO guilds (guild_id, command_prefix, request_channel_id, requests_enabled, welcome_channel_id, welcome_message, welcome_message_enabled, nsfw_enabled, inactive_role) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
		try{
			stmt.setString(1, guild.getId());
			stmt.setString(2, Config.DEFAULT_PREFIX);
			stmt.setString(3, "-1");
			stmt.setBoolean(4, false);
			stmt.setString(5, guild.getDefaultChannel().getId());
			stmt.setString(6, "Welcome [username] to this server!");
			stmt.setBoolean(7, true);
			stmt.setBoolean(8, true);
			stmt.setBoolean(9, false);
			SQL.execute(stmt);
			return true;
		}
		catch(SQLException e){
			LOG.error("Error registering guild: " + guild.getId(), e);
		}
		return false;
	}

	public boolean addCommandStatistics(String guildId, String commandId, String userId, String command, long processingTime){
		var stmt = SQL.prepStatement("INSERT INTO commands (message_id, guild_id, user_id, command, processing_time, time) VALUES (?, ?, ?, ?, ?, ?)");
		try{
			stmt.setString(1, commandId);
			stmt.setString(2, guildId);
			stmt.setString(3, userId);
			stmt.setString(4, command);
			stmt.setLong(5, processingTime);
			stmt.setLong(6,  System.currentTimeMillis());
			return true;
		}
		catch(SQLException e){
			LOG.error("Error adding command statistics for message: " + commandId, e);
		}
		return false;
	}

	public Map<Long, Long> getCommandStatistics(String guildId, long from, long to){
		Map<Long, Long> map = new LinkedHashMap<>();
		var stmt = SQL.prepStatement("SELECT * FROM commands WHERE guild_id = ? and time > ? and time < ?");
		try{
			stmt.setString(1, guildId);
			stmt.setLong(1, from);
			stmt.setLong(1, to);
			var result = SQL.query(stmt);
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

	public String getCommandPrefix(String guildId){
		String prefix = commandPrefixes.get(guildId);
		if(prefix == null){
			prefix = get(guildId, "command_prefix");
			commandPrefixes.put(guildId, prefix);
		}
		return prefix;
	}

	private String get(String guildId, String key){
		ResultSet result = sql.getProperty("guilds", "guild_id", guildId);
		try{
			if(result.next()){
				return result.getString(key);
			}
		}
		catch(SQLException e){
			LOG.error("Error while getting key " + key + " from guild " + guildId, e);
		}
		return null;
	}

	private Boolean getBoolean(String guildId, String key){
		ResultSet result = sql.getProperty("guilds", "guild_id", guildId);
		try{
			if(result.next()){
				return result.getBoolean(key);
			}
		}
		catch(SQLException e){
			LOG.error("Error while getting key " + key + " from guild " + guildId, e);
		}
		return false;
	}

	public boolean setCommandPrefix(String guildId, String prefix){
		commandPrefixes.put(guildId, prefix);
		return set(guildId, "command_prefix", prefix);
	}

	private boolean set(String guildId, String key, String value){
		return sql.setProperty("guilds", key, value, "guild_id", guildId);
	}

	public boolean addSelfAssignableRole(String guildId, String role, String emote){
		return addSelfAssignableRoles(guildId, new HashMap<>(Collections.singletonMap(role, emote)));
	}

	public boolean addSelfAssignableRoles(String guildId, Map<String, String> roles){
		boolean result = true;
		for(Map.Entry<String, String> role : roles.entrySet()){
			boolean r = sql.execute("INSERT INTO self_assignable_roles (role_id, guild_id, emote_id) VALUES ('" + role.getKey() + "', '" + guildId + "', '" + role.getValue() + "');");
			if(!r){
				result = false;
			}
		}
		return result;
	}

	public void removeSelfAssignableRole(String guildId, String role){
		removeSelfAssignableRoles(guildId, new HashSet<>(Collections.singleton(role)));
	}

	public boolean removeSelfAssignableRoles(String guildId, Set<String> roles){
		boolean result = true;
		for(String role : roles){
			boolean r = sql.execute("DELETE FROM self_assignable_roles WHERE role_id = '" + role + "' and guild_id = '" + guildId + "';");
			if(!r){
				result = false;
			}
		}
		return result;
	}

	public void setSelfAssignableRoles(String guildId, JsonArray newRoles){
		Map<String, String> roles = getSelfAssignableRoles(guildId);
		Map<String, String> addRoles = new HashMap<>();
		Set<String> removeRoles = new HashSet<>();
		for(Map.Entry<String, String> valuePair : roles.entrySet()){
			JsonObject obj = new JsonObject();
			obj.addProperty("role_id", valuePair.getKey());
			obj.addProperty("emote_id", valuePair.getValue());
			if(!newRoles.contains(obj)){
				newRoles.remove(obj);
				removeRoles.add(valuePair.getKey());
			}
		}
		for(JsonElement ele : newRoles){
			JsonObject obj = ele.getAsJsonObject();
			addRoles.put(obj.get("role").getAsString(), obj.get("emote").getAsString());
		}
		removeSelfAssignableRoles(guildId, removeRoles);
		addSelfAssignableRoles(guildId, addRoles);
	}

	public Map<String, String> getSelfAssignableRoles(String guildId){
		Map<String, String> map = new HashMap<>();
		ResultSet result = sql.query("SELECT role_id, emote_id FROM self_assignable_roles WHERE guild_id = '" + guildId + "';");
		try{
			while(result.next()){
				map.put(result.getString("role_id"), result.getString("emote_id"));
			}
			return map;
		}
		catch(SQLException e){
			LOG.error("Error while getting self-assignable roles from guild " + guildId, e);
		}
		return null;
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
		return getBoolean(guildId, "welcome_message_enabled");
	}

	public boolean setWelcomeMessageEnabled(String guildId, boolean enabled){
		return set(guildId, "welcome_message_enabled", enabled ? 1 : 0);
	}

	private boolean set(String guildId, String key, int value){
		return sql.setProperty("guilds", key, value, "guild_id", guildId);
	}

	public boolean getNSFWEnabled(String guildId){
		return getBoolean(guildId, "nsfw_enabled");
	}

	public boolean setNSFWEnabled(String guildId, boolean enabled){
		return set(guildId, "nsfw_enabled", enabled ? 1 : 0);
	}

	public boolean addReactiveMessage(String guildId, String userId, String messageId, String commandId, String command, String allowed){
		return sql.execute("INSERT INTO reactive_messages (message_id, command_id, user_id, guild_id, command, allowed) VALUES ('" + messageId + "', '" + commandId + "', '" + userId + "', '" + guildId + "', '" + command + "', '" + allowed + "');");
	}

	public boolean removeReactiveMessage(String guildId, String messageId){
		return sql.execute("DELETE FROM reactive_messages WHERE message_id = '" + messageId + "' AND guild_id = '" + guildId + "';");
	}

	public ReactiveMessage isReactiveMessage(String guildId, String messageId){
		ResultSet result = sql.query("SELECT * FROM reactive_messages WHERE message_id = '" + messageId + "' AND guild_id = '" + guildId + "';");
		try{
			if(result.next()){
				return new ReactiveMessage(result.getString("message_id"), result.getString("user_id"), result.getString("command_id"), result.getString("command"), result.getString("allowed"));
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
		ResultSet result = sql.query("SELECT joined_voice FROM user_statistics WHERE guild_id = '" + userId + "' and user_id = '" + guildId + "';");
		try{
			return Long.parseLong(result.getString("joined_voice"));
		}
		catch(SQLException e){
			LOG.error("Error while requesting voice state for user " + userId + " in guild: " + guildId, e);
		}
		return -1L;
	}

	public void setUserVoiceState(String guildId, String userId, long joined){
		if(sql.update("UPDATE user_statistics SET joined_voice='" + joined + "' WHERE guild_id = '" + guildId + "' and user_id = '" + userId + "';") == 0){
			//addUserStatistics(guildId, userId);
		}
	}

	/*
	 * Session specified methods
	 */

	public void addSession(String userId, String key){
		sql.execute("INSERT INTO sessions (session_id, user_id) VALUES ('" + key + "', '" + userId + "');");
	}

	public String generateUniqueKey(){
		String key = Utils.generate(32);
		while(sessionExists(key)){
			key = Utils.generate(32);
		}
		return key;
	}

	public boolean sessionExists(String key){
		return sql.exists("SELECT * from sessions WHERE session_id = '" + key + "';");
	}

	public boolean deleteSession(String key){
		return sql.execute("DELETE FROM sessions WHERE session_id = '" + key + "';");
	}

	public String getSession(String key){
		ResultSet result = sql.query("SELECT * from sessions WHERE session_id = '" + key + "';");
		try{
			if(result.next()){
				return result.getString("user_id");
			}
		}
		catch(SQLException e){
			LOG.error("Error while getting session", e);
		}
		return null;
	}

}
