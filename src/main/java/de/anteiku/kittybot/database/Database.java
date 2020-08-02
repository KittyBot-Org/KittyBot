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

import java.sql.PreparedStatement;
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
			LOG.error("Error while checking if guild exists", e);
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

	public static boolean addCommandStatistics(String guildId, String commandId, String userId, String command, long processingTime){
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

	public static Map<Long, Long> getCommandStatistics(String guildId, long from, long to){
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

	public static String getCommandPrefix(String guildId){
		String prefix = commandPrefixes.get(guildId);
		if(prefix == null){
			prefix = getString(guildId, "command_prefix");
			commandPrefixes.put(guildId, prefix);
		}
		return prefix;
	}

	private static String getString(String guildId, String key){
		var stmt = SQL.prepStatement("SELECT * FROM guilds WHERE guild_id = ?");
		try{
			stmt.setString(1, guildId);
			var result = SQL.query(stmt);
			if(result.next()){
				return result.getString(key);
			}
		}
		catch(SQLException e){
			LOG.error("Error while getting key " + key + " from guild " + guildId, e);
		}
		return null;
	}

	private static boolean getBoolean(String guildId, String key){
		var stmt = SQL.prepStatement("SELECT * FROM guilds WHERE guild_id = ?");
		try{
			stmt.setString(1, guildId);
			var result = SQL.query(stmt);
			if(result.next()){
				return result.getBoolean(key);
			}
		}
		catch(SQLException e){
			LOG.error("Error while getting key " + key + " from guild " + guildId, e);
		}
		return false;
	}

	private static boolean setProperty(String guildId, String key, String value){
		var stmt = SQL.prepStatement("UPDATE guilds SET ?=? WHERE guild_id = ?");
		try{
			stmt.setString(1, key);
			stmt.setString(2, value);
			stmt.setString(3, guildId);
			return SQL.execute(stmt);
		}
		catch(SQLException e){
			LOG.error("Error while getting key " + key + " from guild " + guildId, e);
		}
		return false;
	}

	private static boolean setProperty(String guildId, String key, int value){
		var stmt = SQL.prepStatement("UPDATE guilds SET " + key + "=? WHERE guild_id = ?");
		try{
			stmt.setInt(1, value);
			stmt.setString(2, guildId);
			return SQL.execute(stmt);
		}
		catch(SQLException e){
			LOG.error("Error while getting key " + key + " from guild " + guildId, e);
		}
		return false;
	}

	private static boolean setProperty(String guildId, String key, boolean value){
		var stmt = SQL.prepStatement("UPDATE guilds SET " + key + "=? WHERE guild_id = ?");
		try{
			stmt.setBoolean(1, value);
			stmt.setString(2, guildId);
			return SQL.execute(stmt);
		}
		catch(SQLException e){
			LOG.error("Error while getting key " + key + " from guild " + guildId, e);
		}
		return false;
	}

	public static boolean setCommandPrefix(String guildId, String prefix){
		commandPrefixes.put(guildId, prefix);
		return setProperty(guildId, "command_prefix", prefix);
	}

	public boolean addSelfAssignableRole(String guildId, String role, String emote){
		return addSelfAssignableRoles(guildId, new HashMap<>(Collections.singletonMap(role, emote)));
	}

	public static boolean addSelfAssignableRoles(String guildId, Map<String, String> roles){
		boolean result = true;
		for(Map.Entry<String, String> role : roles.entrySet()){
			var stmt = SQL.prepStatement("INSERT INTO self_assignable_roles (role_id, guild_id, emote_id) VALUES (?, ?, ?)");
			try {
				stmt.setString(1, role.getKey());
				stmt.setString(2, guildId);
				stmt.setString(3, role.getKey());
				boolean r = SQL.execute(stmt);
				if(!r){
					result = false;
				}
			}
			catch(SQLException e){
				LOG.error("Error inserting self-assignable role", e);
			}
		}
		return result;
	}

	public void removeSelfAssignableRole(String guildId, String role){
		removeSelfAssignableRoles(guildId, new HashSet<>(Collections.singleton(role)));
	}

	public static boolean removeSelfAssignableRoles(String guildId, Set<String> roles){
		boolean result = true;
		for(String role : roles){
			var stmt = SQL.prepStatement("DELETE FROM self_assignable_roles WHERE role_id = ? and guild_id = ?");
			try {
				stmt.setString(1, role);
				stmt.setString(2, guildId);
				boolean r = SQL.execute(stmt);
				if(!r){
					result = false;
				}
			}
			catch(SQLException e){
				LOG.error("Error removing self-assignable role: " + role + " guild " + guildId, e);
			}
		}
		return result;
	}

	public static void setSelfAssignableRoles(String guildId, JsonArray newRoles){
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

	public static Map<String, String> getSelfAssignableRoles(String guildId){
		Map<String, String> map = new HashMap<>();
		var stmt = SQL.prepStatement("SELECT role_id, emote_id FROM self_assignable_roles WHERE guild_id = ?");
		try{
			stmt.setString(1, guildId);
			ResultSet result = SQL.query(stmt);
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

	public static String getWelcomeChannelId(String guildId){
		return getString(guildId, "welcome_channel_id");
	}

	public static boolean setWelcomeChannelId(String guildId, String channelId){
		return setProperty(guildId, "welcome_channel_id", channelId);
	}

	public static String getWelcomeMessage(String guildId){
		return getString(guildId, "welcome_message");
	}

	public static boolean setWelcomeMessage(String guildId, String message){
		return setProperty(guildId, "welcome_message", message);
	}

	public static boolean getWelcomeMessageEnabled(String guildId){
		return getBoolean(guildId, "welcome_message_enabled");
	}

	public static boolean setWelcomeMessageEnabled(String guildId, boolean enabled){
		return setProperty(guildId, "welcome_message_enabled", enabled);
	}

	public static boolean getNSFWEnabled(String guildId){
		return getBoolean(guildId, "nsfw_enabled");
	}

	public static boolean setNSFWEnabled(String guildId, boolean enabled){
		return setProperty(guildId, "nsfw_enabled", enabled);
	}

	public static boolean addReactiveMessage(String guildId, String userId, String messageId, String commandId, String command, String allowed){
		var stmt = SQL.prepStatement("INSERT INTO reactive_messages (message_id, command_id, user_id, guild_id, command, allowed) VALUES (?, ?, ?, ?, ?, ?)");
		try {
			stmt.setString(1, messageId);
			stmt.setString(2, commandId);
			stmt.setString(3, userId);
			stmt.setString(4, guildId);
			stmt.setString(5, command);
			stmt.setString(6, allowed);
			return SQL.execute(stmt);
		}
		catch(SQLException e){
			LOG.error("Error creating reactive message", e);
		}
		return false;
	}

	public static boolean removeReactiveMessage(String guildId, String messageId){
		var stmt = SQL.prepStatement("DELETE FROM reactive_messages WHERE message_id = ? AND guild_id = ?");
		try {
			stmt.setString(1, messageId);
			stmt.setString(2, guildId);
			SQL.execute(stmt);
		}
		catch(SQLException e){
			LOG.error("Error removing reactive message", e);
		}
		return false;
	}

	public static ReactiveMessage isReactiveMessage(String guildId, String messageId){
		var stmt = SQL.prepStatement("SELECT * FROM reactive_messages WHERE message_id = ? AND guild_id = ?");
		try{
			stmt.setString(1, messageId);
			stmt.setString(2, guildId);
			var result = SQL.query(stmt);
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

	public static long getUserVoiceState(String guildId, String userId){
		var stmt = SQL.prepStatement("SELECT joined_voice FROM user_statistics WHERE guild_id = ? and user_id = ?");
		try{
			var result = SQL.query(stmt);
			if(result != null){
				return result.getLong("joined_voice");
			}
		}
		catch(SQLException e){
			LOG.error("Error while requesting voice state for user " + userId + " in guild: " + guildId, e);
		}
		return -1L;
	}

	public static void setUserVoiceState(String guildId, String userId, long joined){
		var stmt = SQL.prepStatement("UPDATE user_statistics SET joined_voice=? WHERE guild_id = ? and user_id = ?");
		try {
			stmt.setLong(1, joined);
			stmt.setString(2,guildId );
			stmt.setString(3, userId);
			if(SQL.update(stmt) == 0){
				//addUserStatistics(guildId, userId);
			}
		}
		catch (SQLException e) {
			LOG.error("Error updating voice state for user " + userId + " in guild: " + guildId, e);
		}
	}

	/*
	 * Session specified methods
	 */

	public static void addSession(String userId, String key){
		var stmt = SQL.prepStatement("INSERT INTO sessions (session_id, user_id) VALUES (?, ?)");
		try {
			stmt.setString(1, key);
			stmt.setString(2, userId);
			SQL.execute(stmt);
		}
		catch (SQLException e) {
			LOG.error("Error adding session for user " + userId, e);
		}
	}

	public static String generateUniqueKey(){
		String key = Utils.generate(32);
		while(sessionExists(key)){
			key = Utils.generate(32);
		}
		return key;
	}

	public static boolean sessionExists(String key){
		var stmt = SQL.prepStatement("SELECT * from sessions WHERE session_id = ?");
		try {
			stmt.setString(1, key);
			return SQL.exists(stmt);
		}
		catch (SQLException e){
			LOG.error("Error checking if session exists", e);
		}
		return false;
	}

	public static boolean deleteSession(String key){
		var stmt = SQL.prepStatement("DELETE FROM sessions WHERE session_id = ?");
		try {
			stmt.setString(1, key);
			return SQL.execute(stmt);
		}
		catch (SQLException e) {
			LOG.error("Error deleting session", e);
		}
		return false;
	}

	public static String getSession(String key){
		var stmt = SQL.prepStatement("SELECT * from sessions WHERE session_id = ?");
		try{
			stmt.setString(1, key);
			var result = SQL.query(stmt);
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
