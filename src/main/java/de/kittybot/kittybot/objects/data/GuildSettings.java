package de.kittybot.kittybot.objects.data;

import de.kittybot.kittybot.utils.MessageUtils;

public class GuildSettings{

	private String commandPrefix;
	private String requestChannelId;
	private boolean requestsEnabled;
	private String announcementChannelId;
	private String joinMessages;
	private boolean joinMessagesEnabled;
	private String leaveMessages;
	private boolean leaveMessagesEnabled;
	private String boostMessages;
	private boolean boostMessagesEnabled;
	private String logChannelId;
	private boolean logMessageEnabled;
	private boolean nsfwEnabled;
	private String inactiveRoleId;

	public GuildSettings(String commandPrefix, String requestChannelId, boolean requestsEnabled, String announcementChannelId, String joinMessages, boolean joinMessagesEnabled,
	                     String leaveMessages, boolean leaveMessagesEnabled, String boostMessages, boolean boostMessagesEnabled, String logChannelId, boolean logMessageEnabled, boolean nsfwEnabled,
	                     String inactiveRoleId){
		this.commandPrefix = commandPrefix;
		this.requestChannelId = requestChannelId;
		this.requestsEnabled = requestsEnabled;
		this.announcementChannelId = announcementChannelId;
		this.joinMessages = joinMessages;
		this.joinMessagesEnabled = joinMessagesEnabled;
		this.leaveMessages = leaveMessages;
		this.leaveMessagesEnabled = leaveMessagesEnabled;
		this.boostMessages = boostMessages;
		this.boostMessagesEnabled = boostMessagesEnabled;
		this.logChannelId = logChannelId;
		this.logMessageEnabled = logMessageEnabled;
		this.nsfwEnabled = nsfwEnabled;
		this.inactiveRoleId = inactiveRoleId;
	}

	public String getCommandPrefix(){
		return commandPrefix;
	}

	public void setCommandPrefix(String commandPrefix){
		this.commandPrefix = commandPrefix;
	}

	public String getRequestChannelId(){
		return requestChannelId;
	}

	public void setRequestChannelId(String channelId){
		this.requestChannelId = channelId;
	}

	public String getRequestChannel(){
		return MessageUtils.getChannelMention(requestChannelId);
	}

	public boolean areRequestsEnabled(){
		return requestsEnabled;
	}

	public String getAnnouncementChannelId(){
		return announcementChannelId;
	}

	public void setAnnouncementChannelId(String channelId){
		this.announcementChannelId = channelId;
	}

	public String getAnnouncementChannel(){
		return MessageUtils.getChannelMention(announcementChannelId);
	}

	public String getJoinMessage(){
		return joinMessages;
	}

	public void setJoinMessage(String message){
		this.joinMessages = message;
	}

	public boolean areJoinMessagesEnabled(){
		return joinMessagesEnabled;
	}

	public String getLeaveMessage(){
		return leaveMessages;
	}

	public void setLeaveMessage(String message){
		this.leaveMessages = message;
	}

	public boolean areLeaveMessagesEnabled(){
		return leaveMessagesEnabled;
	}

	public String getBoostMessage(){
		return boostMessages;
	}

	public void setBoostMessage(String message){
		this.boostMessages = message;
	}

	public boolean areBoostMessagesEnabled(){
		return boostMessagesEnabled;
	}

	public String getLogChannelId(){
		return logChannelId;
	}

	public void setLogChannelId(String channelId){
		this.logChannelId = channelId;
	}

	public String getLogChannel(){
		return MessageUtils.getChannelMention(logChannelId);
	}

	public boolean areLogMessageEnabled(){
		return logMessageEnabled;
	}

	public boolean isNSFWEnabled(){
		return nsfwEnabled;
	}

	public void setNSFWEnabled(boolean enabled){
		this.nsfwEnabled = enabled;
	}

	public String getInactiveRoleId(){
		return inactiveRoleId;
	}

	public void setInactiveRoleId(String role){
		this.inactiveRoleId = role;
	}

	public String getInactiveRole(){
		return MessageUtils.getRoleMention(inactiveRoleId);
	}

	public void setRequestsEnabled(boolean enabled){
		this.requestsEnabled = enabled;
	}

	public void setJoinMessagesEnabled(boolean enabled){
		this.joinMessagesEnabled = enabled;
	}

	public void setLeaveMessagesEnabled(boolean enabled){
		this.leaveMessagesEnabled = enabled;
	}

	public void setBoostMessagesEnabled(boolean enabled){
		this.boostMessagesEnabled = enabled;
	}

	public void setLogMessageEnabled(boolean enabled){
		this.logMessageEnabled = enabled;
	}

}
