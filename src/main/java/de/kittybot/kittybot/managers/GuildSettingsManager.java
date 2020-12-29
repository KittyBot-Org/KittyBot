package de.kittybot.kittybot.managers;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import de.kittybot.kittybot.jooq.tables.records.BotDisabledChannelsRecord;
import de.kittybot.kittybot.jooq.tables.records.BotIgnoredUsersRecord;
import de.kittybot.kittybot.jooq.tables.records.SnipeDisabledChannelsRecord;
import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.objects.GuildSettings;
import de.kittybot.kittybot.objects.InviteRole;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jooq.Field;
import org.jooq.types.YearToSecond;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static de.kittybot.kittybot.jooq.Tables.*;

public class GuildSettingsManager extends ListenerAdapter{

	private static final Logger LOG = LoggerFactory.getLogger(CommandManager.class);

	private final KittyBot main;
	private final LoadingCache<Long, GuildSettings> guildSettings;

	public GuildSettingsManager(KittyBot main){
		this.main = main;
		this.guildSettings = Caffeine.newBuilder()
				.expireAfterAccess(30, TimeUnit.MINUTES)
				.recordStats()
				.build(this::retrieveGuildSettings);
	}

	public GuildSettings retrieveGuildSettings(long guildId){
		var dbManager = this.main.getDatabaseManager();
		try(var con = dbManager.getCon();
		    var ctxSettings = dbManager.getCtx(con).selectFrom(GUILDS);
		    var ctxSnipeDisabledChannels = dbManager.getCtx(con).selectFrom(SNIPE_DISABLED_CHANNELS);
		    var ctxBotDisabledChannels = dbManager.getCtx(con).selectFrom(BOT_DISABLED_CHANNELS);
		    var ctxBotIgnoredUsers = dbManager.getCtx(con).selectFrom(BOT_IGNORED_USERS);
		    var ctxGuildInviteRoles = dbManager.getCtx(con).select()
		){
			var res = ctxSettings.where(GUILDS.GUILD_ID.eq(guildId)).fetchOne();
			if(res != null){
				return new GuildSettings(
						res,
						ctxSnipeDisabledChannels.where(SNIPE_DISABLED_CHANNELS.GUILD_ID.eq(guildId)).fetch().parallelStream().map(
								SnipeDisabledChannelsRecord::getChannelId).collect(Collectors.toSet()
						),
						ctxBotDisabledChannels.where(BOT_DISABLED_CHANNELS.GUILD_ID.eq(guildId)).fetch().parallelStream().map(
								BotDisabledChannelsRecord::getChannelId).collect(Collectors.toSet()
						),
						ctxBotIgnoredUsers.where(BOT_IGNORED_USERS.GUILD_ID.eq(guildId)).fetch().parallelStream().map(
								BotIgnoredUsersRecord::getUserId).collect(Collectors.toSet()
						),
						ctxGuildInviteRoles.from(GUILD_INVITES).join(GUILD_INVITE_ROLES).on(GUILD_INVITES.GUILD_INVITE_ID.eq(GUILD_INVITE_ROLES.GUILD_INVITE_ID))
								.where(GUILD_INVITES.GUILD_ID.eq(guildId)).fetch().parallelStream()
								.map(InviteRole::new)
								.collect(Collectors.groupingBy(InviteRole::getCode, Collectors.mapping(InviteRole::getRoleId, Collectors.toSet())))
				);

			}
		}
		catch(SQLException e){
			LOG.error("Error while retrieving guild settings for guild: " + guildId, e);
		}
		return null;
	}

	@Override
	public void onGuildReady(@NotNull GuildReadyEvent event){
		insertGuildSettingsIfNotExists(event.getGuild());
	}

	@Override
	public void onGuildJoin(@Nonnull GuildJoinEvent event){
		insertGuildSettings(event.getGuild());
	}

	@Override
	public void onGuildLeave(@Nonnull GuildLeaveEvent event){
		cleanupAllGuildSettings(event.getGuild().getIdLong());
	}

	public void cleanupAllGuildSettings(long guildId){
		LOG.info("Cleaning up guild: {}", guildId);
		var dbManager = this.main.getDatabaseManager();
		try(var con = dbManager.getCon()){
			dbManager.getCtx(con).deleteFrom(GUILDS).where(GUILDS.GUILD_ID.eq(guildId)).execute();
		}
		catch(SQLException e){
			LOG.error("Error cleaning up guild: {}", guildId, e);
		}
	}

	public void insertGuildSettingsIfNotExists(Guild guild){
		var dbManager = this.main.getDatabaseManager();
		var insert = true;
		try(var con = dbManager.getCon(); var ctx = dbManager.getCtx(con).selectFrom(GUILDS)){
			insert = ctx.where(GUILDS.GUILD_ID.eq(guild.getIdLong())).fetch().isEmpty();
		}
		catch(SQLException e){
			LOG.error("Error while checking if guild exists for guild: " + guild.getIdLong(), e);
		}
		if(insert){
			insertGuildSettings(guild);
		}
	}

	public void insertGuildSettings(Guild guild){
		LOG.info("Registering new guild: {}", guild.getId());
		var dbManager = this.main.getDatabaseManager();
		try(var con = dbManager.getCon()){
			dbManager.getCtx(con).insertInto(GUILDS)
					.columns(
							GUILDS.GUILD_ID,
							GUILDS.COMMAND_PREFIX,
							GUILDS.ANNOUNCEMENT_CHANNEL_ID,
							GUILDS.INACTIVE_DURATION
					)
					.values(
							guild.getIdLong(),
							this.main.getConfig().getString("default_prefix"),
							guild.getDefaultChannel() == null ? -1 : guild.getDefaultChannel().getIdLong(),
							YearToSecond.valueOf(Duration.ofDays(3))
					)
					.onDuplicateKeyIgnore()
					.execute();
		}
		catch(SQLException e){
			LOG.error("Error registering guild: {}", guild.getId(), e);
		}
	}

	public CacheStats getStats(){
		return this.guildSettings.stats();
	}

	public String getPrefix(long guildId){
		return this.getSettings(guildId).getCommandPrefix();
	}

	public GuildSettings getSettings(long guildId){
		return this.guildSettings.get(guildId);
	}

	public void setPrefix(long guildId, String prefix){
		updateGuildSetting(guildId, GUILDS.COMMAND_PREFIX, prefix);
		var settings = getSettingsIfPresent(guildId);
		if(settings != null){
			settings.setCommandPrefix(prefix);
		}
	}

	public <T> void updateGuildSetting(long guildId, Field<T> field, T value){
		var dbManager = this.main.getDatabaseManager();
		try(var con = dbManager.getCon()){
			dbManager.getCtx(con).update(GUILDS).set(field, value).where(GUILDS.GUILD_ID.eq(guildId)).execute();
		}
		catch(SQLException e){
			LOG.error("Error updating guild: {}", guildId, e);
		}
	}

	public GuildSettings getSettingsIfPresent(long guildId){
		return this.guildSettings.getIfPresent(guildId);
	}

	public long getAnnouncementChannelId(long guildId){
		return this.getSettings(guildId).getAnnouncementChannelId();
	}

	public void setAnnouncementChannelId(long guildId, long channelId){
		updateGuildSetting(guildId, GUILDS.ANNOUNCEMENT_CHANNEL_ID, channelId);
		var settings = getSettingsIfPresent(guildId);
		if(settings != null){
			settings.setAnnouncementChannelId(channelId);
		}
	}

	public long getRequestChannelId(long guildId){
		return this.getSettings(guildId).getRequestChannelId();
	}

	public void setRequestChannelId(long guildId, long channelId){
		updateGuildSetting(guildId, GUILDS.REQUEST_CHANNEL_ID, channelId);
		var settings = getSettingsIfPresent(guildId);
		if(settings != null){
			settings.setRequestChannelId(channelId);
		}
	}

	public boolean areRequestsEnabled(long guildId){
		return this.getSettings(guildId).areRequestsEnabled();
	}

	public void setRequestsEnabled(long guildId, boolean enabled){
		updateGuildSetting(guildId, GUILDS.REQUESTS_ENABLED, enabled);
		var settings = getSettingsIfPresent(guildId);
		if(settings != null){
			settings.setRequestsEnabled(enabled);
		}
	}

	public String getJoinMessage(long guildId){
		return this.getSettings(guildId).getJoinMessage();
	}

	public void setJoinMessage(long guildId, String message){
		updateGuildSetting(guildId, GUILDS.JOIN_MESSAGE, message);
		var settings = getSettingsIfPresent(guildId);
		if(settings != null){
			settings.setJoinMessage(message);
		}
	}

	public boolean areJoinMessagesEnabled(long guildId){
		return this.getSettings(guildId).areJoinMessagesEnabled();
	}

	public void setJoinMessagesEnabled(long guildId, boolean enabled){
		updateGuildSetting(guildId, GUILDS.JOIN_MESSAGES_ENABLED, enabled);
		var settings = getSettingsIfPresent(guildId);
		if(settings != null){
			settings.setJoinMessagesEnabled(enabled);
		}
	}

	public String getLeaveMessage(long guildId){
		return this.getSettings(guildId).getLeaveMessage();
	}

	public void setLeaveMessage(long guildId, String message){
		updateGuildSetting(guildId, GUILDS.LEAVE_MESSAGE, message);
		var settings = getSettingsIfPresent(guildId);
		if(settings != null){
			settings.setLeaveMessage(message);
		}
	}

	public boolean areLeaveMessagesEnabled(long guildId){
		return this.getSettings(guildId).areLeaveMessagesEnabled();
	}

	public void setLeaveMessagesEnabled(long guildId, boolean enabled){
		updateGuildSetting(guildId, GUILDS.LEAVE_MESSAGES_ENABLED, enabled);
		var settings = getSettingsIfPresent(guildId);
		if(settings != null){
			settings.setLeaveMessagesEnabled(enabled);
		}
	}

	public long getLogChannelId(long guildId){
		return this.getSettings(guildId).getLogChannelId();
	}

	public void setLogChannelId(long guildId, long channelId){
		updateGuildSetting(guildId, GUILDS.LOG_CHANNEL_ID, channelId);
		var settings = getSettingsIfPresent(guildId);
		if(settings != null){
			settings.setLogChannelId(channelId);
		}
	}

	public boolean areLogMessagesEnabled(long guildId){
		return this.getSettings(guildId).areLogMessagesEnabled();
	}

	public void setLogMessagesEnabled(long guildId, boolean enabled){
		updateGuildSetting(guildId, GUILDS.LOG_MESSAGES_ENABLED, enabled);
		var settings = getSettingsIfPresent(guildId);
		if(settings != null){
			settings.setLogMessagesEnabled(enabled);
		}
	}

	public boolean isNsfwEnabled(long guildId){
		return this.getSettings(guildId).isNsfwEnabled();
	}

	public void setNsfwEnabled(long guildId, boolean enabled){
		updateGuildSetting(guildId, GUILDS.NSFW_ENABLED, enabled);
		var settings = getSettingsIfPresent(guildId);
		if(settings != null){
			settings.setNsfwEnabled(enabled);
		}
	}

	public boolean getInactiveRoleId(long guildId){
		return this.getSettings(guildId).areLeaveMessagesEnabled();
	}

	public void setInactiveRoleId(long guildId, long roleId){
		updateGuildSetting(guildId, GUILDS.INACTIVE_ROLE_ID, roleId);
		var settings = getSettingsIfPresent(guildId);
		if(settings != null){
			settings.setInactiveRoleId(roleId);
		}
	}

	public Duration getInactiveDuration(long guildId){
		return this.getSettings(guildId).getInactiveDuration();
	}

	public void setInactiveDuration(long guildId, Duration duration){
		updateGuildSetting(guildId, GUILDS.INACTIVE_DURATION, YearToSecond.valueOf(duration));
		var settings = getSettingsIfPresent(guildId);
		if(settings != null){
			settings.setInactiveDuration(duration);
		}
	}

	public long getDjRoleId(long guildId){
		return this.getSettings(guildId).getDjRoleId();
	}

	public void setDjRoleId(long guildId, long roleId){
		updateGuildSetting(guildId, GUILDS.DJ_ROLE_ID, roleId);
		var settings = getSettingsIfPresent(guildId);
		if(settings != null){
			settings.setDjRoleId(roleId);
		}
	}

	public boolean areSnipesEnabled(long guildId){
		return this.getSettings(guildId).areSnipesEnabled();
	}

	public void setSnipesEnabled(long guildId, boolean enabled){
		updateGuildSetting(guildId, GUILDS.SNIPES_ENABLED, enabled);
		var settings = getSettingsIfPresent(guildId);
		if(settings != null){
			settings.setSnipesEnabled(enabled);
		}
	}

	public boolean areSnipesDisabledInChannel(long guildId, long channelId){
		return getSettings(guildId).areSnipesDisabledInChannel(channelId);
	}

	public void setSnipesDisabledInChannel(long guildId, long channelId, boolean disable){
		var settings = getSettings(guildId);
		if(settings != null){
			settings.setSnipesDisabledInChannel(channelId, disable);
		}
		if(disable){
			insertSnipeDisabledChannel(guildId, channelId);
			return;
		}
		deleteSnipeDisabledChannel(guildId, channelId);
	}

	public void insertSnipeDisabledChannel(long guildId, long channelId){
		var dbManager = this.main.getDatabaseManager();
		try(var con = dbManager.getCon()){
			dbManager.getCtx(con).insertInto(SNIPE_DISABLED_CHANNELS).values(guildId, channelId).execute();
		}
		catch(SQLException e){
			LOG.error("Error inserting bot disabled channels: {}", guildId, e);
		}
	}

	public void deleteSnipeDisabledChannel(long guildId, long channelId){
		var dbManager = this.main.getDatabaseManager();
		try(var con = dbManager.getCon()){
			dbManager.getCtx(con).deleteFrom(SNIPE_DISABLED_CHANNELS).where(SNIPE_DISABLED_CHANNELS.GUILD_ID.eq(guildId).and(SNIPE_DISABLED_CHANNELS.CHANNEL_ID.eq(channelId))).execute();
		}
		catch(SQLException e){
			LOG.error("Error deleting bot disabled channels: {}", guildId, e);
		}
	}

	public boolean isBotDisabledInChannel(long guildId, long channelId){
		return getSettings(guildId).isBotDisabledInChannel(channelId);
	}

	public void setBotDisabledInChannel(long guildId, long channelId, boolean disable){
		var settings = getSettings(guildId);
		if(settings != null){
			settings.setBotDisabledInChannel(channelId, disable);
		}
		if(disable){
			insertBotDisabledChannel(guildId, channelId);
			return;
		}
		deleteBotDisabledChannel(guildId, channelId);
	}

	public void insertBotDisabledChannel(long guildId, long channelId){
		var dbManager = this.main.getDatabaseManager();
		try(var con = dbManager.getCon()){
			dbManager.getCtx(con).insertInto(BOT_DISABLED_CHANNELS).values(guildId, channelId).execute();
		}
		catch(SQLException e){
			LOG.error("Error inserting bot disabled channels: {}", guildId, e);
		}
	}

	public void deleteBotDisabledChannel(long guildId, long channelId){
		var dbManager = this.main.getDatabaseManager();
		try(var con = dbManager.getCon()){
			dbManager.getCtx(con).deleteFrom(BOT_DISABLED_CHANNELS).where(BOT_DISABLED_CHANNELS.GUILD_ID.eq(guildId).and(BOT_DISABLED_CHANNELS.CHANNEL_ID.eq(channelId))).execute();
		}
		catch(SQLException e){
			LOG.error("Error deleting bot disabled channels: {}", guildId, e);
		}
	}

	public Map<String, Set<Long>> getInviteRoles(long guildId){
		return getSettings(guildId).getInviteRoles();
	}

	public Set<Long> getInviteRoles(long guildId, String code){
		return getSettings(guildId).getInviteRoles(code);
	}

	public void setInviteRoles(long guildId, String code, Set<Long> roles){
		var settings = getSettings(guildId);
		if(settings != null){
			settings.setInviteRoles(code, roles);
		}
		if(roles.isEmpty()){
			deleteInviteRole(guildId, code);
			return;
		}
		deleteInviteRoles(guildId, code);
		insertInviteRoles(guildId, code, roles);
	}

	private void deleteInviteRole(long guildId, String code){
		var dbManager = this.main.getDatabaseManager();
		try(var con = dbManager.getCon()){
			var res = dbManager.getCtx(con).deleteFrom(GUILD_INVITES).where(GUILD_INVITES.GUILD_ID.eq(guildId).and(GUILD_INVITES.CODE.eq(code))).returningResult(GUILD_INVITES.GUILD_INVITE_ID).fetchOne();
			if(res == null){
				return;
			}
			var guildInviteId = res.get(GUILD_INVITES.GUILD_INVITE_ID);
			dbManager.getCtx(con).deleteFrom(GUILD_INVITE_ROLES).where(GUILD_INVITE_ROLES.GUILD_INVITE_ID.eq(guildInviteId)).execute();
		}
		catch(SQLException e){
			LOG.error("Error deleting invite roles for code: {}", guildId, e);
		}
	}

	private void deleteInviteRoles(long guildId, String code){
		var dbManager = this.main.getDatabaseManager();
		try(var con = dbManager.getCon()){
			var res = dbManager.getCtx(con).selectFrom(GUILD_INVITES).where(GUILD_INVITES.GUILD_ID.eq(guildId).and(GUILD_INVITES.CODE.eq(code))).fetchOne();
			if(res == null){
				return;
			}
			var guildInviteId = res.get(GUILD_INVITES.GUILD_INVITE_ID);
			dbManager.getCtx(con).deleteFrom(GUILD_INVITE_ROLES).where(GUILD_INVITE_ROLES.GUILD_INVITE_ID.eq(guildInviteId)).execute();
		}
		catch(SQLException e){
			LOG.error("Error deleting invite roles for code: {}", guildId, e);
		}
	}

	private void insertInviteRoles(long guildId, String code, Set<Long> roles){
		var dbManager = this.main.getDatabaseManager();
		try(var con = dbManager.getCon(); var selectCtx = dbManager.getCtx(con).selectFrom(GUILD_INVITES)){
			var res = selectCtx.where(GUILD_INVITES.GUILD_ID.eq(guildId).and(GUILD_INVITES.CODE.eq(code))).fetchOne();
			var inviteRoleId = 0L;
			if(res == null){
				var res2 = dbManager.getCtx(con).insertInto(GUILD_INVITES).columns(GUILD_INVITES.GUILD_ID, GUILD_INVITES.CODE).values(guildId, code).returningResult(GUILD_INVITES.GUILD_INVITE_ID).fetchOne();
				if(res2 == null){
					LOG.error("Cane we have a problem! Tickle Topi!");
					return;
				}
				inviteRoleId = res2.get(GUILD_INVITES.GUILD_INVITE_ID);
			}
			else{
				inviteRoleId = res.getGuildInviteId();
			}
			var ctx = dbManager.getCtx(con).insertInto(GUILD_INVITE_ROLES).columns(GUILD_INVITE_ROLES.ROLE_ID, GUILD_INVITE_ROLES.GUILD_INVITE_ID);
			for(var role : roles){
				ctx = ctx.values(role, inviteRoleId);
			}
			ctx.execute();
		}
		catch(SQLException e){
			LOG.error("Error inserting invite role", e);
		}
	}

	public void removeInviteRole(long guildId, long roleId){
		var settings = getSettingsIfPresent(guildId);
		if(settings != null){
			settings.getInviteRoles().forEach((key, value) -> value.removeIf(r -> r == roleId));
		}
		var dbManager = this.main.getDatabaseManager();
		try(var con = dbManager.getCon()){
			dbManager.getCtx(con).deleteFrom(GUILD_INVITE_ROLES).where(GUILD_INVITE_ROLES.ROLE_ID.eq(roleId)).execute();
		}
		catch(SQLException e){
			LOG.error("Error deleting invite role: {}", roleId, e);
		}
	}

	public void addBotIgnoredUsers(long guildId, Set<Long> users){
		var settings = getSettingsIfPresent(guildId);
		if(settings != null){
			settings.setBotIgnoredUsers(users, true);
		}
		insertIgnoredUsers(guildId, users);
	}

	private void insertIgnoredUsers(long guildId, Set<Long> users){
		var dbManager = this.main.getDatabaseManager();
		try(var con = dbManager.getCon()){
			var ctx = dbManager.getCtx(con).insertInto(BOT_IGNORED_USERS).columns(BOT_IGNORED_USERS.GUILD_ID, BOT_IGNORED_USERS.USER_ID);
			for(var user : users){
				ctx = ctx.values(guildId, user);
			}
			ctx.execute();
		}
		catch(SQLException e){
			LOG.error("Error inserting ignored users", e);
		}
	}

	public void deleteBotIgnoredUsers(long guildId, Set<Long> users){
		var settings = getSettingsIfPresent(guildId);
		if(settings != null){
			settings.setBotIgnoredUsers(users, false);
		}
		removeIgnoredUsers(guildId, users);
	}

	private void removeIgnoredUsers(long guildId, Set<Long> users){
		var dbManager = this.main.getDatabaseManager();
		try(var con = dbManager.getCon()){
			dbManager.getCtx(con).deleteFrom(BOT_IGNORED_USERS).where(BOT_IGNORED_USERS.GUILD_ID.eq(guildId).and(BOT_IGNORED_USERS.USER_ID.in(users))).execute();
		}
		catch(SQLException e){
			LOG.error("Error inserting ignored users", e);
		}
	}

}
