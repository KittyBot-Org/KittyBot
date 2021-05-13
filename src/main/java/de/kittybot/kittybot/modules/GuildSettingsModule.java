package de.kittybot.kittybot.modules;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import de.kittybot.kittybot.jooq.tables.records.BotDisabledChannelsRecord;
import de.kittybot.kittybot.jooq.tables.records.BotIgnoredMembersRecord;
import de.kittybot.kittybot.jooq.tables.records.SnipeDisabledChannelsRecord;
import de.kittybot.kittybot.objects.module.Module;
import de.kittybot.kittybot.objects.settings.IGuildSettings;
import de.kittybot.kittybot.objects.settings.SelfAssignableRole;
import de.kittybot.kittybot.objects.settings.SelfAssignableRoleGroup;
import de.kittybot.kittybot.objects.settings.guild.BlacklistGuildSettings;
import de.kittybot.kittybot.objects.settings.guild.GeneralGuildSettings;
import de.kittybot.kittybot.objects.settings.guild.RoleGuildSettings;
import de.kittybot.kittybot.objects.settings.guild.SnipesGuildSettings;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import org.jetbrains.annotations.NotNull;
import org.jooq.types.YearToSecond;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static de.kittybot.kittybot.jooq.Tables.*;

public class GuildSettingsModule extends Module{

	private static final Logger LOG = LoggerFactory.getLogger(GuildSettingsModule.class);

	private LoadingCache<Long, Map<Class<? extends IGuildSettings>, IGuildSettings>> guildSettings;

	@Override
	public Set<Class<? extends Module>> getDependencies(){
		return Set.of(DatabaseModule.class);
	}

	@Override
	public void onEnable(){
		this.guildSettings = Caffeine.newBuilder()
			.expireAfterAccess(30, TimeUnit.MINUTES)
			.build(this::retrieveGuildSettings);
	}

	@Override
	public void onGuildJoin(@Nonnull GuildJoinEvent event){
		registerGuild(event.getGuild());
	}

	@Override
	public void onGuildReady(@NotNull GuildReadyEvent event){
		registerGuild(event.getGuild());
	}

	@Override
	public void onGuildLeave(@Nonnull GuildLeaveEvent event){
		var guildId = event.getGuild().getIdLong();
		LOG.info("Cleaning up guild: {}", guildId);
		this.modules.get(DatabaseModule.class).getCtx().deleteFrom(GUILDS).where(GUILDS.ID.eq(guildId)).execute();
	}

	public Map<Class<? extends IGuildSettings>, IGuildSettings> retrieveGuildSettings(long guildId){
		var settings = new HashMap<Class<? extends IGuildSettings>, IGuildSettings>();
		var dbModule = this.modules.get(DatabaseModule.class);

		try(
			var guildSettingsCtx = dbModule.getCtx().selectFrom(GUILDS);
			var snipesSettingsCtx = dbModule.getCtx().selectFrom(SNIPE_DISABLED_CHANNELS);
			var botDisabledChannelsCtx = dbModule.getCtx().selectFrom(BOT_DISABLED_CHANNELS);
			var botIgnoredMembersCtx = dbModule.getCtx().selectFrom(BOT_IGNORED_MEMBERS);
			var selfAssignableRolesCtx = dbModule.getCtx().selectFrom(SELF_ASSIGNABLE_ROLES);
			var selfAssignableRoleGroupsCtx = dbModule.getCtx().selectFrom(SELF_ASSIGNABLE_ROLE_GROUPS);
			var guildInviteRolesCtx = dbModule.getCtx().select()
		){
			var guildsRecord = guildSettingsCtx.where(GUILDS.ID.eq(guildId)).fetchOne();
			if(guildsRecord == null){
				return null;
			}

			settings.put(GeneralGuildSettings.class, new GeneralGuildSettings(guildsRecord));
			settings.put(SnipesGuildSettings.class, new SnipesGuildSettings(
				guildsRecord,
				snipesSettingsCtx.where(SNIPE_DISABLED_CHANNELS.GUILD_ID.eq(guildId)).fetch(SnipeDisabledChannelsRecord::getChannelId)
			));
			settings.put(BlacklistGuildSettings.class, new BlacklistGuildSettings(
				botDisabledChannelsCtx.where(BOT_DISABLED_CHANNELS.GUILD_ID.eq(guildId)).fetch(BotDisabledChannelsRecord::getChannelId),
				botIgnoredMembersCtx.where(BOT_IGNORED_MEMBERS.GUILD_ID.eq(guildId)).fetch(BotIgnoredMembersRecord::getUserId)
			));
			settings.put(RoleGuildSettings.class, new RoleGuildSettings(
				selfAssignableRolesCtx.where(SELF_ASSIGNABLE_ROLES.GUILD_ID.eq(guildId)).fetch().map(SelfAssignableRole::new),
				selfAssignableRoleGroupsCtx.where(SELF_ASSIGNABLE_ROLE_GROUPS.GUILD_ID.eq(guildId)).fetch().map(SelfAssignableRoleGroup::new),
				guildInviteRolesCtx.from(GUILD_INVITES).join(GUILD_INVITE_ROLES).on(GUILD_INVITES.ID.eq(GUILD_INVITE_ROLES.GUILD_INVITE_ID)).where(GUILD_INVITES.GUILD_ID.eq(guildId)).fetch()
					.stream().collect(Collectors.groupingBy(record -> record.get(GUILD_INVITES.CODE), Collectors.mapping(record -> record.get(GUILD_INVITE_ROLES.ROLE_ID), Collectors.toSet())))
			));
		}
		return settings;
	}

	public void registerGuild(Guild guild){
		LOG.info("Registering new guild: {}", guild.getId());
		this.modules.get(DatabaseModule.class).getCtx()
			.insertInto(GUILDS)
			.columns(
				GUILDS.ID,
				GUILDS.ANNOUNCEMENT_CHANNEL_ID,
				GUILDS.INACTIVE_DURATION
			)
			.values(
				guild.getIdLong(),
				guild.getDefaultChannel() == null ? -1 : guild.getDefaultChannel().getIdLong(),
				YearToSecond.valueOf(Duration.ofDays(3))
			)
			.onDuplicateKeyIgnore()
			.execute();
	}

	public Map<Class<? extends IGuildSettings>, IGuildSettings> get(long guildId){
		return this.guildSettings.get(guildId);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> clazz, long guildId){
		var settings = this.guildSettings.get(guildId);
		if(settings == null){
			return null;
		}
		return (T) settings.get(clazz);
	}

	public GeneralGuildSettings getGeneralGuildSettings(long guildId){
		return get(GeneralGuildSettings.class, guildId);
	}

	public SnipesGuildSettings getSnipeGuildSettings(long guildId){
		return get(SnipesGuildSettings.class, guildId);
	}

}
