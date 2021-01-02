package de.kittybot.kittybot.objects;

import de.kittybot.kittybot.jooq.tables.records.GuildsRecord;
import de.kittybot.kittybot.utils.MessageUtils;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GuildSettings{

	private final long guildId;
	private final Set<Long> snipeDisabledChannels;
	private final Set<Long> botDisabledChannels;
	private final Set<Long> botIgnoredUsers;
	private final Set<SelfAssignableRole> selfAssignableRoles;
	private final Set<SelfAssignableRoleGroup> selfAssignableRoleGroups;
	private final Map<String, Set<Long>> guildInviteRoles;
	private String commandPrefix;
	private long streamAnnouncementChannelId;
	private String streamAnnouncementMessage;
	private long announcementChannelId;
	private long requestChannelId;
	private boolean requestsEnabled;
	private String joinMessage;
	private boolean joinMessagesEnabled;
	private String leaveMessage;
	private boolean leaveMessagesEnabled;
	private long logChannelId;
	private boolean logMessagesEnabled;
	private boolean nsfwEnabled;
	private long inactiveRoleId;
	private Duration inactiveDuration;
	private long djRoleId;
	private boolean snipesEnabled;

	public GuildSettings(GuildsRecord record, Set<Long> snipeDisabledChannels, Set<Long> botDisabledChannels, Set<Long> botIgnoredUsers, Set<SelfAssignableRole> selfAssignableRoles, Set<SelfAssignableRoleGroup> selfAssignableRoleGroups, Map<String, Set<Long>> guildInviteRoles){
		this.guildId = record.getGuildId();
		this.commandPrefix = record.getCommandPrefix();
		this.streamAnnouncementChannelId = record.getStreamAnnouncementChannelId();
		this.streamAnnouncementMessage = record.getStreamAnnouncementMessage();
		this.announcementChannelId = record.getAnnouncementChannelId();
		this.requestChannelId = record.getRequestChannelId();
		this.requestsEnabled = record.getRequestsEnabled();
		this.joinMessage = record.getJoinMessage();
		this.joinMessagesEnabled = record.getJoinMessagesEnabled();
		this.leaveMessage = record.getLeaveMessage();
		this.leaveMessagesEnabled = record.getLeaveMessagesEnabled();
		this.logChannelId = record.getLogChannelId();
		this.logMessagesEnabled = record.getLogMessagesEnabled();
		this.nsfwEnabled = record.getNsfwEnabled();
		this.inactiveRoleId = record.getInactiveRoleId();
		this.inactiveDuration = record.getInactiveDuration().toDuration();
		this.djRoleId = record.getDjRoleId();
		this.snipesEnabled = record.getSnipesEnabled();
		this.snipeDisabledChannels = snipeDisabledChannels == null ? new HashSet<>() : snipeDisabledChannels;
		this.botDisabledChannels = botDisabledChannels == null ? new HashSet<>() : botDisabledChannels;
		this.botIgnoredUsers = botIgnoredUsers == null ? new HashSet<>() : botIgnoredUsers;
		this.selfAssignableRoles = selfAssignableRoles == null ? new HashSet<>() : selfAssignableRoles;
		this.selfAssignableRoleGroups = selfAssignableRoleGroups == null ? new HashSet<>() : selfAssignableRoleGroups;
		this.guildInviteRoles = guildInviteRoles == null ? new HashMap<>() : guildInviteRoles;
	}

	public long getGuildId(){
		return this.guildId;
	}

	public String getCommandPrefix(){
		return this.commandPrefix;
	}

	public void setCommandPrefix(String commandPrefix){
		this.commandPrefix = commandPrefix;
	}

	public long getStreamAnnouncementChannelId(){
		return this.streamAnnouncementChannelId;
	}

	public String getStreamAnnouncementChannel(){
		return MessageUtils.getChannelMention(streamAnnouncementChannelId);
	}

	public void setStreamAnnouncementChannelId(long streamAnnouncementChannelId){
		this.streamAnnouncementChannelId = streamAnnouncementChannelId;
	}

	public String getStreamAnnouncementMessage(){
		return this.streamAnnouncementMessage;
	}

	public void setStreamAnnouncementMessage(String streamAnnouncementMessage){
		this.streamAnnouncementMessage = streamAnnouncementMessage;
	}

	public long getAnnouncementChannelId(){
		return this.announcementChannelId;
	}

	public String getAnnouncementChannel(){
		return MessageUtils.getChannelMention(announcementChannelId);
	}

	public void setAnnouncementChannelId(long announcementChannelId){
		this.announcementChannelId = announcementChannelId;
	}

	public long getRequestChannelId(){
		return this.requestChannelId;
	}

	public void setRequestChannelId(long requestChannelId){
		this.requestChannelId = requestChannelId;
	}

	public String getRequestChannel(){
		return MessageUtils.getChannelMention(requestChannelId);
	}

	public boolean areRequestsEnabled(){
		return this.requestsEnabled;
	}

	public void setRequestsEnabled(boolean requestsEnabled){
		this.requestsEnabled = requestsEnabled;
	}

	public String getJoinMessage(){
		return this.joinMessage;
	}

	public void setJoinMessage(String joinMessage){
		this.joinMessage = joinMessage;
	}

	public boolean areJoinMessagesEnabled(){
		return this.joinMessagesEnabled;
	}

	public void setJoinMessagesEnabled(boolean joinMessagesEnabled){
		this.joinMessagesEnabled = joinMessagesEnabled;
	}

	public String getLeaveMessage(){
		return this.leaveMessage;
	}

	public void setLeaveMessage(String leaveMessage){
		this.leaveMessage = leaveMessage;
	}

	public boolean areLeaveMessagesEnabled(){
		return this.leaveMessagesEnabled;
	}

	public void setLeaveMessagesEnabled(boolean leaveMessagesEnabled){
		this.leaveMessagesEnabled = leaveMessagesEnabled;
	}

	public long getLogChannelId(){
		return this.logChannelId;
	}

	public void setLogChannelId(long logChannelId){
		this.logChannelId = logChannelId;
	}

	public String getLogChannel(){
		return MessageUtils.getChannelMention(logChannelId);
	}

	public boolean areLogMessagesEnabled(){
		return this.logMessagesEnabled;
	}

	public void setLogMessagesEnabled(boolean logMessagesEnabled){
		this.logMessagesEnabled = logMessagesEnabled;
	}

	public boolean isNsfwEnabled(){
		return this.nsfwEnabled;
	}

	public void setNsfwEnabled(boolean nsfwEnabled){
		this.nsfwEnabled = nsfwEnabled;
	}

	public long getInactiveRoleId(){
		return this.inactiveRoleId;
	}

	public void setInactiveRoleId(long inactiveRoleId){
		this.inactiveRoleId = inactiveRoleId;
	}

	public String getInactiveRole(){
		return MessageUtils.getRoleMention(inactiveRoleId);
	}

	public Duration getInactiveDuration(){
		return this.inactiveDuration;
	}

	public void setInactiveDuration(Duration inactiveDuration){
		this.inactiveDuration = inactiveDuration;
	}

	public long getDjRoleId(){
		return this.djRoleId;
	}

	public void setDjRoleId(long djRoleId){
		this.djRoleId = djRoleId;
	}

	public String getDjRole(){
		return MessageUtils.getRoleMention(this.djRoleId);
	}

	public void setSnipesEnabled(boolean enabled){
		this.snipesEnabled = enabled;
	}

	public boolean areSnipesEnabled(){
		return this.snipesEnabled;
	}

	public boolean areSnipesDisabledInChannel(long channelId){
		return this.snipeDisabledChannels.contains(channelId);
	}

	public void setSnipesDisabledInChannel(long channelId, boolean disable){
		if(disable){
			this.snipeDisabledChannels.add(channelId);
			return;
		}
		this.snipeDisabledChannels.remove(channelId);
	}

	public Set<Long> getSnipeDisabledChannels(){
		return this.snipeDisabledChannels;
	}

	public boolean isBotDisabledInChannel(long channelId){
		return this.botDisabledChannels.contains(channelId);
	}

	public void setBotDisabledInChannel(long channelId, boolean enabled){
		if(enabled){
			this.botDisabledChannels.add(channelId);
			return;
		}
		this.botDisabledChannels.remove(channelId);
	}

	public Set<Long> getBotDisabledChannels(){
		return this.botDisabledChannels;
	}

	public boolean isBotIgnoredUser(long userId){
		return this.botIgnoredUsers.contains(userId);
	}

	public void setBotIgnoredUsers(Set<Long> userIds, boolean ignored){
		if(ignored){
			this.botIgnoredUsers.addAll(userIds);
			return;
		}
		this.botIgnoredUsers.removeAll(userIds);
	}

	public Set<Long> getBotIgnoredUsers(){
		return botIgnoredUsers;
	}

	public Set<SelfAssignableRole> getSelfAssignableRoles(){
		return this.selfAssignableRoles;
	}

	public void addSelfAssignableRoles(Set<SelfAssignableRole> roles){
		this.selfAssignableRoles.addAll(roles);
	}

	public void removeSelfAssignableRoles(Set<Long> roles){
		this.selfAssignableRoles.removeIf(role -> roles.contains(role.getRoleId()));
	}

	public Set<SelfAssignableRoleGroup> getSelfAssignableRoleGroups(){
		return this.selfAssignableRoleGroups;
	}

	public void addSelfAssignableRoleGroups(Set<SelfAssignableRoleGroup> groups){
		this.selfAssignableRoleGroups.addAll(groups);
	}

	public void removeSelfAssignableRoleGroups(Set<Long> groups){
		this.selfAssignableRoleGroups.removeIf(group -> groups.contains(group.getId()));
	}

	public Map<String, Set<Long>> getInviteRoles(){
		return this.guildInviteRoles;
	}

	public void setInviteRoles(String code, Set<Long> roles){
		if(roles.isEmpty()){
			this.guildInviteRoles.remove(code);
			return;
		}
		this.guildInviteRoles.put(code, roles);
	}

	public Set<Long> getInviteRoles(String code){
		return this.guildInviteRoles.get(code);
	}

}
