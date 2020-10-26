package de.kittybot.kittybot.cache;

import de.kittybot.kittybot.database.Database;
import de.kittybot.kittybot.objects.GuildSettings;
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.Map;

public class GuildSettingsCache{

	private static final Map<String, GuildSettings> SETTINGS = new HashMap<>();

	private GuildSettingsCache(){}

	public static GuildSettings getGuildSettings(String guildId){
		return SETTINGS.computeIfAbsent(guildId, k -> Database.getGuildSettings(guildId));
	}

	public static String getCommandPrefix(String guildId){
		return getGuildSettings(guildId).getCommandPrefix();
	}

	public static String getRequestChannelId(String guildId){
		return getGuildSettings(guildId).getRequestChannelId();
	}

	public static String getRequestChannel(String guildId){
		return getGuildSettings(guildId).getRequestChannel();
	}

	public static boolean areRequestsEnabled(String guildId){
		return getGuildSettings(guildId).areRequestsEnabled();
	}

	public static String getAnnouncementChannelId(String guildId){
		return getGuildSettings(guildId).getAnnouncementChannelId();
	}

	public static String getAnnouncementChannel(String guildId){
		return getGuildSettings(guildId).getAnnouncementChannel();
	}

	public static String getJoinMessage(String guildId){
		return getGuildSettings(guildId).getJoinMessage();
	}

	public static boolean areJoinMessagesEnabled(String guildId){
		return getGuildSettings(guildId).areJoinMessagesEnabled();
	}

	public static String getLeaveMessage(String guildId){
		return getGuildSettings(guildId).getLeaveMessage();
	}

	public static boolean areLeaveMessagesEnabled(String guildId){
		return getGuildSettings(guildId).areLeaveMessagesEnabled();
	}

	public static String getBoostMessage(String guildId){
		return getGuildSettings(guildId).getBoostMessage();
	}

	public static boolean areBoostMessagesEnabled(String guildId){
		return getGuildSettings(guildId).areBoostMessagesEnabled();
	}

	public static String getLogChannelId(String guildId){
		return getGuildSettings(guildId).getLogChannelId();
	}

	public static String getLogChannel(String guildId){
		return getGuildSettings(guildId).getLogChannel();
	}

	public static boolean areLogMessageEnabled(String guildId){
		return getGuildSettings(guildId).areLogMessageEnabled();
	}

	public static boolean isNSFWEnabled(String guildId){
		return getGuildSettings(guildId).isNSFWEnabled();
	}

	public static String getInactiveRoleId(String guildId){
		return getGuildSettings(guildId).getInactiveRoleId();
	}

	public static String getInactiveRole(String guildId){
		return getGuildSettings(guildId).getInactiveRole();
	}

	public static void setCommandPrefix(String guildId, String prefix){
		getGuildSettings(guildId).setCommandPrefix(prefix);
		Database.setCommandPrefix(guildId, prefix);
	}

	public static void setRequestChannelId(String guildId, String channelId){
		getGuildSettings(guildId).setRequestChannelId(channelId);
		Database.setRequestChannelId(guildId, channelId);
	}

	public static void setRequestsEnabled(String guildId, boolean enabled){
		getGuildSettings(guildId).setRequestsEnabled(enabled);
		Database.setRequestsEnabled(guildId, enabled);
	}

	public static void setAnnouncementChannelId(String guildId, String channelId){
		getGuildSettings(guildId).setAnnouncementChannelId(channelId);
		Database.setAnnouncementChannelId(guildId, channelId);
	}

	public static void setJoinMessage(String guildId, String message){
		getGuildSettings(guildId).setJoinMessage(message);
		Database.setJoinMessage(guildId, message);
	}

	public static void setJoinMessagesEnabled(String guildId, boolean enabled){
		getGuildSettings(guildId).setJoinMessagesEnabled(enabled);
		Database.setJoinMessageEnabled(guildId, enabled);
	}

	public static void setLeaveMessage(String guildId, String message){
		getGuildSettings(guildId).setLeaveMessage(message);
		Database.setLeaveMessage(guildId, message);
	}

	public static void setLeaveMessagesEnabled(String guildId, boolean enabled){
		getGuildSettings(guildId).setLeaveMessagesEnabled(enabled);
		Database.setLeaveMessageEnabled(guildId, enabled);
	}

	public static void setBoostMessage(String guildId, String message){
		getGuildSettings(guildId).setBoostMessage(message);
		Database.setBoostMessage(guildId, message);
	}

	public static void setBoostMessagesEnabled(String guildId, boolean enabled){
		getGuildSettings(guildId).setBoostMessagesEnabled(enabled);
		Database.setBoostMessageEnabled(guildId, enabled);
	}

	public static void setLogChannelId(String guildId, String channelId){
		getGuildSettings(guildId).setLogChannelId(channelId);
		Database.setLogChannelId(guildId, channelId);
	}

	public static void setLogMessagesEnabled(String guildId, boolean enabled){
		getGuildSettings(guildId).setLeaveMessagesEnabled(enabled);
		Database.setLogMessagesEnabled(guildId, enabled);
	}

	public static void setNSFWEnabled(String guildId, boolean enabled){
		getGuildSettings(guildId).setNSFWEnabled(enabled);
		Database.setNSFWEnabled(guildId, enabled);
	}

	public static void setInactiveRoleId(String guildId, String roleId){
		getGuildSettings(guildId).setInactiveRoleId(roleId);
		Database.setInactiveRoleId(guildId, roleId);
	}

	public static void pruneCache(Guild guild){
		SETTINGS.remove(guild.getId());
	}

}