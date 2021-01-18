package de.kittybot.kittybot.modules;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import de.kittybot.kittybot.jooq.tables.records.BotDisabledChannelsRecord;
import de.kittybot.kittybot.jooq.tables.records.BotIgnoredMembersRecord;
import de.kittybot.kittybot.jooq.tables.records.SnipeDisabledChannelsRecord;
import de.kittybot.kittybot.module.Module;
import de.kittybot.kittybot.objects.SelfAssignableRole;
import de.kittybot.kittybot.objects.SelfAssignableRoleGroup;
import de.kittybot.kittybot.objects.Settings;
import de.kittybot.kittybot.utils.Config;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import org.jetbrains.annotations.NotNull;
import org.jooq.Field;
import org.jooq.types.YearToSecond;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static de.kittybot.kittybot.jooq.Tables.*;

public class SettingsModule extends Module{

	private static final Logger LOG = LoggerFactory.getLogger(SettingsModule.class);

	private LoadingCache<Long, Settings> guildSettings;

	@Override
	public void onEnable(){
		this.guildSettings = Caffeine.newBuilder()
				.expireAfterAccess(30, TimeUnit.MINUTES)
				.recordStats()
				.build(this::retrieveGuildSettings);
	}

	public Settings retrieveGuildSettings(long guildId){
		var dbModule = this.modules.get(DatabaseModule.class);

		try(
				var ctxSettings = dbModule.getCtx().selectFrom(GUILDS);
				var ctxSnipeDisabledChannels = dbModule.getCtx().selectFrom(SNIPE_DISABLED_CHANNELS);
				var ctxBotDisabledChannels = dbModule.getCtx().selectFrom(BOT_DISABLED_CHANNELS);
				var ctxBotIgnoredUsers = dbModule.getCtx().selectFrom(BOT_IGNORED_MEMBERS);
				var ctxSelfAssignableRoles = dbModule.getCtx().selectFrom(SELF_ASSIGNABLE_ROLES);
				var ctxSelfAssignableRoleGroups = dbModule.getCtx().selectFrom(SELF_ASSIGNABLE_ROLE_GROUPS);
				var ctxGuildInviteRoles = dbModule.getCtx().select()
		){
			var res = ctxSettings.where(GUILDS.ID.eq(guildId)).fetchOne();
			if(res != null){
				return new Settings(
						res,

						ctxSnipeDisabledChannels.where(SNIPE_DISABLED_CHANNELS.GUILD_ID.eq(guildId)).fetch().map(SnipeDisabledChannelsRecord::getChannelId),

						ctxBotDisabledChannels.where(BOT_DISABLED_CHANNELS.GUILD_ID.eq(guildId)).fetch().map(BotDisabledChannelsRecord::getChannelId),

						ctxBotIgnoredUsers.where(BOT_IGNORED_MEMBERS.GUILD_ID.eq(guildId)).fetch().map(BotIgnoredMembersRecord::getUserId),

						ctxSelfAssignableRoles.where(SELF_ASSIGNABLE_ROLES.GUILD_ID.eq(guildId)).fetch().map(SelfAssignableRole::new),

						ctxSelfAssignableRoleGroups.where(SELF_ASSIGNABLE_ROLE_GROUPS.GUILD_ID.eq(guildId)).fetch().map(SelfAssignableRoleGroup::new),

						ctxGuildInviteRoles.from(GUILD_INVITES).join(GUILD_INVITE_ROLES).on(GUILD_INVITES.ID.eq(GUILD_INVITE_ROLES.GUILD_INVITE_ID))
								.where(GUILD_INVITES.GUILD_ID.eq(guildId)).fetch()
								.stream()
								.collect(Collectors.groupingBy(record -> record.get(GUILD_INVITES.CODE), Collectors.mapping(record -> record.get(GUILD_INVITE_ROLES.ROLE_ID), Collectors.toSet())))
				);

			}
		}
		return null;
	}

	@Override
	public void onGuildReady(@NotNull GuildReadyEvent event){
		insertSettingsIfNotExists(event.getGuild());
	}

	@Override
	public void onGuildJoin(@Nonnull GuildJoinEvent event){
		insertSettings(event.getGuild());
	}

	@Override
	public void onGuildLeave(@Nonnull GuildLeaveEvent event){
		cleanupAllSettings(event.getGuild().getIdLong());
	}

	public void cleanupAllSettings(long guildId){
		LOG.info("Cleaning up guild: {}", guildId);
		var dbModule = this.modules.get(DatabaseModule.class);
		dbModule.getCtx().deleteFrom(GUILDS).where(GUILDS.ID.eq(guildId)).execute();
	}

	public void insertSettingsIfNotExists(Guild guild){
		var dbModule = this.modules.get(DatabaseModule.class);
		var insert = true;
		try(var ctx = dbModule.getCtx().selectFrom(GUILDS)){
			insert = ctx.where(GUILDS.ID.eq(guild.getIdLong())).fetch().isEmpty();
		}
		if(insert){
			insertSettings(guild);
		}
	}

	public void insertSettings(Guild guild){
		LOG.info("Registering new guild: {}", guild.getId());

		this.modules.get(DatabaseModule.class).getCtx().insertInto(GUILDS)
				.columns(
						GUILDS.ID,
						GUILDS.PREFIX,
						GUILDS.ANNOUNCEMENT_CHANNEL_ID,
						GUILDS.INACTIVE_DURATION
				)
				.values(
						guild.getIdLong(),
						Config.DEFAULT_PREFIX,
						guild.getDefaultChannel() == null ? -1 : guild.getDefaultChannel().getIdLong(),
						YearToSecond.valueOf(Duration.ofDays(3))
				)
				.onDuplicateKeyIgnore()
				.execute();
	}

	public CacheStats getStats(){
		return this.guildSettings.stats();
	}

	public Settings getSettings(long guildId){
		return this.guildSettings.get(guildId);
	}

	public <T> void updateSetting(long guildId, Field<T> field, T value){
		this.modules.get(DatabaseModule.class).getCtx().update(GUILDS).set(field, value).where(GUILDS.ID.eq(guildId)).execute();
	}

	public Settings getSettingsIfPresent(long guildId){
		return this.guildSettings.getIfPresent(guildId);
	}

	public long getStreamAnnouncementChannelId(long guildId){
		return this.getSettings(guildId).getStreamAnnouncementChannelId();
	}

	public void setStreamAnnouncementChannelId(long guildId, long channelId){
		updateSetting(guildId, GUILDS.STREAM_ANNOUNCEMENT_CHANNEL_ID, channelId);
		var settings = getSettingsIfPresent(guildId);
		if(settings != null){
			settings.setStreamAnnouncementChannelId(channelId);
		}
	}

	public long getAnnouncementChannelId(long guildId){
		return this.getSettings(guildId).getAnnouncementChannelId();
	}

	public void setAnnouncementChannelId(long guildId, long channelId){
		updateSetting(guildId, GUILDS.ANNOUNCEMENT_CHANNEL_ID, channelId);
		var settings = getSettingsIfPresent(guildId);
		if(settings != null){
			settings.setAnnouncementChannelId(channelId);
		}
	}

	public long getRequestChannelId(long guildId){
		return this.getSettings(guildId).getRequestChannelId();
	}

	public void setRequestChannelId(long guildId, long channelId){
		updateSetting(guildId, GUILDS.REQUEST_CHANNEL_ID, channelId);
		var settings = getSettingsIfPresent(guildId);
		if(settings != null){
			settings.setRequestChannelId(channelId);
		}
	}

	public boolean areRequestsEnabled(long guildId){
		return this.getSettings(guildId).areRequestsEnabled();
	}

	public void setRequestsEnabled(long guildId, boolean enabled){
		updateSetting(guildId, GUILDS.REQUESTS_ENABLED, enabled);
		var settings = getSettingsIfPresent(guildId);
		if(settings != null){
			settings.setRequestsEnabled(enabled);
		}
	}

	public String getJoinMessage(long guildId){
		return this.getSettings(guildId).getJoinMessage();
	}

	public void setJoinMessage(long guildId, String message){
		updateSetting(guildId, GUILDS.JOIN_MESSAGE, message);
		var settings = getSettingsIfPresent(guildId);
		if(settings != null){
			settings.setJoinMessage(message);
		}
	}

	public boolean areJoinMessagesEnabled(long guildId){
		return this.getSettings(guildId).areJoinMessagesEnabled();
	}

	public void setJoinMessagesEnabled(long guildId, boolean enabled){
		updateSetting(guildId, GUILDS.JOIN_MESSAGES_ENABLED, enabled);
		var settings = getSettingsIfPresent(guildId);
		if(settings != null){
			settings.setJoinMessagesEnabled(enabled);
		}
	}

	public String getLeaveMessage(long guildId){
		return this.getSettings(guildId).getLeaveMessage();
	}

	public void setLeaveMessage(long guildId, String message){
		updateSetting(guildId, GUILDS.LEAVE_MESSAGE, message);
		var settings = getSettingsIfPresent(guildId);
		if(settings != null){
			settings.setLeaveMessage(message);
		}
	}

	public boolean areLeaveMessagesEnabled(long guildId){
		return this.getSettings(guildId).areLeaveMessagesEnabled();
	}

	public void setLeaveMessagesEnabled(long guildId, boolean enabled){
		updateSetting(guildId, GUILDS.LEAVE_MESSAGES_ENABLED, enabled);
		var settings = getSettingsIfPresent(guildId);
		if(settings != null){
			settings.setLeaveMessagesEnabled(enabled);
		}
	}

	public long getLogChannelId(long guildId){
		return this.getSettings(guildId).getLogChannelId();
	}

	public void setLogChannelId(long guildId, long channelId){
		updateSetting(guildId, GUILDS.LOG_CHANNEL_ID, channelId);
		var settings = getSettingsIfPresent(guildId);
		if(settings != null){
			settings.setLogChannelId(channelId);
		}
	}

	public boolean areLogMessagesEnabled(long guildId){
		return this.getSettings(guildId).areLogMessagesEnabled();
	}

	public void setLogMessagesEnabled(long guildId, boolean enabled){
		updateSetting(guildId, GUILDS.LOG_MESSAGES_ENABLED, enabled);
		var settings = getSettingsIfPresent(guildId);
		if(settings != null){
			settings.setLogMessagesEnabled(enabled);
		}
	}

	public boolean isNsfwEnabled(long guildId){
		return this.getSettings(guildId).isNsfwEnabled();
	}

	public void setNsfwEnabled(long guildId, boolean enabled){
		updateSetting(guildId, GUILDS.NSFW_ENABLED, enabled);
		var settings = getSettingsIfPresent(guildId);
		if(settings != null){
			settings.setNsfwEnabled(enabled);
		}
	}

	public boolean getInactiveRoleId(long guildId){
		return this.getSettings(guildId).areLeaveMessagesEnabled();
	}

	public void setInactiveRoleId(long guildId, long roleId){
		updateSetting(guildId, GUILDS.INACTIVE_ROLE_ID, roleId);
		var settings = getSettingsIfPresent(guildId);
		if(settings != null){
			settings.setInactiveRoleId(roleId);
		}
	}

	public Duration getInactiveDuration(long guildId){
		return this.getSettings(guildId).getInactiveDuration();
	}

	public void setInactiveDuration(long guildId, Duration duration){
		updateSetting(guildId, GUILDS.INACTIVE_DURATION, YearToSecond.valueOf(duration));
		var settings = getSettingsIfPresent(guildId);
		if(settings != null){
			settings.setInactiveDuration(duration);
		}
	}

	public long getDjRoleId(long guildId){
		return this.getSettings(guildId).getDjRoleId();
	}

	public boolean hasDJRole(Member member){
		var djRole = this.getSettings(member.getGuild().getIdLong()).getDjRoleId();
		return member.getRoles().stream().anyMatch(role -> role.getIdLong() == djRole);
	}

	public void setDjRoleId(long guildId, long roleId){
		updateSetting(guildId, GUILDS.DJ_ROLE_ID, roleId);
		var settings = getSettingsIfPresent(guildId);
		if(settings != null){
			settings.setDjRoleId(roleId);
		}
	}

	public boolean areSnipesEnabled(long guildId){
		return this.getSettings(guildId).areSnipesEnabled();
	}

	public void setSnipesEnabled(long guildId, boolean enabled){
		updateSetting(guildId, GUILDS.SNIPES_ENABLED, enabled);
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
		this.modules.get(DatabaseModule.class).getCtx().insertInto(SNIPE_DISABLED_CHANNELS).columns(SNIPE_DISABLED_CHANNELS.GUILD_ID, SNIPE_DISABLED_CHANNELS.CHANNEL_ID).values(guildId, channelId).execute();
	}

	public void deleteSnipeDisabledChannel(long guildId, long channelId){
		this.modules.get(DatabaseModule.class).getCtx().deleteFrom(SNIPE_DISABLED_CHANNELS).where(
				SNIPE_DISABLED_CHANNELS.GUILD_ID.eq(guildId).and(SNIPE_DISABLED_CHANNELS.CHANNEL_ID.eq(channelId))
		).execute();
	}

	public boolean isBotDisabledInChannel(long guildId, long channelId){
		return getSettings(guildId).isBotDisabledInChannel(channelId);
	}

	public Set<Long> getBotDisabledChannels(long guildId){
		return getSettings(guildId).getBotDisabledChannels();
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
		this.modules.get(DatabaseModule.class).getCtx().insertInto(BOT_DISABLED_CHANNELS).columns(BOT_DISABLED_CHANNELS.GUILD_ID, BOT_DISABLED_CHANNELS.CHANNEL_ID).values(guildId, channelId).execute();
	}

	public void deleteBotDisabledChannel(long guildId, long channelId){
		this.modules.get(DatabaseModule.class).getCtx().deleteFrom(BOT_DISABLED_CHANNELS).where(
				BOT_DISABLED_CHANNELS.GUILD_ID.eq(guildId).and(BOT_DISABLED_CHANNELS.CHANNEL_ID.eq(channelId))
		).execute();
	}

	public Set<SelfAssignableRole> getSelfAssignableRoles(long guildId){
		return getSettings(guildId).getSelfAssignableRoles();
	}

	public void addSelfAssignableRoles(long guildId, Set<SelfAssignableRole> roles){
		var settings = getSettings(guildId);
		if(settings != null){
			settings.addSelfAssignableRoles(roles);
		}
		insertSelfAssignableRoles(guildId, roles);
	}

	private void insertSelfAssignableRoles(long guildId, Set<SelfAssignableRole> roles){
		var ctx = this.modules.get(DatabaseModule.class).getCtx().insertInto(SELF_ASSIGNABLE_ROLES)
				.columns(SELF_ASSIGNABLE_ROLES.GROUP_ID, SELF_ASSIGNABLE_ROLES.GUILD_ID, SELF_ASSIGNABLE_ROLES.ROLE_ID, SELF_ASSIGNABLE_ROLES.EMOTE_ID);
		for(var role : roles){
			ctx.values(role.getGroupId(), role.getGuildId(), role.getRoleId(), role.getEmoteId());
		}
		ctx.execute();
	}

	public void removeSelfAssignableRoles(long guildId, Set<Long> roles){
		var settings = getSettings(guildId);
		if(settings != null){
			settings.removeSelfAssignableRoles(roles);
		}
		deleteSelfAssignableRoles(guildId, roles);
	}

	private void deleteSelfAssignableRoles(long guildId, Set<Long> roles){
		this.modules.get(DatabaseModule.class).getCtx().deleteFrom(SELF_ASSIGNABLE_ROLES).where(
				SELF_ASSIGNABLE_ROLES.ROLE_ID.in(roles)
		).execute();
	}

	public Set<SelfAssignableRoleGroup> getSelfAssignableRoleGroups(long guildId){
		return getSettings(guildId).getSelfAssignableRoleGroups();
	}

	public void addSelfAssignableRoleGroups(long guildId, Set<SelfAssignableRoleGroup> groups){
		insertSelfAssignableRoleGroups(guildId, groups);
		var settings = getSettings(guildId);
		if(settings != null){
			settings.addSelfAssignableRoleGroups(groups);
		}
	}

	private Set<SelfAssignableRoleGroup> insertSelfAssignableRoleGroups(long guildId, Set<SelfAssignableRoleGroup> groups){
		var dbModule = this.modules.get(DatabaseModule.class);
		var ctx = dbModule.getCtx().insertInto(SELF_ASSIGNABLE_ROLE_GROUPS)
				.columns(SELF_ASSIGNABLE_ROLE_GROUPS.GUILD_ID, SELF_ASSIGNABLE_ROLE_GROUPS.NAME, SELF_ASSIGNABLE_ROLE_GROUPS.MAX_ROLES);

		for(var group : groups){
			ctx.values(group.getGuildId(), group.getName(), group.getMaxRoles());
		}
		var res = ctx.returningResult(SELF_ASSIGNABLE_ROLE_GROUPS.ID).fetch();
		res.forEach(group ->
				groups.stream().filter(g ->
						g.getId() == group.get(SELF_ASSIGNABLE_ROLE_GROUPS.ID)
				).findFirst().ifPresent(grp -> grp.setId(group.get(SELF_ASSIGNABLE_ROLE_GROUPS.ID)))
		);
		return groups;
	}

	public void removeSelfAssignableRoleGroups(long guildId, Set<Long> groups){
		deleteSelfAssignableRoleGroups(guildId, groups);
		var settings = getSettings(guildId);
		if(settings != null){
			settings.removeSelfAssignableRoleGroups(groups);
		}
	}

	private void deleteSelfAssignableRoleGroups(long guildId, Set<Long> groups){
		this.modules.get(DatabaseModule.class).getCtx().deleteFrom(SELF_ASSIGNABLE_ROLE_GROUPS).where(
				SELF_ASSIGNABLE_ROLE_GROUPS.GUILD_ID.eq(guildId).and(SELF_ASSIGNABLE_ROLE_GROUPS.ID.in(groups))
		).execute();
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
		deleteInviteRole(guildId, code);
		insertInviteRoles(guildId, code, roles);
	}

	public void addInviteRoles(long guildId, String code, Set<Long> roles){
		var settings = getSettings(guildId);
		if(settings != null){
			settings.addInviteRoles(code, roles);
		}
		insertInviteRoles(guildId, code, roles);
	}

	public void removeInviteRoles(long guildId, String code, Set<Long> roles){
		var settings = getSettings(guildId);
		if(settings != null){
			settings.removeInviteRoles(code, roles);
		}
		if(roles.isEmpty()){
			deleteInviteRole(guildId, code);
			return;
		}
		deleteInviteRoles(guildId, code, roles);
	}

	private void deleteInviteRole(long guildId, String code){
		var dbModule = this.modules.get(DatabaseModule.class);
		var res = dbModule.getCtx().deleteFrom(GUILD_INVITES).where(
				GUILD_INVITES.GUILD_ID.eq(guildId).and(GUILD_INVITES.CODE.eq(code))).returningResult(GUILD_INVITES.ID).fetchOne();
		if(res == null){
			return;
		}
		var guildInviteId = res.get(GUILD_INVITES.ID);
		dbModule.getCtx().deleteFrom(GUILD_INVITE_ROLES).where(GUILD_INVITE_ROLES.GUILD_INVITE_ID.eq(guildInviteId)).execute();
	}

	private void deleteInviteRoles(long guildId, String code, Set<Long> roles){
		var dbModule = this.modules.get(DatabaseModule.class);
		try(var ctx = dbModule.getCtx().selectFrom(GUILD_INVITES)){
			var res = ctx.where(GUILD_INVITES.GUILD_ID.eq(guildId).and(GUILD_INVITES.CODE.eq(code))).fetchOne();
			if(res == null){
				return;
			}
			dbModule.getCtx().deleteFrom(GUILD_INVITE_ROLES).where(GUILD_INVITE_ROLES.GUILD_INVITE_ID.eq(res.get(GUILD_INVITES.ID)).and(GUILD_INVITE_ROLES.ROLE_ID.in(roles))).execute();
		}
	}

	private void insertInviteRoles(long guildId, String code, Set<Long> roles){
		var dbModule = this.modules.get(DatabaseModule.class);
		try(var selectCtx = dbModule.getCtx().selectFrom(GUILD_INVITES)){
			var res = selectCtx.where(GUILD_INVITES.GUILD_ID.eq(guildId).and(GUILD_INVITES.CODE.eq(code))).fetchOne();
			var inviteRoleId = 0L;
			if(res == null){
				var res2 = dbModule.getCtx().insertInto(GUILD_INVITES).columns(GUILD_INVITES.GUILD_ID, GUILD_INVITES.CODE).values(
						guildId, code).returningResult(GUILD_INVITES.ID).fetchOne();
				if(res2 == null){
					LOG.error("Cane we have a problem! Tickle Topi!");
					return;
				}
				inviteRoleId = res2.get(GUILD_INVITES.ID);
			}
			else{
				inviteRoleId = res.getId();
			}
			var ctx = dbModule.getCtx().insertInto(GUILD_INVITE_ROLES).columns(GUILD_INVITE_ROLES.ROLE_ID, GUILD_INVITE_ROLES.GUILD_INVITE_ID);
			for(var role : roles){
				ctx = ctx.values(role, inviteRoleId);
			}
			ctx.execute();
		}
	}

	public void removeInviteRole(long guildId, long roleId){
		var settings = getSettingsIfPresent(guildId);
		if(settings != null){
			settings.getInviteRoles().forEach((key, value) -> value.removeIf(r -> r == roleId));
		}
		this.modules.get(DatabaseModule.class).getCtx().deleteFrom(GUILD_INVITE_ROLES).where(GUILD_INVITE_ROLES.ROLE_ID.eq(roleId)).execute();
	}



	public void removeInviteRoles(long guildId, String code){
		var settings = getSettingsIfPresent(guildId);
		if(settings != null){
			settings.getInviteRoles().remove(code);
		}
		this.modules.get(DatabaseModule.class).getCtx().deleteFrom(GUILD_INVITES).where(GUILD_INVITES.CODE.eq(code)).execute();
	}

	public Set<Long> getBotIgnoredUsers(long guildId){
		return getSettings(guildId).getBotIgnoredUsers();
	}

	public void addBotIgnoredUsers(long guildId, Set<Long> users){
		var settings = getSettingsIfPresent(guildId);
		if(settings != null){
			settings.setBotIgnoredUsers(users, true);
		}
		insertBotIgnoredUsers(guildId, users);
	}

	private void insertBotIgnoredUsers(long guildId, Set<Long> users){
		var ctx = this.modules.get(DatabaseModule.class).getCtx().insertInto(BOT_IGNORED_MEMBERS).columns(BOT_IGNORED_MEMBERS.GUILD_ID, BOT_IGNORED_MEMBERS.USER_ID);
		for(var user : users){
			ctx.values(guildId, user);
		}
		ctx.execute();
	}

	public void removeBotIgnoredUsers(long guildId, Set<Long> users){
		var settings = getSettingsIfPresent(guildId);
		if(settings != null){
			settings.setBotIgnoredUsers(users, false);
		}
		deleteBotIgnoredUsers(guildId, users);
	}

	private void deleteBotIgnoredUsers(long guildId, Set<Long> users){
		this.modules.get(DatabaseModule.class).getCtx().deleteFrom(BOT_IGNORED_MEMBERS).where(
				BOT_IGNORED_MEMBERS.GUILD_ID.eq(guildId).and(BOT_IGNORED_MEMBERS.USER_ID.in(users))
		).execute();
	}

}
