/*
 * This file is generated by jOOQ.
 */
package de.kittybot.kittybot.database.jooq.tables.records;


import de.kittybot.kittybot.database.jooq.tables.Guilds;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record15;
import org.jooq.Row15;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class GuildsRecord extends UpdatableRecordImpl<GuildsRecord> implements Record15<String, String, String, Boolean, String, String, Boolean, String, Boolean, String, Boolean, String, Boolean, Boolean, String>{

	private static final long serialVersionUID = -146101540;

	/**
	 * Create a detached GuildsRecord
	 */
	public GuildsRecord(){
		super(Guilds.GUILDS);
	}

	/**
	 * Create a detached, initialised GuildsRecord
	 */
	public GuildsRecord(String guildId, String commandPrefix, String requestChannelId, Boolean requestsEnabled, String announcementChannelId, String joinMessages, Boolean joinMessagesEnabled, String leaveMessages, Boolean leaveMessagesEnabled, String boostMessages, Boolean boostMessagesEnabled, String logChannelId, Boolean logMessagesEnabled, Boolean nsfwEnabled, String inactiveRoleId){
		super(Guilds.GUILDS);

		set(0, guildId);
		set(1, commandPrefix);
		set(2, requestChannelId);
		set(3, requestsEnabled);
		set(4, announcementChannelId);
		set(5, joinMessages);
		set(6, joinMessagesEnabled);
		set(7, leaveMessages);
		set(8, leaveMessagesEnabled);
		set(9, boostMessages);
		set(10, boostMessagesEnabled);
		set(11, logChannelId);
		set(12, logMessagesEnabled);
		set(13, nsfwEnabled);
		set(14, inactiveRoleId);
	}

	@Override
	public Record1<String> key(){
		return (Record1) super.key();
	}

	@Override
	public Row15<String, String, String, Boolean, String, String, Boolean, String, Boolean, String, Boolean, String, Boolean, Boolean, String> fieldsRow(){
		return (Row15) super.fieldsRow();
	}

	@Override
	public Row15<String, String, String, Boolean, String, String, Boolean, String, Boolean, String, Boolean, String, Boolean, Boolean, String> valuesRow(){
		return (Row15) super.valuesRow();
	}

	@Override
	public Field<String> field1(){
		return Guilds.GUILDS.GUILD_ID;
	}

	@Override
	public Field<String> field2(){
		return Guilds.GUILDS.COMMAND_PREFIX;
	}

	@Override
	public Field<String> field3(){
		return Guilds.GUILDS.REQUEST_CHANNEL_ID;
	}

	@Override
	public Field<Boolean> field4(){
		return Guilds.GUILDS.REQUESTS_ENABLED;
	}

	@Override
	public Field<String> field5(){
		return Guilds.GUILDS.ANNOUNCEMENT_CHANNEL_ID;
	}

	@Override
	public Field<String> field6(){
		return Guilds.GUILDS.JOIN_MESSAGES;
	}

	@Override
	public Field<Boolean> field7(){
		return Guilds.GUILDS.JOIN_MESSAGES_ENABLED;
	}

	@Override
	public Field<String> field8(){
		return Guilds.GUILDS.LEAVE_MESSAGES;
	}

	@Override
	public Field<Boolean> field9(){
		return Guilds.GUILDS.LEAVE_MESSAGES_ENABLED;
	}

	@Override
	public Field<String> field10(){
		return Guilds.GUILDS.BOOST_MESSAGES;
	}

	@Override
	public Field<Boolean> field11(){
		return Guilds.GUILDS.BOOST_MESSAGES_ENABLED;
	}

	@Override
	public Field<String> field12(){
		return Guilds.GUILDS.LOG_CHANNEL_ID;
	}

	@Override
	public Field<Boolean> field13(){
		return Guilds.GUILDS.LOG_MESSAGES_ENABLED;
	}

	@Override
	public Field<Boolean> field14(){
		return Guilds.GUILDS.NSFW_ENABLED;
	}

	@Override
	public Field<String> field15(){
		return Guilds.GUILDS.INACTIVE_ROLE_ID;
	}

	@Override
	public String value1(){
		return getGuildId();
	}

	@Override
	public String value2(){
		return getCommandPrefix();
	}

	@Override
	public String value3(){
		return getRequestChannelId();
	}

	@Override
	public Boolean value4(){
		return getRequestsEnabled();
	}

	@Override
	public String value5(){
		return getAnnouncementChannelId();
	}

	@Override
	public String value6(){
		return getJoinMessages();
	}

	@Override
	public Boolean value7(){
		return getJoinMessagesEnabled();
	}

	@Override
	public String value8(){
		return getLeaveMessages();
	}

	@Override
	public Boolean value9(){
		return getLeaveMessagesEnabled();
	}

	@Override
	public String value10(){
		return getBoostMessages();
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	@Override
	public Boolean value11(){
		return getBoostMessagesEnabled();
	}

	// -------------------------------------------------------------------------
	// Record15 type implementation
	// -------------------------------------------------------------------------

	@Override
	public String value12(){
		return getLogChannelId();
	}

	@Override
	public Boolean value13(){
		return getLogMessagesEnabled();
	}

	@Override
	public Boolean value14(){
		return getNsfwEnabled();
	}

	@Override
	public String value15(){
		return getInactiveRoleId();
	}

	@Override
	public GuildsRecord value1(String value){
		setGuildId(value);
		return this;
	}

	@Override
	public GuildsRecord value2(String value){
		setCommandPrefix(value);
		return this;
	}

	@Override
	public GuildsRecord value3(String value){
		setRequestChannelId(value);
		return this;
	}

	@Override
	public GuildsRecord value4(Boolean value){
		setRequestsEnabled(value);
		return this;
	}

	@Override
	public GuildsRecord value5(String value){
		setAnnouncementChannelId(value);
		return this;
	}

	@Override
	public GuildsRecord value6(String value){
		setJoinMessages(value);
		return this;
	}

	@Override
	public GuildsRecord value7(Boolean value){
		setJoinMessagesEnabled(value);
		return this;
	}

	@Override
	public GuildsRecord value8(String value){
		setLeaveMessages(value);
		return this;
	}

	@Override
	public GuildsRecord value9(Boolean value){
		setLeaveMessagesEnabled(value);
		return this;
	}

	@Override
	public GuildsRecord value10(String value){
		setBoostMessages(value);
		return this;
	}

	@Override
	public GuildsRecord value11(Boolean value){
		setBoostMessagesEnabled(value);
		return this;
	}

	@Override
	public GuildsRecord value12(String value){
		setLogChannelId(value);
		return this;
	}

	@Override
	public GuildsRecord value13(Boolean value){
		setLogMessagesEnabled(value);
		return this;
	}

	@Override
	public GuildsRecord value14(Boolean value){
		setNsfwEnabled(value);
		return this;
	}

	@Override
	public GuildsRecord value15(String value){
		setInactiveRoleId(value);
		return this;
	}

	@Override
	public GuildsRecord values(String value1, String value2, String value3, Boolean value4, String value5, String value6, Boolean value7, String value8, Boolean value9, String value10, Boolean value11, String value12, Boolean value13, Boolean value14, String value15){
		value1(value1);
		value2(value2);
		value3(value3);
		value4(value4);
		value5(value5);
		value6(value6);
		value7(value7);
		value8(value8);
		value9(value9);
		value10(value10);
		value11(value11);
		value12(value12);
		value13(value13);
		value14(value14);
		value15(value15);
		return this;
	}

	@Override
	public String component1(){
		return getGuildId();
	}

	/**
	 * Getter for <code>public.guilds.guild_id</code>.
	 */
	public String getGuildId(){
		return (String) get(0);
	}

	/**
	 * Setter for <code>public.guilds.guild_id</code>.
	 */
	public GuildsRecord setGuildId(String value){
		set(0, value);
		return this;
	}

	@Override
	public String component2(){
		return getCommandPrefix();
	}

	/**
	 * Getter for <code>public.guilds.command_prefix</code>.
	 */
	public String getCommandPrefix(){
		return (String) get(1);
	}

	/**
	 * Setter for <code>public.guilds.command_prefix</code>.
	 */
	public GuildsRecord setCommandPrefix(String value){
		set(1, value);
		return this;
	}

	@Override
	public String component3(){
		return getRequestChannelId();
	}

	/**
	 * Getter for <code>public.guilds.request_channel_id</code>.
	 */
	public String getRequestChannelId(){
		return (String) get(2);
	}

	/**
	 * Setter for <code>public.guilds.request_channel_id</code>.
	 */
	public GuildsRecord setRequestChannelId(String value){
		set(2, value);
		return this;
	}

	@Override
	public Boolean component4(){
		return getRequestsEnabled();
	}

	/**
	 * Getter for <code>public.guilds.requests_enabled</code>.
	 */
	public Boolean getRequestsEnabled(){
		return (Boolean) get(3);
	}

	/**
	 * Setter for <code>public.guilds.requests_enabled</code>.
	 */
	public GuildsRecord setRequestsEnabled(Boolean value){
		set(3, value);
		return this;
	}

	@Override
	public String component5(){
		return getAnnouncementChannelId();
	}

	/**
	 * Getter for <code>public.guilds.announcement_channel_id</code>.
	 */
	public String getAnnouncementChannelId(){
		return (String) get(4);
	}

	/**
	 * Setter for <code>public.guilds.announcement_channel_id</code>.
	 */
	public GuildsRecord setAnnouncementChannelId(String value){
		set(4, value);
		return this;
	}

	@Override
	public String component6(){
		return getJoinMessages();
	}

	/**
	 * Getter for <code>public.guilds.join_messages</code>.
	 */
	public String getJoinMessages(){
		return (String) get(5);
	}

	/**
	 * Setter for <code>public.guilds.join_messages</code>.
	 */
	public GuildsRecord setJoinMessages(String value){
		set(5, value);
		return this;
	}

	@Override
	public Boolean component7(){
		return getJoinMessagesEnabled();
	}

	/**
	 * Getter for <code>public.guilds.join_messages_enabled</code>.
	 */
	public Boolean getJoinMessagesEnabled(){
		return (Boolean) get(6);
	}

	/**
	 * Setter for <code>public.guilds.join_messages_enabled</code>.
	 */
	public GuildsRecord setJoinMessagesEnabled(Boolean value){
		set(6, value);
		return this;
	}

	@Override
	public String component8(){
		return getLeaveMessages();
	}

	/**
	 * Getter for <code>public.guilds.leave_messages</code>.
	 */
	public String getLeaveMessages(){
		return (String) get(7);
	}

	/**
	 * Setter for <code>public.guilds.leave_messages</code>.
	 */
	public GuildsRecord setLeaveMessages(String value){
		set(7, value);
		return this;
	}

	@Override
	public Boolean component9(){
		return getLeaveMessagesEnabled();
	}

	/**
	 * Getter for <code>public.guilds.leave_messages_enabled</code>.
	 */
	public Boolean getLeaveMessagesEnabled(){
		return (Boolean) get(8);
	}

	/**
	 * Setter for <code>public.guilds.leave_messages_enabled</code>.
	 */
	public GuildsRecord setLeaveMessagesEnabled(Boolean value){
		set(8, value);
		return this;
	}

	@Override
	public String component10(){
		return getBoostMessages();
	}

	/**
	 * Getter for <code>public.guilds.boost_messages</code>.
	 */
	public String getBoostMessages(){
		return (String) get(9);
	}

	/**
	 * Setter for <code>public.guilds.boost_messages</code>.
	 */
	public GuildsRecord setBoostMessages(String value){
		set(9, value);
		return this;
	}

	@Override
	public Boolean component11(){
		return getBoostMessagesEnabled();
	}

	/**
	 * Getter for <code>public.guilds.boost_messages_enabled</code>.
	 */
	public Boolean getBoostMessagesEnabled(){
		return (Boolean) get(10);
	}

	/**
	 * Setter for <code>public.guilds.boost_messages_enabled</code>.
	 */
	public GuildsRecord setBoostMessagesEnabled(Boolean value){
		set(10, value);
		return this;
	}

	@Override
	public String component12(){
		return getLogChannelId();
	}

	/**
	 * Getter for <code>public.guilds.log_channel_id</code>.
	 */
	public String getLogChannelId(){
		return (String) get(11);
	}

	/**
	 * Setter for <code>public.guilds.log_channel_id</code>.
	 */
	public GuildsRecord setLogChannelId(String value){
		set(11, value);
		return this;
	}

	@Override
	public Boolean component13(){
		return getLogMessagesEnabled();
	}

	/**
	 * Getter for <code>public.guilds.log_messages_enabled</code>.
	 */
	public Boolean getLogMessagesEnabled(){
		return (Boolean) get(12);
	}

	/**
	 * Setter for <code>public.guilds.log_messages_enabled</code>.
	 */
	public GuildsRecord setLogMessagesEnabled(Boolean value){
		set(12, value);
		return this;
	}

	@Override
	public Boolean component14(){
		return getNsfwEnabled();
	}

	/**
	 * Getter for <code>public.guilds.nsfw_enabled</code>.
	 */
	public Boolean getNsfwEnabled(){
		return (Boolean) get(13);
	}

	/**
	 * Setter for <code>public.guilds.nsfw_enabled</code>.
	 */
	public GuildsRecord setNsfwEnabled(Boolean value){
		set(13, value);
		return this;
	}

	@Override
	public String component15(){
		return getInactiveRoleId();
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Getter for <code>public.guilds.inactive_role_id</code>.
	 */
	public String getInactiveRoleId(){
		return (String) get(14);
	}

	/**
	 * Setter for <code>public.guilds.inactive_role_id</code>.
	 */
	public GuildsRecord setInactiveRoleId(String value){
		set(14, value);
		return this;
	}

}
