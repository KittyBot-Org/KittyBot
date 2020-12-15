package de.kittybot.kittybot.objects;

import de.kittybot.kittybot.jooq.tables.records.GuildsRecord;
import de.kittybot.kittybot.utils.MessageUtils;

import java.time.Duration;

public class GuildSettings{

	private final long guildId;
	private String commandPrefix;
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

	public GuildSettings(GuildsRecord record){
		this.guildId = record.getGuildId();
		this.commandPrefix = record.getCommandPrefix();
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

	public String getRequestChannel(){
		return MessageUtils.getChannelMention(requestChannelId);
	}

	public void setRequestChannelId(long requestChannelId){
		this.requestChannelId = requestChannelId;
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

	public String getLogChannel(){
		return MessageUtils.getChannelMention(logChannelId);
	}

	public void setLogChannelId(long logChannelId){
		this.logChannelId = logChannelId;
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

	public String getInactiveRole(){
		return MessageUtils.getRoleMention(inactiveRoleId);
	}

	public void setInactiveRoleId(long inactiveRoleId){
		this.inactiveRoleId = inactiveRoleId;
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

	public String getDjRole(){
		return MessageUtils.getRoleMention(djRoleId);
	}

	public void setDjRoleId(long djRoleId){
		this.djRoleId = djRoleId;
	}

}
