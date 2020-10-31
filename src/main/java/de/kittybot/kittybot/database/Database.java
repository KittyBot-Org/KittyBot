package de.kittybot.kittybot.database;

import com.jagrosh.jdautilities.oauth2.session.SessionData;
import de.kittybot.kittybot.WebService;
import de.kittybot.kittybot.cache.SelfAssignableRoleCache;
import de.kittybot.kittybot.cache.SelfAssignableRoleGroupCache;
import de.kittybot.kittybot.objects.Config;
import de.kittybot.kittybot.objects.GuildSettings;
import de.kittybot.kittybot.objects.ReactiveMessage;
import de.kittybot.kittybot.objects.SelfAssignableRole;
import de.kittybot.kittybot.objects.SelfAssignableRoleGroup;
import de.kittybot.kittybot.objects.session.DashboardSession;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.jooq.types.YearToSecond;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static de.kittybot.kittybot.database.SQL.*;
import static de.kittybot.kittybot.database.jooq.Tables.*;

public class Database{

	private static final Logger LOG = LoggerFactory.getLogger(Database.class);

	private Database(){}

	public static void init(JDA jda){
		createTable("guilds");
		createTable("self_assignable_roles");
		createTable("self_assignable_role_groups");
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


	public static void addCommandStatistics(String guildId, String commandId, String userId, String command, YearToSecond processingTime){
		try(var con = getCon()){
			getCtx(con).insertInto(COMMANDS).columns(COMMANDS.fields()).values(commandId, guildId, userId, command, processingTime, LocalDateTime.now()).execute();
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

	public static void setSelfAssignableRoles(String guildId, Set<SelfAssignableRole> newRoles){
		var roles = SelfAssignableRoleCache.getSelfAssignableRoles(guildId);
		if(roles != null){
			var addRoles = new HashSet<SelfAssignableRole>();
			var removeRoles = new HashSet<String>();
			for(var role : roles){
				if(newRoles.stream().noneMatch(selfAssignableRole -> selfAssignableRole.getRoleId().equals(role.getRoleId()))){
					removeRoles.add(role.getRoleId());
				}
			}
			for(var role : newRoles){
				if(!SelfAssignableRoleCache.isSelfAssignableRole(guildId, role.getRoleId())){
					addRoles.add(role);
				}
			}
			if(!removeRoles.isEmpty()){
				removeSelfAssignableRoles(guildId, removeRoles);
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

	public static void addSelfAssignableRoles(String guildId, Set<SelfAssignableRole> roles){
		try(var con = getCon(); var ctx = getCtx(con)){
			var col = ctx.insertInto(SELF_ASSIGNABLE_ROLES).columns(SELF_ASSIGNABLE_ROLES.fields());
			for(var role : roles){
				col.values(role.getRoleId(), role.getGroupId(), guildId, role.getEmoteId());
			}
			col.execute();
		}
		catch(SQLException e){
			LOG.error("Error inserting self-assignable roles: {}", roles, e);
		}
	}

	public static Set<SelfAssignableRoleGroup> setSelfAssignableRoleGroups(String guildId, Set<SelfAssignableRoleGroup> newGroups){
		var groups = SelfAssignableRoleGroupCache.getSelfAssignableRoleGroups(guildId);
		if(groups != null){
			var addGroups = new HashSet<SelfAssignableRoleGroup>();
			var removeGroups = new HashSet<String>();
			for(var group : groups){
				if(newGroups.stream().noneMatch(selfAssignableRole -> selfAssignableRole.getId().equals(group.getId()))){
					removeGroups.add(group.getId());
				}
			}
			for(var group : newGroups){
				if(!SelfAssignableRoleGroupCache.isSelfAssignableRoleGroup(guildId, group.getId())){
					addGroups.add(group);
				}
			}
			if(!removeGroups.isEmpty()){
				removeSelfAssignableRoleGroups(guildId, removeGroups);
				groups.removeIf(selfAssignableRoleGroup -> removeGroups.contains(selfAssignableRoleGroup.getId()));
			}
			if(!addGroups.isEmpty()){
				var addedGroups = addSelfAssignableRoleGroups(guildId, addGroups);
				if(addedGroups != null){
					groups.addAll(addedGroups);
				}
			}
		}
		return groups;
	}

	public static boolean removeSelfAssignableRoleGroups(String guildId, Set<String> groups){
		try(var con = getCon(); var ctx = getCtx(con)){
			return ctx.deleteFrom(SELF_ASSIGNABLE_ROLE_GROUPS).where(SELF_ASSIGNABLE_ROLE_GROUPS.GUILD_ID.eq(guildId).and(SELF_ASSIGNABLE_ROLE_GROUPS.GROUP_ID.in(groups))).execute() == groups.size();
		}
		catch(SQLException e){
			LOG.error("Error removing self-assignable role groups: " + groups.toString() + " from guild " + guildId, e);
		}
		return false;
	}

	public static Set<SelfAssignableRoleGroup> addSelfAssignableRoleGroups(String guildId, Set<SelfAssignableRoleGroup> groups){
		try(var con = getCon(); var ctx = getCtx(con)){
			var col = ctx.insertInto(SELF_ASSIGNABLE_ROLE_GROUPS).columns(SELF_ASSIGNABLE_ROLE_GROUPS.GUILD_ID, SELF_ASSIGNABLE_ROLE_GROUPS.GROUP_NAME, SELF_ASSIGNABLE_ROLE_GROUPS.MAX_ROLES);
			for(var group : groups){
				col.values(guildId, group.getName(), group.getMaxRoles());
			}
			var res = col.returningResult(SELF_ASSIGNABLE_ROLE_GROUPS.fields()).fetch();
			final var newGroups = new HashSet<SelfAssignableRoleGroup>();
			for(var r : res){
				newGroups.add(new SelfAssignableRoleGroup(guildId, String.valueOf(r.get(SELF_ASSIGNABLE_ROLE_GROUPS.GROUP_ID)), r.get(SELF_ASSIGNABLE_ROLE_GROUPS.GROUP_NAME), r.get(SELF_ASSIGNABLE_ROLE_GROUPS.MAX_ROLES)));
			}
			return newGroups;
		}
		catch(SQLException e){
			LOG.error("Error inserting self-assignable role groups: " + groups.toString(), e);
		}
		return null;
	}

	public static Set<SelfAssignableRole> getSelfAssignableRoles(String guildId){
		try(var con = getCon(); var ctx = getCtx(con)){
			return ctx.selectFrom(SELF_ASSIGNABLE_ROLES).where(SELF_ASSIGNABLE_ROLES.GUILD_ID.eq(guildId)).fetch()
					.stream()
					.map(sar -> new SelfAssignableRole(sar.get(SELF_ASSIGNABLE_ROLES.GUILD_ID), sar.get(SELF_ASSIGNABLE_ROLES.GROUP_ID), sar.get(SELF_ASSIGNABLE_ROLES.ROLE_ID), sar.get(SELF_ASSIGNABLE_ROLES.EMOTE_ID)))
					.collect(Collectors.toSet());
		}
		catch(SQLException e){
			LOG.error("Error while getting self-assignable roles from guild {}", guildId, e);
		}
		return null;
	}

	public static Set<SelfAssignableRoleGroup> getSelfAssignableRoleGroups(String guildId){
		try(var con = getCon(); var ctx = getCtx(con)){
			return ctx.selectFrom(SELF_ASSIGNABLE_ROLE_GROUPS).where(SELF_ASSIGNABLE_ROLE_GROUPS.GUILD_ID.eq(guildId)).fetch()
					.stream()
					.map(sar -> new SelfAssignableRoleGroup(sar.get(SELF_ASSIGNABLE_ROLE_GROUPS.GUILD_ID), String.valueOf(sar.get(SELF_ASSIGNABLE_ROLE_GROUPS.GROUP_ID)), sar.get(SELF_ASSIGNABLE_ROLE_GROUPS.GROUP_NAME), sar.get(SELF_ASSIGNABLE_ROLE_GROUPS.MAX_ROLES)))
					.collect(Collectors.toSet());
		}
		catch(SQLException e){
			LOG.error("Error while getting self-assignable role groups from guild " + guildId, e);
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

	public void addSelfAssignableRole(String guildId, String roleId, String groupId, String emoteId){
		addSelfAssignableRoles(guildId, Collections.singleton(new SelfAssignableRole(guildId, groupId, roleId, emoteId)));
	}

	public void removeSelfAssignableRole(String guildId, String roleId){
		removeSelfAssignableRoles(guildId, Collections.singleton(roleId));
	}

}
