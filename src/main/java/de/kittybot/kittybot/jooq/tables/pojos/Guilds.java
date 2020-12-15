/*
 * This file is generated by jOOQ.
 */
package de.kittybot.kittybot.jooq.tables.pojos;


import org.jooq.types.YearToSecond;

import java.io.Serializable;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class Guilds implements Serializable{

	private static final long serialVersionUID = 1L;

	private final Long guildId;
	private final String commandPrefix;
	private final Long announcementChannelId;
	private final Long requestChannelId;
	private final Boolean requestsEnabled;
	private final String joinMessage;
	private final Boolean joinMessagesEnabled;
	private final String leaveMessage;
	private final Boolean leaveMessagesEnabled;
	private final Long logChannelId;
	private final Boolean logMessagesEnabled;
	private final Boolean nsfwEnabled;
	private final Long inactiveRoleId;
	private final YearToSecond inactiveDuration;
	private final Long djRoleId;

	public Guilds(Guilds value){
		this.guildId = value.guildId;
		this.commandPrefix = value.commandPrefix;
		this.announcementChannelId = value.announcementChannelId;
		this.requestChannelId = value.requestChannelId;
		this.requestsEnabled = value.requestsEnabled;
		this.joinMessage = value.joinMessage;
		this.joinMessagesEnabled = value.joinMessagesEnabled;
		this.leaveMessage = value.leaveMessage;
		this.leaveMessagesEnabled = value.leaveMessagesEnabled;
		this.logChannelId = value.logChannelId;
		this.logMessagesEnabled = value.logMessagesEnabled;
		this.nsfwEnabled = value.nsfwEnabled;
		this.inactiveRoleId = value.inactiveRoleId;
		this.inactiveDuration = value.inactiveDuration;
		this.djRoleId = value.djRoleId;
	}

	public Guilds(
			Long guildId,
			String commandPrefix,
			Long announcementChannelId,
			Long requestChannelId,
			Boolean requestsEnabled,
			String joinMessage,
			Boolean joinMessagesEnabled,
			String leaveMessage,
			Boolean leaveMessagesEnabled,
			Long logChannelId,
			Boolean logMessagesEnabled,
			Boolean nsfwEnabled,
			Long inactiveRoleId,
			YearToSecond inactiveDuration,
			Long djRoleId
	){
		this.guildId = guildId;
		this.commandPrefix = commandPrefix;
		this.announcementChannelId = announcementChannelId;
		this.requestChannelId = requestChannelId;
		this.requestsEnabled = requestsEnabled;
		this.joinMessage = joinMessage;
		this.joinMessagesEnabled = joinMessagesEnabled;
		this.leaveMessage = leaveMessage;
		this.leaveMessagesEnabled = leaveMessagesEnabled;
		this.logChannelId = logChannelId;
		this.logMessagesEnabled = logMessagesEnabled;
		this.nsfwEnabled = nsfwEnabled;
		this.inactiveRoleId = inactiveRoleId;
		this.inactiveDuration = inactiveDuration;
		this.djRoleId = djRoleId;
	}

	/**
	 * Getter for <code>public.guilds.guild_id</code>.
	 */
	public Long getGuildId(){
		return this.guildId;
	}

	/**
	 * Getter for <code>public.guilds.command_prefix</code>.
	 */
	public String getCommandPrefix(){
		return this.commandPrefix;
	}

	/**
	 * Getter for <code>public.guilds.announcement_channel_id</code>.
	 */
	public Long getAnnouncementChannelId(){
		return this.announcementChannelId;
	}

	/**
	 * Getter for <code>public.guilds.request_channel_id</code>.
	 */
	public Long getRequestChannelId(){
		return this.requestChannelId;
	}

	/**
	 * Getter for <code>public.guilds.requests_enabled</code>.
	 */
	public Boolean getRequestsEnabled(){
		return this.requestsEnabled;
	}

	/**
	 * Getter for <code>public.guilds.join_message</code>.
	 */
	public String getJoinMessage(){
		return this.joinMessage;
	}

	/**
	 * Getter for <code>public.guilds.join_messages_enabled</code>.
	 */
	public Boolean getJoinMessagesEnabled(){
		return this.joinMessagesEnabled;
	}

	/**
	 * Getter for <code>public.guilds.leave_message</code>.
	 */
	public String getLeaveMessage(){
		return this.leaveMessage;
	}

	/**
	 * Getter for <code>public.guilds.leave_messages_enabled</code>.
	 */
	public Boolean getLeaveMessagesEnabled(){
		return this.leaveMessagesEnabled;
	}

	/**
	 * Getter for <code>public.guilds.log_channel_id</code>.
	 */
	public Long getLogChannelId(){
		return this.logChannelId;
	}

	/**
	 * Getter for <code>public.guilds.log_messages_enabled</code>.
	 */
	public Boolean getLogMessagesEnabled(){
		return this.logMessagesEnabled;
	}

	/**
	 * Getter for <code>public.guilds.nsfw_enabled</code>.
	 */
	public Boolean getNsfwEnabled(){
		return this.nsfwEnabled;
	}

	/**
	 * Getter for <code>public.guilds.inactive_role_id</code>.
	 */
	public Long getInactiveRoleId(){
		return this.inactiveRoleId;
	}

	/**
	 * Getter for <code>public.guilds.inactive_duration</code>.
	 */
	public YearToSecond getInactiveDuration(){
		return this.inactiveDuration;
	}

	/**
	 * Getter for <code>public.guilds.dj_role_id</code>.
	 */
	public Long getDjRoleId(){
		return this.djRoleId;
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder("Guilds (");

		sb.append(guildId);
		sb.append(", ").append(commandPrefix);
		sb.append(", ").append(announcementChannelId);
		sb.append(", ").append(requestChannelId);
		sb.append(", ").append(requestsEnabled);
		sb.append(", ").append(joinMessage);
		sb.append(", ").append(joinMessagesEnabled);
		sb.append(", ").append(leaveMessage);
		sb.append(", ").append(leaveMessagesEnabled);
		sb.append(", ").append(logChannelId);
		sb.append(", ").append(logMessagesEnabled);
		sb.append(", ").append(nsfwEnabled);
		sb.append(", ").append(inactiveRoleId);
		sb.append(", ").append(inactiveDuration);
		sb.append(", ").append(djRoleId);

		sb.append(")");
		return sb.toString();
	}

}
