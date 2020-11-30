package de.kittybot.kittybot.database;

import com.jagrosh.jdautilities.oauth2.session.SessionData;
import de.kittybot.kittybot.WebService;
import de.kittybot.kittybot.cache.SelfAssignableRoleCache;
import de.kittybot.kittybot.objects.Config;
import de.kittybot.kittybot.objects.data.GuildSettings;
import de.kittybot.kittybot.objects.data.ReactiveMessage;
import de.kittybot.kittybot.objects.session.DashboardSession;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

import static de.kittybot.kittybot.database.SQL.*;
import static de.kittybot.kittybot.database.jooq.Tables.*;

public class Database{

	private static final Logger LOG = LoggerFactory.getLogger(Database.class);

	private Database(){}

	public static void init(JDA jda){
		createTable("guilds");
		createTable("self_assignable_roles");
		createTable("commands");
		createTable("reactive_messages");
		createTable("user_statistics");
		createTable("sessions");

		jda.getGuildCache().forEach(guild -> {
			LOG.debug("Loading Guild: {}...", guild.getName());
			if(!isGuildRegistered(guild)){
				registerGuild(guild);
			}
		});
	}

	private static boolean isGuildRegistered(Guild guild){
		try(var con = getCon(); var selectStep = getCtx(con).selectFrom(GUILDS)){
			return selectStep.where(GUILDS.GUILD_ID.eq(guild.getId())).fetch().isNotEmpty();
		}
		catch(SQLException e){
			LOG.error("Error while checking if guild exists", e);
		}
		return false;
	}

	public static void registerGuild(Guild guild){
		LOG.debug("Registering new guild: {}", guild.getId());
		try(var con = getCon()){
			getCtx(con).insertInto(GUILDS)
					.columns(GUILDS.fields())
					.values(
							guild.getId(),
							Config.DEFAULT_PREFIX,
							"-1",
							false,
							guild.getDefaultChannel() == null ? "-1" : guild.getDefaultChannel().getId(),
							"Welcome ${user} to this server!",
							false,
							"Goodbye ${user}(${user_tag})!",
							false,
							"${user} boosted this server!",
							false,
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

	public static GuildSettings getGuildSettings(String guildId){
		try(var con = getCon(); var selectStep = getCtx(con).selectFrom(GUILDS)){
			var res = selectStep.where(GUILDS.GUILD_ID.eq(guildId)).fetchOne();
			if(res == null){
				return null;
			}

			return new GuildSettings(
					res.get(GUILDS.COMMAND_PREFIX), res.get(GUILDS.REQUEST_CHANNEL_ID), res.get(GUILDS.REQUESTS_ENABLED),
					res.get(GUILDS.ANNOUNCEMENT_CHANNEL_ID), res.get(GUILDS.JOIN_MESSAGES), res.get(GUILDS.JOIN_MESSAGES_ENABLED),
					res.get(GUILDS.LEAVE_MESSAGES), res.get(GUILDS.LEAVE_MESSAGES_ENABLED), res.get(GUILDS.BOOST_MESSAGES),
					res.get(GUILDS.BOOST_MESSAGES_ENABLED), res.get(GUILDS.LOG_CHANNEL_ID), res.get(GUILDS.LOG_MESSAGES_ENABLED),
					res.get(GUILDS.NSFW_ENABLED), res.get(GUILDS.INACTIVE_ROLE_ID)
			);
		}
		catch(SQLException e){
			LOG.error("Error getting guild settings for guild: {}", guildId, e);
		}
		return null;
	}

	public static void setInactiveRoleId(String guildId, String roleId){
		setProperty(guildId, GUILDS.INACTIVE_ROLE_ID, roleId);
	}

	public static void setRequestChannelId(String guildId, String roleId){
		setProperty(guildId, GUILDS.REQUEST_CHANNEL_ID, roleId);
	}

	public static void setRequestsEnabled(String guildId, boolean enabled){
		setProperty(guildId, GUILDS.REQUESTS_ENABLED, enabled);
	}

	public static void setLogChannelId(String guildId, String channelId){
		setProperty(guildId, GUILDS.LOG_CHANNEL_ID, channelId);
	}

	public static void setLogMessagesEnabled(String guildId, boolean enabled){
		setProperty(guildId, GUILDS.LOG_MESSAGES_ENABLED, enabled);
	}

	public static void setCommandPrefix(String guildId, String prefix){
		setProperty(guildId, GUILDS.COMMAND_PREFIX, prefix);
	}

	public static void setAnnouncementChannelId(String guildId, String channelId){
		setProperty(guildId, GUILDS.ANNOUNCEMENT_CHANNEL_ID, channelId);
	}

	public static void setJoinMessage(String guildId, String message){
		setProperty(guildId, GUILDS.JOIN_MESSAGES, message);
	}

	public static void setJoinMessageEnabled(String guildId, boolean enabled){
		setProperty(guildId, GUILDS.JOIN_MESSAGES_ENABLED, enabled);
	}

	public static void setLeaveMessage(String guildId, String message){
		setProperty(guildId, GUILDS.LEAVE_MESSAGES, message);
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

	public static void setBoostMessageEnabled(String guildId, boolean enabled){
		setProperty(guildId, GUILDS.BOOST_MESSAGES_ENABLED, enabled);
	}

	public static void setNSFWEnabled(String guildId, boolean enabled){
		setProperty(guildId, GUILDS.NSFW_ENABLED, enabled);
	}

	public static void setSelfAssignableRoles(String guildId, Map<String, String> newRoles){
		var roles = SelfAssignableRoleCache.getSelfAssignableRoles(guildId);
		if(roles == null){
			return;
		}
		var addRoles = new HashMap<String, String>();
		var removeRoles = new HashSet<String>();

		roles.keySet().forEach(key -> {
			if(newRoles.get(key) == null){
				removeRoles.add(key);
			}
		});
		newRoles.forEach((key, value) -> {
			if(roles.get(key) == null){
				addRoles.put(key, value);
			}
		});

		if(!removeRoles.isEmpty()){
			removeSelfAssignableRoles(guildId, removeRoles);
		}
		if(!addRoles.isEmpty()){
			addSelfAssignableRoles(guildId, addRoles);
		}
	}

	public static boolean removeSelfAssignableRoles(String guildId, Set<String> roles){
		try(var con = getCon(); var deleteStep = getCtx(con).deleteFrom(SELF_ASSIGNABLE_ROLES)){
			return deleteStep.where(SELF_ASSIGNABLE_ROLES.GUILD_ID.eq(guildId).and(SELF_ASSIGNABLE_ROLES.ROLE_ID.in(roles))).execute() == roles.size();
		}
		catch(SQLException e){
			LOG.error("Error removing self-assignable roles: {} guilds {}", roles, guildId, e);
		}
		return false;
	}

	public static void addSelfAssignableRoles(String guildId, Map<String, String> roles){
		try(var con = getCon()){
			var col = getCtx(con).insertInto(SELF_ASSIGNABLE_ROLES).columns(SELF_ASSIGNABLE_ROLES.fields());
			roles.forEach((key, value) -> col.values(key, guildId, value));
			col.execute();
		}
		catch(SQLException e){
			LOG.error("Error inserting self-assignable roles: {}", roles, e);
		}
	}

	public static Map<String, String> getSelfAssignableRoles(String guildId){
		try(var con = getCon(); var selectStep = getCtx(con).selectFrom(SELF_ASSIGNABLE_ROLES)){
			return selectStep.where(SELF_ASSIGNABLE_ROLES.GUILD_ID.eq(guildId)).fetch()
					.stream()
					.collect(Collectors.toMap(sar -> sar.get(SELF_ASSIGNABLE_ROLES.ROLE_ID), sar -> sar.get(SELF_ASSIGNABLE_ROLES.EMOTE_ID)));
		}
		catch(SQLException e){
			LOG.error("Error while getting self-assignable roles from guild {}", guildId, e);
		}
		return null;
	}

	public static ReactiveMessage getReactiveMessage(String guildId, String messageId){
		try(var con = getCon(); var selectStep = getCtx(con).selectFrom(REACTIVE_MESSAGES)){
			var reactiveMsg = selectStep.where(REACTIVE_MESSAGES.MESSAGE_ID.eq(messageId).and(REACTIVE_MESSAGES.GUILD_ID.eq(guildId))).fetchOne();
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
		try(var con = getCon()){
			getCtx(con).insertInto(REACTIVE_MESSAGES).columns(REACTIVE_MESSAGES.fields()).values(channelId, messageId, commandId, userId, guildId, command, allowed).execute();
		}
		catch(SQLException e){
			LOG.error("Error creating reactive message", e);
		}
	}

	public static void removeReactiveMessage(String guildId, String messageId){
		try(var con = getCon(); var deleteStep = getCtx(con).deleteFrom(REACTIVE_MESSAGES)){
			deleteStep.where(REACTIVE_MESSAGES.MESSAGE_ID.eq(messageId).and(REACTIVE_MESSAGES.GUILD_ID.eq(guildId))).execute();
		}
		catch(SQLException e){
			LOG.error("Error removing reactive message", e);
		}
	}

	public static void addSession(DashboardSession session){
		try(var con = getCon()){
			getCtx(con).insertInto(SESSIONS).columns(SESSIONS.fields()).values(session.getUserId(), session.getAccessToken(), session.getRefreshToken(), session.getExpiration().toLocalDateTime()).onDuplicateKeyIgnore().execute();
		}
		catch(SQLException e){
			LOG.error("Error adding session for user {}", session.getUserId(), e);
		}
	}

	public static DashboardSession getSession(String userId){
		try(var con = getCon(); var selectStep = getCtx(con).selectFrom(SESSIONS)){
			var r = selectStep.where(SESSIONS.USER_ID.eq(userId)).fetchOne();
			if(r != null){
				return new DashboardSession(new SessionData(userId, r.get(SESSIONS.ACCESS_TOKEN), r.get(SESSIONS.REFRESH_TOKEN), "Bearer", OffsetDateTime.of(r.get(SESSIONS.EXPIRATION), ZoneOffset.UTC), WebService.getScopes()));
			}
		}
		catch(SQLException e){
			LOG.error("Error while getting session for user: {}", userId, e);
		}
		return null;
	}

	public static boolean hasSession(String userId){
		try(var con = getCon(); var selectStep = getCtx(con).selectFrom(SESSIONS)){
			return selectStep.where(SESSIONS.USER_ID.eq(userId)).fetch().isNotEmpty();
		}
		catch(SQLException e){
			LOG.error("Error checking if user: {} has a session", userId, e);
		}
		return false;
	}

	public static void deleteSession(String userId){
		try(var con = getCon(); var deleteStep = getCtx(con).deleteFrom(SESSIONS)){
			deleteStep.where(SESSIONS.USER_ID.eq(userId)).execute();
		}
		catch(SQLException e){
			LOG.error("Error deleting session for user: {}", userId, e);
		}
	}

	public void addSelfAssignableRole(String guildId, String role, String emote){
		addSelfAssignableRoles(guildId, Collections.singletonMap(role, emote));
	}

	public void removeSelfAssignableRole(String guildId, String role){
		removeSelfAssignableRoles(guildId, Collections.singleton(role));
	}

}
