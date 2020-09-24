package de.kittybot.kittybot.database;

import de.kittybot.kittybot.database.jooq.tables.records.GuildsRecord;
import de.kittybot.kittybot.objects.Config;
import de.kittybot.kittybot.objects.ReactiveMessage;
import de.kittybot.kittybot.objects.cache.SelfAssignableRoleCache;
import de.kittybot.kittybot.utils.Utils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.jooq.Field;
import org.jooq.TableField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static de.kittybot.kittybot.database.jooq.Tables.*;

public class Database{

	private static final Logger LOG = LoggerFactory.getLogger(Database.class);

	private Database(){
	}

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
		try(var ctx = SQL.getCtx()){
			var res = ctx.selectFrom(GUILDS).where(GUILDS.GUILD_ID.eq(guild.getId())).limit(1).fetch();
			return res.isNotEmpty();
		}
		catch(SQLException | NullPointerException e){
			LOG.error("Error while checking if guild exists", e);
		}
		return false;
	}

	public static void registerGuild(Guild guild){
		LOG.debug("Registering new guild: {}", guild.getId());
		try(var ctx = SQL.getCtx()){
			ctx.insertInto(GUILDS)
					.columns(GUILDS.fields())
					.values(
							guild.getId(),
							Config.DEFAULT_PREFIX,
							"-1",
							false,
							guild.getDefaultChannel() == null ? "-1" : guild.getDefaultChannel().getId(),
							"Welcome ${user} to this server!",
							true,
							"Goodbye ${user}(${user_tag})!",
							true,
							"${user} boosted this server!",
							true,
							"-1",
							false,
							true,
							"-1"
					)
					.execute();
		}
		catch(SQLException e){
			LOG.error("Error registering guild: " + guild.getId(), e);
		}
	}



	public static void addCommandStatistics(String guildId, String commandId, String userId, String command, long processingTime){
		try(var ctx = SQL.getCtx()){
			ctx.insertInto(COMMANDS).columns(COMMANDS.fields()).values(commandId, guildId, userId, command, processingTime, Instant.now().getEpochSecond()).executeAsync();
		}
		catch(SQLException e){
			LOG.error("Error adding command statistics for message: " + commandId, e);
		}
	}

	public static void setCommandPrefix(String guildId, String prefix){
		SQL.setProperty(guildId, GUILDS.COMMAND_PREFIX, prefix);
	}

	public static String getCommandPrefix(String guildId){
		return SQL.getProperty(guildId, GUILDS.COMMAND_PREFIX);
	}

	public static void setSelfAssignableRoles(String guildId, Map<String, String> newRoles){
		var roles = SelfAssignableRoleCache.getSelfAssignableRoles(guildId);
		if(roles != null){
			var addRoles = new HashMap<String, String>();
			var removeRoles = new HashSet<String>();
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
			if(!removeRoles.isEmpty()){
				removeSelfAssignableRoles(guildId, removeRoles);
			}
			if(!addRoles.isEmpty()){
				addSelfAssignableRoles(guildId, addRoles);
			}
		}
	}

	public static boolean OldremoveSelfAssignableRoles(String guildId, Set<String> roles){
		boolean result = true;
		for(String role : roles){
			try(var ctx = SQL.getCtx()){
				var res = ctx.deleteFrom(SELF_ASSIGNABLE_ROLES).where(SELF_ASSIGNABLE_ROLES.ROLE_ID.eq(role).and(SELF_ASSIGNABLE_ROLES.GUILD_ID.eq(guildId))).execute();
				if(res != 1){
					result = false;
				}
			}
			catch(SQLException e){
				LOG.error("Error removing self-assignable role: " + role + " guild " + guildId, e);
			}
		}
		return result;
	}

	public static boolean removeSelfAssignableRoles(String guildId, Set<String> roles){
		try(var ctx = SQL.getCtx()){
			return ctx.deleteFrom(SELF_ASSIGNABLE_ROLES).where(SELF_ASSIGNABLE_ROLES.GUILD_ID.eq(guildId).and(SELF_ASSIGNABLE_ROLES.ROLE_ID.in(roles))).execute() != roles.size();
		}
		catch(SQLException e){
			LOG.error("Error removing self-assignable roles: " + roles.toString() + " guild " + guildId, e);
		}
		return false;
	}

	public static boolean addSelfAssignableRoles(String guildId, Map<String, String> roles){
		var query = "INSERT INTO self_assignable_roles (role_id, guild_id, emote_id) VALUES (?, ?, ?)" + ", (?, ?, ?)".repeat(roles.size() - 1);
		try(var con = SQL.getConnection(); var stmt = con.prepareStatement(query)){
			var i = 0;
			for(var role : roles.entrySet()){
				stmt.setString(++i, role.getKey());
				stmt.setString(++i, guildId);
				stmt.setString(++i, role.getValue());
			}
			return SQL.execute(stmt);
		}
		catch(SQLException e){
			LOG.error("Error inserting self-assignable role", e);
		}
		return false;
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
				return new ReactiveMessage(result.getString("channel_id"), result.getString("message_id"), result.getString("user_id"), result.getString("command_id"), result
						.getString("command"), result.getString("allowed"));
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
