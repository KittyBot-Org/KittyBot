package de.kittybot.kittybot.database;

import com.jagrosh.jdautilities.oauth2.session.SessionData;
import de.kittybot.kittybot.WebService;
import de.kittybot.kittybot.cache.DashboardSessionCache;
import de.kittybot.kittybot.cache.SelfAssignableRoleCache;
import de.kittybot.kittybot.database.jooq.Tables;
import de.kittybot.kittybot.objects.Config;
import de.kittybot.kittybot.objects.GuildSettings;
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
		try(var con = SQL.getCon(); var ctx = SQL.getCtx(con)){
			return ctx.selectFrom(Tables.GUILDS).where(Tables.GUILDS.GUILD_ID.eq(guild.getId())).fetch().isNotEmpty();
		}
		catch(SQLException e){
			LOG.error("Error while checking if guild exists", e);
		}
		return false;
	}

	public static void registerGuild(Guild guild){
		LOG.debug("Registering new guild: {}", guild.getId());
		try(var con = SQL.getCon(); var ctx = SQL.getCtx(con)){
			ctx.insertInto(Tables.GUILDS)
					.columns(Tables.GUILDS.fields())
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
					.onDuplicateKeyIgnore()
					.execute();
		}
		catch(SQLException e){
			LOG.error("Error registering guild: {}", guild.getId(), e);
		}
	}


	public static void addCommandStatistics(String guildId, String commandId, String userId, String command, YearToSecond processingTime){
		try(var con = SQL.getCon(); var ctx = SQL.getCtx(con)){
			ctx.insertInto(Tables.COMMANDS).columns(Tables.COMMANDS.fields()).values(commandId, guildId, userId, command, processingTime, LocalDateTime.now()).execute();
		}
		catch(SQLException e){
			LOG.error("Error adding command statistics for message: {}", commandId, e);
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

	public static GuildSettings getGuildSettings(String guildId){
		try(var con = SQL.getCon(); var ctx = SQL.getCtx(con)){
			var res = ctx.selectFrom(Tables.GUILDS).where(Tables.GUILDS.GUILD_ID.eq(guildId)).fetchOne();
			if(res == null){
				return null;
			}

			return new GuildSettings(guildId,
					res.get(Tables.GUILDS.COMMAND_PREFIX), res.get(Tables.GUILDS.REQUEST_CHANNEL_ID), res.get(Tables.GUILDS.REQUESTS_ENABLED),
					res.get(Tables.GUILDS.ANNOUNCEMENT_CHANNEL_ID), res.get(Tables.GUILDS.JOIN_MESSAGES), res.get(Tables.GUILDS.JOIN_MESSAGES_ENABLED),
					res.get(Tables.GUILDS.LEAVE_MESSAGES), res.get(Tables.GUILDS.LEAVE_MESSAGES_ENABLED), res.get(Tables.GUILDS.BOOST_MESSAGES),
					res.get(Tables.GUILDS.BOOST_MESSAGES_ENABLED), res.get(Tables.GUILDS.LOG_CHANNEL_ID), res.get(Tables.GUILDS.LOG_MESSAGES_ENABLED),
					res.get(Tables.GUILDS.NSFW_ENABLED), res.get(Tables.GUILDS.INACTIVE_ROLE_ID)
					);
		}
		catch(SQLException e){
			LOG.error("Error getting guild settings for guild: " + guildId, e);
		}
		return null;
	}

	public static void setInactiveRoleId(String guildId, String roleId){
		SQL.setProperty(guildId, Tables.GUILDS.INACTIVE_ROLE_ID, roleId);
	}

	public static void setRequestChannelId(String guildId, String roleId){
		SQL.setProperty(guildId, Tables.GUILDS.REQUEST_CHANNEL_ID, roleId);
	}

	public static void setRequestsEnabled(String guildId, boolean enabled){
		SQL.setProperty(guildId, Tables.GUILDS.REQUESTS_ENABLED, enabled);
	}

	public static void setLogChannelId(String guildId, String channelId){
		SQL.setProperty(guildId, Tables.GUILDS.LOG_CHANNEL_ID, channelId);
	}

	public static void setLogMessagesEnabled(String guildId, boolean enabled){
		SQL.setProperty(guildId, Tables.GUILDS.LOG_MESSAGES_ENABLED, enabled);
	}

	public static void setCommandPrefix(String guildId, String prefix){
		SQL.setProperty(guildId, Tables.GUILDS.COMMAND_PREFIX, prefix);
	}

	public static void setAnnouncementChannelId(String guildId, String channelId){
		SQL.setProperty(guildId, Tables.GUILDS.ANNOUNCEMENT_CHANNEL_ID, channelId);
	}

	public static void setJoinMessage(String guildId, String message){
		SQL.setProperty(guildId, Tables.GUILDS.JOIN_MESSAGES, message);
	}

	public static void setJoinMessageEnabled(String guildId, boolean enabled){
		SQL.setProperty(guildId, Tables.GUILDS.JOIN_MESSAGES_ENABLED, enabled);
	}

	public static void setLeaveMessage(String guildId, String message){
		SQL.setProperty(guildId, Tables.GUILDS.LEAVE_MESSAGES, message);
	}

	public static void setLeaveMessageEnabled(String guildId, boolean enabled){
		SQL.setProperty(guildId, Tables.GUILDS.LEAVE_MESSAGES_ENABLED, enabled);
	}

	public static String getBoostMessage(String guildId){
		return SQL.getProperty(guildId, Tables.GUILDS.BOOST_MESSAGES);
	}

	public static void setBoostMessage(String guildId, String message){
		SQL.setProperty(guildId, Tables.GUILDS.BOOST_MESSAGES, message);
	}

	public static void setBoostMessageEnabled(String guildId, boolean enabled){
		SQL.setProperty(guildId, Tables.GUILDS.BOOST_MESSAGES_ENABLED, enabled);
	}

	public static void setNSFWEnabled(String guildId, boolean enabled){
		SQL.setProperty(guildId, Tables.GUILDS.NSFW_ENABLED, enabled);
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
		try(var con = SQL.getCon(); var ctx = SQL.getCtx(con)){
			return ctx.deleteFrom(Tables.SELF_ASSIGNABLE_ROLES).where(Tables.SELF_ASSIGNABLE_ROLES.GUILD_ID.eq(guildId).and(Tables.SELF_ASSIGNABLE_ROLES.ROLE_ID.in(roles))).execute() == roles.size();
		}
		catch(SQLException e){
			LOG.error("Error removing self-assignable roles: {} guilds {}", roles, guildId, e);
		}
		return false;
	}

	public static void addSelfAssignableRoles(String guildId, Map<String, String> roles){
		try(var con = SQL.getCon(); var ctx = SQL.getCtx(con)){
			var col = ctx.insertInto(Tables.SELF_ASSIGNABLE_ROLES).columns(Tables.SELF_ASSIGNABLE_ROLES.fields());
			for(var role : roles.entrySet()){
				col.values(role.getKey(), guildId, role.getValue());
			}
			col.execute();
		}
		catch(SQLException e){
			LOG.error("Error inserting self-assignable roles: {}", roles, e);
		}
	}

	public static Map<String, String> getSelfAssignableRoles(String guildId){
		try(var con = SQL.getCon(); var ctx = SQL.getCtx(con)){
			return ctx.selectFrom(Tables.SELF_ASSIGNABLE_ROLES).where(Tables.SELF_ASSIGNABLE_ROLES.GUILD_ID.eq(guildId)).fetch()
					.stream()
					.collect(Collectors.toMap(sar -> sar.get(Tables.SELF_ASSIGNABLE_ROLES.ROLE_ID), sar -> sar.get(Tables.SELF_ASSIGNABLE_ROLES.EMOTE_ID)));
		}
		catch(SQLException e){
			LOG.error("Error while getting self-assignable roles from guild {}", guildId, e);
		}
		return null;
	}

	public static ReactiveMessage getReactiveMessage(String guildId, String messageId){
		try(var con = SQL.getCon(); var ctx = SQL.getCtx(con)){
			var reactiveMsg = ctx.selectFrom(Tables.REACTIVE_MESSAGES).where(Tables.REACTIVE_MESSAGES.MESSAGE_ID.eq(messageId).and(Tables.REACTIVE_MESSAGES.GUILD_ID.eq(guildId))).fetchOne();
			if(reactiveMsg != null){
				return new ReactiveMessage(reactiveMsg.getChannelId(), reactiveMsg.getMessageId(), reactiveMsg.getUserId(), reactiveMsg.getCommandId(), reactiveMsg.getCommand(), reactiveMsg.getAllowed());
			}
		}
		catch(SQLException e){
			LOG.error("Error while checking reactive message for guild {} message {}", guildId, messageId, e);
		}
		return null;
	}

	public static void addReactiveMessage(String guildId, String userId, String channelId, String messageId, String commandId, String command, String allowed){
		try(var con = SQL.getCon(); var ctx = SQL.getCtx(con)){
			ctx.insertInto(Tables.REACTIVE_MESSAGES).columns(Tables.REACTIVE_MESSAGES.fields()).values(channelId, messageId, commandId, userId, guildId, command, allowed).execute();
		}
		catch(SQLException e){
			LOG.error("Error creating reactive message", e);
		}
	}

	public static void removeReactiveMessage(String guildId, String messageId){
		try(var con = SQL.getCon(); var ctx = SQL.getCtx(con)){
			ctx.deleteFrom(Tables.REACTIVE_MESSAGES).where(Tables.REACTIVE_MESSAGES.MESSAGE_ID.eq(messageId).and(Tables.REACTIVE_MESSAGES.GUILD_ID.eq(guildId))).execute();
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
		try(var con = SQL.getCon(); var ctx = SQL.getCtx(con)){
			ctx.insertInto(Tables.SESSIONS).columns(Tables.SESSIONS.fields()).values(session.getSessionKey(), session.getUserId(), session.getAccessToken(), session.getRefreshToken(), session.getExpiration().toLocalDateTime()).execute();
		}
		catch(SQLException e){
			LOG.error("Error adding session for user {}", session.getUserId(), e);
		}
	}

	public static DashboardSession getSession(String sessionKey){
		try(var con = SQL.getCon(); var ctx = SQL.getCtx(con)){
			var r = ctx.selectFrom(Tables.SESSIONS).where(Tables.SESSIONS.SESSION_KEY.eq(sessionKey)).fetchOne();
			if(r != null){
				return new DashboardSession(r.get(Tables.SESSIONS.USER_ID), new SessionData(sessionKey, r.get(Tables.SESSIONS.ACCESS_TOKEN), r.get(Tables.SESSIONS.REFRESH_TOKEN), "Bearer", OffsetDateTime.of(r.get(Tables.SESSIONS.EXPIRATION), ZoneOffset.UTC), WebService.getScopes()));
			}
		}
		catch(SQLException e){
			LOG.error("Error while getting session", e);
		}
		return null;
	}

	public static boolean sessionExists(String sessionKey){
		try(var con = SQL.getCon(); var ctx = SQL.getCtx(con)){
			return ctx.selectFrom(Tables.SESSIONS).where(Tables.SESSIONS.SESSION_KEY.eq(sessionKey)).fetchOne() != null;
		}
		catch(SQLException e){
			LOG.error("Error checking if session exists", e);
		}
		return false;
	}

	public static boolean hasSession(String userId){
		try(var con = SQL.getCon(); var ctx = SQL.getCtx(con)){
			return ctx.selectFrom(Tables.SESSIONS).where(Tables.SESSIONS.USER_ID.eq(userId)).fetch().isNotEmpty();
		}
		catch(SQLException e){
			LOG.error("Error checking if a user has a session", e);
		}
		return false;
	}

	public static int getUserSessions(String userId){
		try(var con = SQL.getCon(); var ctx = SQL.getCtx(con)){
			return ctx.selectCount().from(Tables.SESSIONS).where(Tables.SESSIONS.USER_ID.eq(userId)).fetchOne(0, int.class);
		}
		catch(SQLException e){
			LOG.error("Error while getting session", e);
		}
		return 0;
	}

	public static void deleteSession(String sessionKey){
		try(var con = SQL.getCon(); var ctx = SQL.getCtx(con)){
			ctx.deleteFrom(Tables.SESSIONS).where(Tables.SESSIONS.SESSION_KEY.eq(sessionKey)).execute();
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
