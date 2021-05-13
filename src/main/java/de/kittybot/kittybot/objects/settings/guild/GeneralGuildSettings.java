package de.kittybot.kittybot.objects.settings.guild;

import de.kittybot.kittybot.jooq.tables.records.GuildsRecord;
import de.kittybot.kittybot.modules.DatabaseModule;
import de.kittybot.kittybot.objects.module.Modules;
import de.kittybot.kittybot.objects.settings.IGuildSettings;
import org.jooq.Field;
import org.jooq.Record;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class GeneralGuildSettings implements IGuildSettings{

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
	private boolean roleSaverEnabled;

	public GeneralGuildSettings(GuildsRecord record){
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
		this.roleSaverEnabled = record.getRoleSaverEnabled();
	}

	public long getStreamAnnouncementChannelId(){
		return this.streamAnnouncementChannelId;
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

	public void setAnnouncementChannelId(long announcementChannelId){
		this.announcementChannelId = announcementChannelId;
	}

	public long getRequestChannelId(){
		return this.requestChannelId;
	}

	public void setRequestChannelId(long requestChannelId){
		this.requestChannelId = requestChannelId;
	}

	public boolean isRequestsEnabled(){
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

	public boolean isJoinMessagesEnabled(){
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

	public boolean isLeaveMessagesEnabled(){
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

	public boolean isLogMessagesEnabled(){
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

	public boolean isRoleSaverEnabled(){
		return this.roleSaverEnabled;
	}

	public void setRoleSaverEnabled(boolean roleSaverEnabled){
		this.roleSaverEnabled = roleSaverEnabled;
	}

}
