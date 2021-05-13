package de.kittybot.kittybot.objects.settings.guild;

import de.kittybot.kittybot.jooq.tables.records.GuildsRecord;
import de.kittybot.kittybot.objects.settings.IGuildSettings;

public class AnnouncementGuildSettings implements IGuildSettings{

	private long streamAnnouncementChannelId;
	private String streamAnnouncementMessage;

	private long announcementChannelId;
	private String joinMessage;
	private boolean joinMessagesEnabled;

	private String leaveMessage;
	private boolean leaveMessagesEnabled;
	public AnnouncementGuildSettings(GuildsRecord record){
		this.streamAnnouncementChannelId = record.getStreamAnnouncementChannelId();
		this.streamAnnouncementMessage = record.getStreamAnnouncementMessage();
		this.announcementChannelId = record.getAnnouncementChannelId();
		this.joinMessage = record.getJoinMessage();
		this.joinMessagesEnabled = record.getJoinMessagesEnabled();
		this.leaveMessage = record.getLeaveMessage();
		this.leaveMessagesEnabled = record.getLeaveMessagesEnabled();
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

}
