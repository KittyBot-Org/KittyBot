package de.kittybot.kittybot.managers;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.objects.GuildSettings;
import net.dv8tion.jda.api.entities.Guild;
import org.jooq.Field;
import org.jooq.types.YearToSecond;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static de.kittybot.kittybot.jooq.Tables.GUILDS;

public class GuildSettingsManager{

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
		try(var con = dbManager.getCon(); var ctx = dbManager.getCtx(con).selectFrom(GUILDS)){
			var res = ctx.where(GUILDS.GUILD_ID.eq(guildId)).fetchOne();
			if(res != null){
				return new GuildSettings(res);
			}
		}
		catch(SQLException e){
			LOG.error("Error while retrieving guild settings for guild: " + guildId, e);
		}
		return null;
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
		LOG.debug("Registering new guild: {}", guild.getId());
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

	public void deleteGuildSettings(long guildId){
		LOG.debug("Deleting old guild: {}", guildId);
		var dbManager = this.main.getDatabaseManager();
		try(var con = dbManager.getCon()){
			dbManager.getCtx(con).deleteFrom(GUILDS).where(GUILDS.GUILD_ID.eq(guildId)).execute();
		}
		catch(SQLException e){
			LOG.error("Error deleting guild: {}", guildId, e);
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

}
