package de.anteiku.kittybot.objects.cache;

import de.anteiku.kittybot.database.Database;
import de.anteiku.kittybot.objects.GuildSettings;
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.Map;

public class GuildSettingsCache{

	private static final Map<String, GuildSettings> SETTINGS = new HashMap<>();

	public static String getCommandPrefix(String guildId){
		return getGuildSettings(guildId).getCommandPrefix();
	}

	public static GuildSettings getGuildSettings(String guildId){
		return SETTINGS.computeIfAbsent(guildId, k -> Database.getGuildSettings(guildId));
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

	public static String getDJRoleId(String guildId){
		return getGuildSettings(guildId).getDJRoleId();
	}

	public static String getDJRole(String guildId){
		return getGuildSettings(guildId).getDJRole();
	}

	public static String getInactiveRoleId(String guildId){
		return getGuildSettings(guildId).getInactiveRoleId();
	}

	public static String getInactiveRole(String guildId){
		return getGuildSettings(guildId).getInactiveRole();
	}

	public static boolean setCommandPrefix(String guildId, String prefix){
		getGuildSettings(guildId).setCommandPrefix(prefix);
		return Database.setCommandPrefix(guildId, prefix);
	}

	public static boolean setRequestChannelId(String guildId, String channelId){
		getGuildSettings(guildId).setRequestChannelId(channelId);
		return Database.setRequestChannelId(guildId, channelId);
	}

	public static boolean setRequestsEnabled(String guildId, boolean enabled){
		getGuildSettings(guildId).setRequestsEnabled(enabled);
		return Database.setRequestsEnabled(guildId, enabled);
	}

	public static boolean setAnnouncementChannelId(String guildId, String channelId){
		getGuildSettings(guildId).setAnnouncementChannelId(channelId);
		return Database.setAnnouncementChannelId(guildId, channelId);
	}

	public static boolean setJoinMessage(String guildId, String message){
		getGuildSettings(guildId).setJoinMessage(message);
		return Database.setJoinMessage(guildId, message);
	}

	public static boolean setJoinMessagesEnabled(String guildId, boolean enabled){
		getGuildSettings(guildId).setJoinMessagesEnabled(enabled);
		return Database.setJoinMessageEnabled(guildId, enabled);
	}

	public static boolean setLeaveMessage(String guildId, String message){
		getGuildSettings(guildId).setLeaveMessage(message);
		return Database.setLeaveMessage(guildId, message);
	}

	public static boolean setLeaveMessagesEnabled(String guildId, boolean enabled){
		getGuildSettings(guildId).setLeaveMessagesEnabled(enabled);
		return Database.setLeaveMessageEnabled(guildId, enabled);
	}

	public static boolean setBoostMessage(String guildId, String message){
		getGuildSettings(guildId).setBoostMessage(message);
		return Database.setBoostMessage(guildId, message);
	}

	public static boolean setBoostMessagesEnabled(String guildId, boolean enabled){
		getGuildSettings(guildId).setBoostMessagesEnabled(enabled);
		return Database.setBoostMessageEnabled(guildId, enabled);
	}

	public static boolean setLogChannelId(String guildId, String channelId){
		getGuildSettings(guildId).setLogChannelId(channelId);
		return Database.setLogChannelId(guildId, channelId);
	}

	public static boolean setLogMessagesEnabled(String guildId, boolean enabled){
		getGuildSettings(guildId).setLeaveMessagesEnabled(enabled);
		return Database.setLogMessagesEnabled(guildId, enabled);
	}

	public static boolean setNSFWEnabled(String guildId, boolean enabled){
		getGuildSettings(guildId).setNSFWEnabled(enabled);
		return Database.setNSFWEnabled(guildId, enabled);
	}

	public static boolean setDJRoleId(String guildId, String roleId){
		getGuildSettings(guildId).setDJRoleId(roleId);
		return Database.setDJRoleId(guildId, roleId);
	}

	public static boolean setInactiveRoleId(String guildId, String roleId){
		getGuildSettings(guildId).setInactiveRoleId(roleId);
		return Database.setInactiveRoleId(guildId, roleId);
	}

	public static void pruneCache(Guild guild){
		SETTINGS.entrySet().removeIf(entry -> entry.getValue().getGuildId().equals(guild.getId()));
	}

}
