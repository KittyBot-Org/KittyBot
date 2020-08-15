package de.anteiku.kittybot.database;

import de.anteiku.kittybot.Utils;
import de.anteiku.kittybot.objects.Config;
import de.anteiku.kittybot.objects.ReactiveMessage;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Database{

	private static final Logger LOG = LoggerFactory.getLogger(Database.class);

	private Database(){}

	public static void init(JDA jda){
		SQL.createTable("guilds");
		SQL.createTable("self_assignable_roles");
		SQL.createTable("commands");
		SQL.createTable("reactive_messages");
		SQL.createTable("user_statistics");
		SQL.createTable("sessions");
		for(Guild guild : jda.getGuilds()){
			LOG.debug("Loading Guild: {}...", guild.getName());
			if(!isGuildRegistered(guild)){
				registerGuild(guild);
			}
		}
	}


	private static boolean isGuildRegistered(Guild guild){
		var query = "SELECT * FROM guilds WHERE guild_id = ?";
		try(var con = SQL.getConnection(); var stmt = con.prepareStatement(query)){
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
		var query = "INSERT INTO guilds (guild_id, " +
				"command_prefix, " +
				"request_channel_id, " +
				"requests_enabled, " +
				"announcement_channel_id, " +
				"join_messages, " +
				"join_messages_enabled, " +
				"leave_messages, " +
				"leave_messages_enabled, " +
				"boost_messages, " +
				"boost_messages_enabled, " +
				"nsfw_enabled, " +
				"inactive_role) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try(var con = SQL.getConnection(); var stmt = con.prepareStatement(query)){
			stmt.setString(1, guild.getId());
			stmt.setString(2, Config.DEFAULT_PREFIX);
			stmt.setString(3, "-1");
			stmt.setBoolean(4, false);
			stmt.setString(5, guild.getDefaultChannel() == null ? "-1" : guild.getDefaultChannel().getId());
			stmt.setString(6, "Welcome ${user} to this server!");
			stmt.setBoolean(7, true);
			stmt.setString(8, "Good bye ${user}(${user_tag})!");
			stmt.setBoolean(9, true);
			stmt.setString(10, "${user} boosted this server!");
			stmt.setBoolean(11, true);
			stmt.setBoolean(12, true);
			stmt.setBoolean(13, false);
			SQL.execute(stmt);
			return true;
		}
		catch(SQLException e){
			LOG.error("Error registering guild: " + guild.getId(), e);
		}
		return false;
	}


	private static boolean setProperty(String guildId, String key, int value){
		var query = "UPDATE guilds SET " + key + "=? WHERE guild_id = ?";
		try(var con = SQL.getConnection(); var stmt = con.prepareStatement(query)){
			stmt.setInt(1, value);
			stmt.setString(2, guildId);
			return SQL.execute(stmt);
		}
		catch(SQLException e){
			LOG.error("Error while getting key " + key + " from guild " + guildId, e);
		}
		return false;
	}

	public static boolean addCommandStatistics(String guildId, String commandId, String userId, String command, long processingTime){
		var query = "INSERT INTO commands (message_id, guild_id, user_id, command, processing_time, time) VALUES (?, ?, ?, ?, ?, ?)";
		try(var con = SQL.getConnection(); var stmt = con.prepareStatement(query)){
			stmt.setString(1, commandId);
			stmt.setString(2, guildId);
			stmt.setString(3, userId);
			stmt.setString(4, command);
			stmt.setLong(5, processingTime);
			stmt.setLong(6, System.currentTimeMillis());
			return true;
		}
		catch(SQLException e){
			LOG.error("Error adding command statistics for message: " + commandId, e);
		}
		return false;
	}

	public static Map<Long, Long> getCommandStatistics(String guildId, long from, long to){
		Map<Long, Long> map = new LinkedHashMap<>();
		var query = "SELECT * FROM commands WHERE guild_id = ? and time > ? and time < ?";
		try(var con = SQL.getConnection(); var stmt = con.prepareStatement(query)){
			stmt.setString(1, guildId);
			stmt.setLong(1, from);
			stmt.setLong(1, to);
			var result = SQL.query(stmt);
			while(result != null && result.next()){
				map.put(result.getLong("time"), result.getLong("processing_time"));
			}
			return map;
		}
		catch(SQLException e){
			LOG.error("Error while getting command statistics from guild" + guildId, e);
		}
		return null;
	}

	public static boolean setCommandPrefix(String guildId, String prefix){
		return setProperty(guildId, "command_prefix", prefix);
	}

	private static boolean setProperty(String guildId, String key, String value){
		var query = "UPDATE guilds SET " + key + "=? WHERE guild_id = ?";
		try(var con = SQL.getConnection(); var stmt = con.prepareStatement(query)){
			stmt.setString(1, value);
			stmt.setString(2, guildId);
			return SQL.execute(stmt);
		}
		catch(SQLException e){
			LOG.error("Error while getting key " + key + " from guild " + guildId, e);
		}
		return false;
	}

	public static String getCommandPrefix(String guildId){
		return getString(guildId, "command_prefix");
	}

	private static String getString(String guildId, String key){
		var query = "SELECT * FROM guilds WHERE guild_id = ?";
		try(var con = SQL.getConnection(); var stmt = con.prepareStatement(query)){
			stmt.setString(1, guildId);
			var result = SQL.query(stmt);
			if(result != null && result.next()){
				return result.getString(key);
			}
		}
		catch(SQLException e){
			LOG.error("Error while getting key " + key + " from guild " + guildId, e);
		}
		return null;
	}

	public static void setSelfAssignableRoles(String guildId, Map<String, String> newRoles){
		Map<String, String> roles = getSelfAssignableRoles(guildId);
		if(roles != null){
			Map<String, String> addRoles = new HashMap<>();
			Set<String> removeRoles = new HashSet<>();
			for(Map.Entry<String, String> role : roles.entrySet()){
				if(newRoles.get(role.getKey()) == null){
					removeRoles.add(role.getKey());
				}
			}
			for(Map.Entry<String, String> role : newRoles.entrySet()){
				if(roles.get(role.getKey()) == null){
					addRoles.put(role.getKey(), role.getValue());
				}
			}
			removeSelfAssignableRoles(guildId, removeRoles);
			addSelfAssignableRoles(guildId, addRoles);
		}
	}

	public static Map<String, String> getSelfAssignableRoles(String guildId){
		Map<String, String> map = new HashMap<>();
		var query = "SELECT * FROM self_assignable_roles WHERE guild_id = ?";
		try(var con = SQL.getConnection(); var stmt = con.prepareStatement(query)){
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

	public static boolean removeSelfAssignableRoles(String guildId, Set<String> roles){
		boolean result = true;
		for(String role : roles){
			var query = "DELETE FROM self_assignable_roles WHERE role_id = ? and guild_id = ?";
			try(var con = SQL.getConnection(); var stmt = con.prepareStatement(query)){
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

	public static boolean addSelfAssignableRoles(String guildId, Map<String, String> roles){
		boolean result = true;
		for(Map.Entry<String, String> role : roles.entrySet()){
			var query = "INSERT INTO self_assignable_roles (role_id, guild_id, emote_id) VALUES (?, ?, ?)";
			try(var con = SQL.getConnection(); var stmt = con.prepareStatement(query)){
				stmt.setString(1, role.getKey());
				stmt.setString(2, guildId);
				stmt.setString(3, role.getValue());
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

	public static boolean isSelfAssignableRole(String guildId, String roleId){
		var query = "SELECT * FROM self_assignable_roles WHERE guild_id = ? and role_id = ?";
		try(var con = SQL.getConnection(); var stmt = con.prepareStatement(query)){
			stmt.setString(1, guildId);
			stmt.setString(2, roleId);
			ResultSet result = SQL.query(stmt);
			return result != null && result.next();
		}
		catch(SQLException e){
			LOG.error("Error while testing if role self-assignable", e);
		}
		return false;
	}

	public static String getAnnouncementChannelId(String guildId){
		return getString(guildId, "announcement_channel_id");
	}

	public static boolean setAnnouncementChannelId(String guildId, String channelId){
		return setProperty(guildId, "announcement_channel_id", channelId);
	}

	public static String getJoinMessage(String guildId){
		return getString(guildId, "join_messages");
	}

	public static boolean setJoinMessage(String guildId, String message){
		return setProperty(guildId, "join_messages", message);
	}

	public static boolean getJoinMessageEnabled(String guildId){
		return getBoolean(guildId, "join_messages_enabled");
	}

	private static boolean getBoolean(String guildId, String key){
		var query = "SELECT * FROM guilds WHERE guild_id = ?";
		try(var con = SQL.getConnection(); var stmt = con.prepareStatement(query)){
			stmt.setString(1, guildId);
			var result = SQL.query(stmt);
			if(result != null && result.next()){
				return result.getBoolean(key);
			}
		}
		catch(SQLException e){
			LOG.error("Error while getting key " + key + " from guild " + guildId, e);
		}
		return false;
	}

	public static boolean setJoinMessageEnabled(String guildId, boolean enabled){
		return setProperty(guildId, "join_messages_enabled", enabled);
	}

	private static boolean setProperty(String guildId, String key, boolean value){
		var query = "UPDATE guilds SET " + key + "=? WHERE guild_id = ?";
		try(var con = SQL.getConnection(); var stmt = con.prepareStatement(query)){
			stmt.setBoolean(1, value);
			stmt.setString(2, guildId);
			return SQL.execute(stmt);
		}
		catch(SQLException e){
			LOG.error("Error while getting key " + key + " from guild " + guildId, e);
		}
		return false;
	}

	public static String getLeaveMessage(String guildId){
		return getString(guildId, "leave_messages");
	}

	public static boolean setLeaveMessage(String guildId, String message){
		return setProperty(guildId, "leave_messages", message);
	}

	public static boolean getLeaveMessageEnabled(String guildId){
		return getBoolean(guildId, "leave_messages_enabled");
	}

	public static boolean setLeaveMessageEnabled(String guildId, boolean enabled){
		return setProperty(guildId, "leave_messages_enabled", enabled);
	}

	public static String getBoostMessage(String guildId){
		return getString(guildId, "boost_messages");
	}

	public static boolean setBoostMessage(String guildId, String message){
		return setProperty(guildId, "boost_messages", message);
	}

	public static boolean getBoostMessageEnabled(String guildId){
		return getBoolean(guildId, "boost_messages_enabled");
	}

	public static boolean setBoostMessageEnabled(String guildId, boolean enabled){
		return setProperty(guildId, "boost_messages_enabled", enabled);
	}

	public static boolean getNSFWEnabled(String guildId){
		return getBoolean(guildId, "nsfw_enabled");
	}

	public static boolean setNSFWEnabled(String guildId, boolean enabled){
		return setProperty(guildId, "nsfw_enabled", enabled);
	}

	public static ReactiveMessage isReactiveMessage(String guildId, String messageId){
		var query = "SELECT * FROM reactive_messages WHERE message_id = ? AND guild_id = ?";
		try(var con = SQL.getConnection(); var stmt = con.prepareStatement(query)){
			stmt.setString(1, messageId);
			stmt.setString(2, guildId);
			var result = SQL.query(stmt);
			if(result != null && result.next()){
				return new ReactiveMessage(result.getString("channel_id"), result.getString("message_id"), result.getString("user_id"), result.getString("command_id"), result.getString("command"), result.getString("allowed"));
			}
		}
		catch(SQLException e){
			LOG.error("Error while checking reactive message for guild " + guildId + " message " + messageId, e);
		}
		return null;
	}

	public static boolean addReactiveMessage(String guildId, String userId, String channelId, String messageId, String commandId, String command, String allowed){
		var query = "INSERT INTO reactive_messages (channel_id, message_id, command_id, user_id, guild_id, command, allowed) VALUES (?, ?, ?, ?, ?, ?, ?)";
		try(var con = SQL.getConnection(); var stmt = con.prepareStatement(query)){
			stmt.setString(1, channelId);
			stmt.setString(2, messageId);
			stmt.setString(3, commandId);
			stmt.setString(4, userId);
			stmt.setString(5, guildId);
			stmt.setString(6, command);
			stmt.setString(7, allowed);
			return SQL.execute(stmt);
		}
		catch(SQLException e){
			LOG.error("Error creating reactive message", e);
		}
		return false;
	}

	public static boolean removeReactiveMessage(String guildId, String messageId){
		var query = "DELETE FROM reactive_messages WHERE message_id = ? AND guild_id = ?";
		try(var con = SQL.getConnection(); var stmt = con.prepareStatement(query)){
			stmt.setString(1, messageId);
			stmt.setString(2, guildId);
			SQL.execute(stmt);
		}
		catch(SQLException e){
			LOG.error("Error removing reactive message", e);
		}
		return false;
	}

	public static long getUserVoiceState(String guildId, String userId){
		var query = "SELECT joined_voice FROM user_statistics WHERE guild_id = ? and user_id = ?";
		try(var con = SQL.getConnection(); var stmt = con.prepareStatement(query)){
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
		var query = "UPDATE user_statistics SET joined_voice=? WHERE guild_id = ? and user_id = ?";
		try(var con = SQL.getConnection(); var stmt = con.prepareStatement(query)){
			stmt.setLong(1, joined);
			stmt.setString(2, guildId);
			stmt.setString(3, userId);
			if(SQL.update(stmt) == 0){
				//addUserStatistics(guildId, userId);
			}
		}
		catch(SQLException e){
			LOG.error("Error updating voice state for user " + userId + " in guild: " + guildId, e);
		}
	}

	public static void addSession(String userId, String key){
		var query = "INSERT INTO sessions (session_id, user_id) VALUES (?, ?)";
		try(var con = SQL.getConnection(); var stmt = con.prepareStatement(query)){
			stmt.setString(1, key);
			stmt.setString(2, userId);
			SQL.execute(stmt);
		}
		catch(SQLException e){
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
		var query = "SELECT * from sessions WHERE session_id = ?";
		try(var con = SQL.getConnection(); var stmt = con.prepareStatement(query)){
			stmt.setString(1, key);
			return SQL.exists(stmt);
		}
		catch(SQLException e){
			LOG.error("Error checking if session exists", e);
		}
		return false;
	}

	public static boolean deleteSession(String key){
		var query = "DELETE FROM sessions WHERE session_id = ?";
		try(var con = SQL.getConnection(); var stmt = con.prepareStatement(query)){
			stmt.setString(1, key);
			return SQL.execute(stmt);
		}
		catch(SQLException e){
			LOG.error("Error deleting session", e);
		}
		return false;
	}

	public static String getSession(String key){
		var query = "SELECT * from sessions WHERE session_id = ?";
		try(var con = SQL.getConnection(); var stmt = con.prepareStatement(query)){
			stmt.setString(1, key);
			var result = SQL.query(stmt);
			if(result != null && result.next()){
				return result.getString("user_id");
			}
		}
		catch(SQLException e){
			LOG.error("Error while getting session", e);
		}
		return null;
	}

	public boolean addSelfAssignableRole(String guildId, String role, String emote){
		return addSelfAssignableRoles(guildId, new HashMap<>(Collections.singletonMap(role, emote)));
	}

	public void removeSelfAssignableRole(String guildId, String role){
		removeSelfAssignableRoles(guildId, new HashSet<>(Collections.singleton(role)));
	}

}
