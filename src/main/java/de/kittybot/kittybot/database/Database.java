package de.kittybot.kittybot.database;

import com.jagrosh.jdautilities.oauth2.session.SessionData;
import de.kittybot.kittybot.WebService;
import de.kittybot.kittybot.cache.DashboardSessionCache;
import de.kittybot.kittybot.cache.SelfAssignableRoleCache;
import de.kittybot.kittybot.objects.Config;
import de.kittybot.kittybot.objects.ReactiveMessage;
import de.kittybot.kittybot.objects.session.DashboardSession;
import de.kittybot.kittybot.utils.Utils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.jooq.types.YearToSecond;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

import static de.kittybot.kittybot.database.SQL.*;
import static de.kittybot.kittybot.database.jooq.Tables.*;

public class Database{

	private static final Logger LOG = LoggerFactory.getLogger(Database.class);

	private Database(){
	}

	public static void init(JDA jda){
		createTable("guilds");
		createTable("self_assignable_roles");
		createTable("commands");
		createTable("reactive_messages");
		createTable("user_statistics");
		createTable("sessions");
		for(Guild guild : jda.getGuilds()){
			LOG.debug("Loading Guild: {}...", guild.getName());
			if(!isGuildRegistered(guild)){
				registerGuild(guild);
			}
		}
	}

	private static boolean isGuildRegistered(Guild guild){
		try(var con = getCon(); var ctx = getCtx(con)){
			return ctx.selectFrom(GUILDS).where(GUILDS.GUILD_ID.eq(guild.getId())).fetch().isNotEmpty();
		}
		catch(SQLException e){
			LOG.error("Error while checking if guild exists", e);
		}
		return false;
	}

	public static void registerGuild(Guild guild){
		LOG.debug("Registering new guild: {}", guild.getId());
		try(var con = getCon(); var ctx = getCtx(con)){
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


	public static void addCommandStatistics(String guildId, String commandId, String userId, String command, YearToSecond processingTime){
		try(var con = getCon(); var ctx = getCtx(con)){
			ctx.insertInto(COMMANDS).columns(COMMANDS.fields()).values(commandId, guildId, userId, command, processingTime, LocalDateTime.now()).execute();
		}
		catch(SQLException e){
			LOG.error("Error adding command statistics for message: " + commandId, e);
		}
	}

/*	public static void getCommandStatistics(String guildId, String userId, String command){
		try(var con = getCon(); var ctx = getCtx(con)){
			var res = ctx.selectFrom(COMMANDS).where(COMMANDS.GUILD_ID.eq(guildId)).fetch();
			for(var r : res){
				r.get(COMMANDS.PROCESSING_TIME).
			}
		}
		catch(SQLException e){
			LOG.error("Error adding command statistics for message: " + commandId, e);
		}
	} */

	public static void setCommandPrefix(String guildId, String prefix){
		setProperty(guildId, GUILDS.COMMAND_PREFIX, prefix);
	}

	public static String getCommandPrefix(String guildId){
		return getProperty(guildId, GUILDS.COMMAND_PREFIX);
	}

	public static void setSelfAssignableRoles(String guildId, Map<String, String> newRoles){
		var roles = SelfAssignableRoleCache.getSelfAssignableRoles(guildId);
		if(roles != null){
			var addRoles = new HashMap<String, String>();
			var removeRoles = new HashSet<String>();
			for(var role : roles.entrySet()){
				if(newRoles.get(role.getKey()) == null){
					removeRoles.add(role.getKey());
				}
			}
			for(var role : newRoles.entrySet()){
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

	public static boolean removeSelfAssignableRoles(String guildId, Set<String> roles){
		try(var con = getCon(); var ctx = getCtx(con)){
			return ctx.deleteFrom(SELF_ASSIGNABLE_ROLES).where(SELF_ASSIGNABLE_ROLES.GUILD_ID.eq(guildId).and(SELF_ASSIGNABLE_ROLES.ROLE_ID.in(roles))).execute() == roles.size();
		}
		catch(SQLException e){
			LOG.error("Error removing self-assignable roles: " + roles.toString() + " guild " + guildId, e);
		}
		return false;
	}

	public static void addSelfAssignableRoles(String guildId, Map<String, String> roles){
		try(var con = getCon(); var ctx = getCtx(con)){
			var col = ctx.insertInto(SELF_ASSIGNABLE_ROLES).columns(SELF_ASSIGNABLE_ROLES.fields());
			for(var role : roles.entrySet()){
				col.values(role.getKey(), guildId, role.getValue());
			}
			col.execute();
		}
		catch(SQLException e){
			LOG.error("Error inserting self-assignable roles: " + roles.toString(), e);
		}
	}

	public static Map<String, String> getSelfAssignableRoles(String guildId){
		try(var con = getCon(); var ctx = getCtx(con)){
			return ctx.selectFrom(SELF_ASSIGNABLE_ROLES).where(SELF_ASSIGNABLE_ROLES.GUILD_ID.eq(guildId)).fetch()
					.stream()
					.collect(Collectors.toMap(sar -> sar.get(SELF_ASSIGNABLE_ROLES.ROLE_ID), sar -> sar.get(SELF_ASSIGNABLE_ROLES.EMOTE_ID)));
		}
		catch(SQLException e){
			LOG.error("Error while getting self-assignable roles from guild " + guildId, e);
		}
		return null;
	}

	public static String getAnnouncementChannelId(String guildId){
		return getProperty(guildId, GUILDS.ANNOUNCEMENT_CHANNEL_ID);
	}

	public static void setAnnouncementChannelId(String guildId, String channelId){
		setProperty(guildId, GUILDS.ANNOUNCEMENT_CHANNEL_ID, channelId);
	}

	public static String getJoinMessage(String guildId){
		return getProperty(guildId, GUILDS.JOIN_MESSAGES);
	}

	public static void setJoinMessage(String guildId, String message){
		setProperty(guildId, GUILDS.JOIN_MESSAGES, message);
	}

	public static Boolean getJoinMessageEnabled(String guildId){
		return getProperty(guildId, GUILDS.JOIN_MESSAGES_ENABLED);
	}

	public static void setJoinMessageEnabled(String guildId, boolean enabled){
		setProperty(guildId, GUILDS.JOIN_MESSAGES_ENABLED, enabled);
	}

	public static String getLeaveMessage(String guildId){
		return getProperty(guildId, GUILDS.LEAVE_MESSAGES);
	}

	public static void setLeaveMessage(String guildId, String message){
		setProperty(guildId, GUILDS.LEAVE_MESSAGES, message);
	}

	public static Boolean getLeaveMessageEnabled(String guildId){
		return getProperty(guildId, GUILDS.LEAVE_MESSAGES_ENABLED);
	}

	public static void setLeaveMessageEnabled(String guildId, boolean enabled){
		setProperty(guildId, GUILDS.LEAVE_MESSAGES_ENABLED, enabled);
	}

	public static String getBoostMessage(String guildId){
		return getProperty(guildId, GUILDS.BOOST_MESSAGES);
	}

	public static void setBoostMessage(String guildId, String message){
		setProperty(guildId, GUILDS.BOOST_MESSAGES, message);
	}

	public static Boolean getBoostMessageEnabled(String guildId){
		return getProperty(guildId, GUILDS.BOOST_MESSAGES_ENABLED);
	}

	public static void setBoostMessageEnabled(String guildId, boolean enabled){
		setProperty(guildId, GUILDS.BOOST_MESSAGES_ENABLED, enabled);
	}

	public static Boolean getNSFWEnabled(String guildId){
		return getProperty(guildId, GUILDS.NSFW_ENABLED);
	}

	public static void setNSFWEnabled(String guildId, boolean enabled){
		setProperty(guildId, GUILDS.NSFW_ENABLED, enabled);
	}

	public static ReactiveMessage getReactiveMessage(String guildId, String messageId){
		try(var con = getCon(); var ctx = getCtx(con)){
			var reactiveMsg = ctx.selectFrom(REACTIVE_MESSAGES).where(REACTIVE_MESSAGES.MESSAGE_ID.eq(messageId).and(REACTIVE_MESSAGES.GUILD_ID.eq(guildId))).fetchOne();
			if(reactiveMsg != null){
				return new ReactiveMessage(reactiveMsg.getChannelId(), reactiveMsg.getMessageId(), reactiveMsg.getUserId(), reactiveMsg.getCommandId(), reactiveMsg.getCommand(), reactiveMsg.getAllowed());
			}
		}
		catch(SQLException e){
			LOG.error("Error while checking reactive message for guild " + guildId + " message " + messageId, e);
		}
		return null;
	}

	public static void addReactiveMessage(String guildId, String userId, String channelId, String messageId, String commandId, String command, String allowed){
		try(var con = getCon(); var ctx = getCtx(con)){
			ctx.insertInto(REACTIVE_MESSAGES).columns(REACTIVE_MESSAGES.fields()).values(channelId, messageId, commandId, userId, guildId, command, allowed).execute();
		}
		catch(SQLException e){
			LOG.error("Error creating reactive message", e);
		}
	}

	public static void removeReactiveMessage(String guildId, String messageId){
		try(var con = getCon(); var ctx = getCtx(con)){
			ctx.deleteFrom(REACTIVE_MESSAGES).where(REACTIVE_MESSAGES.MESSAGE_ID.eq(messageId).and(REACTIVE_MESSAGES.GUILD_ID.eq(guildId))).execute();
		}
		catch(SQLException e){
			LOG.error("Error removing reactive message", e);
		}
	}

	public static String generateUniqueKey(){
		var key = Utils.generate(32);
		while(DashboardSessionCache.sessionExists(key)){
			key = Utils.generate(32);
		}
		return key;
	}

	public static void addSession(DashboardSession session){
		try(var con = getCon(); var ctx = getCtx(con)){
			ctx.insertInto(SESSIONS).columns(SESSIONS.fields()).values(session.getSessionKey(), session.getUserId(), session.getAccessToken(), session.getRefreshToken(), session.getExpiration().toLocalDateTime()).execute();
		}
		catch(SQLException e){
			LOG.error("Error adding session for user " + session.getUserId(), e);
		}
	}

	public static DashboardSession getSession(String sessionKey){
		try(var con = getCon(); var ctx = getCtx(con)){
			var r = ctx.selectFrom(SESSIONS).where(SESSIONS.SESSION_KEY.eq(sessionKey)).fetchOne();
			if(r != null){
				return new DashboardSession(r.get(SESSIONS.USER_ID), new SessionData(sessionKey, r.get(SESSIONS.ACCESS_TOKEN), r.get(SESSIONS.REFRESH_TOKEN), "Bearer", OffsetDateTime.of(r.get(SESSIONS.EXPIRATION), ZoneOffset.UTC), WebService.getScopes()));
			}
		}
		catch(SQLException e){
			LOG.error("Error while getting session", e);
		}
		return null;
	}

	public static boolean sessionExists(String sessionKey){
		try(var con = getCon(); var ctx = getCtx(con)){
			return ctx.selectFrom(SESSIONS).where(SESSIONS.SESSION_KEY.eq(sessionKey)).fetchOne() != null;
		}
		catch(SQLException e){
			LOG.error("Error checking if session exists", e);
		}
		return false;
	}

	public static boolean hasSession(String userId){
		try(var con = getCon(); var ctx = getCtx(con)){
			return ctx.selectFrom(SESSIONS).where(SESSIONS.USER_ID.eq(userId)).fetch().isNotEmpty();
		}
		catch(SQLException e){
			LOG.error("Error checking if a user has a session", e);
		}
		return false;
	}

	public static int getUserSessions(String userId){
		try(var con = getCon(); var ctx = getCtx(con)){
			return ctx.selectCount().from(SESSIONS).where(SESSIONS.USER_ID.eq(userId)).fetchOne(0, int.class);
		}
		catch(SQLException e){
			LOG.error("Error while getting session", e);
		}
		return 0;
	}

	public static void deleteSession(String sessionKey){
		try(var con = getCon(); var ctx = getCtx(con)){
			ctx.deleteFrom(SESSIONS).where(SESSIONS.SESSION_KEY.eq(sessionKey)).execute();
		}
		catch(SQLException e){
			LOG.error("Error deleting session", e);
		}
	}

	public void addSelfAssignableRole(String guildId, String role, String emote){
		addSelfAssignableRoles(guildId, Collections.singletonMap(role, emote));
	}

	public void removeSelfAssignableRole(String guildId, String role){
		removeSelfAssignableRoles(guildId, Collections.singleton(role));
	}

}
