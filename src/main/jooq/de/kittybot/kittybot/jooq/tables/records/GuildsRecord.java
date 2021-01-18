/*
 * This file is generated by jOOQ.
 */
package de.kittybot.kittybot.jooq.tables.records;


import de.kittybot.kittybot.jooq.tables.Guilds;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record18;
import org.jooq.Row18;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.YearToSecond;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class GuildsRecord extends UpdatableRecordImpl<GuildsRecord> implements Record18<Long, Long, String, Boolean, String, Boolean, Long, Boolean, Long, Boolean, Long, String, Boolean, Long, YearToSecond, Boolean, Long, Boolean> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.guilds.id</code>.
     */
    public GuildsRecord setId(Long value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>public.guilds.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.guilds.announcement_channel_id</code>.
     */
    public GuildsRecord setAnnouncementChannelId(Long value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>public.guilds.announcement_channel_id</code>.
     */
    public Long getAnnouncementChannelId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>public.guilds.join_message</code>.
     */
    public GuildsRecord setJoinMessage(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>public.guilds.join_message</code>.
     */
    public String getJoinMessage() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.guilds.join_messages_enabled</code>.
     */
    public GuildsRecord setJoinMessagesEnabled(Boolean value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>public.guilds.join_messages_enabled</code>.
     */
    public Boolean getJoinMessagesEnabled() {
        return (Boolean) get(3);
    }

    /**
     * Setter for <code>public.guilds.leave_message</code>.
     */
    public GuildsRecord setLeaveMessage(String value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>public.guilds.leave_message</code>.
     */
    public String getLeaveMessage() {
        return (String) get(4);
    }

    /**
     * Setter for <code>public.guilds.leave_messages_enabled</code>.
     */
    public GuildsRecord setLeaveMessagesEnabled(Boolean value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>public.guilds.leave_messages_enabled</code>.
     */
    public Boolean getLeaveMessagesEnabled() {
        return (Boolean) get(5);
    }

    /**
     * Setter for <code>public.guilds.log_channel_id</code>.
     */
    public GuildsRecord setLogChannelId(Long value) {
        set(6, value);
        return this;
    }

    /**
     * Getter for <code>public.guilds.log_channel_id</code>.
     */
    public Long getLogChannelId() {
        return (Long) get(6);
    }

    /**
     * Setter for <code>public.guilds.log_messages_enabled</code>.
     */
    public GuildsRecord setLogMessagesEnabled(Boolean value) {
        set(7, value);
        return this;
    }

    /**
     * Getter for <code>public.guilds.log_messages_enabled</code>.
     */
    public Boolean getLogMessagesEnabled() {
        return (Boolean) get(7);
    }

    /**
     * Setter for <code>public.guilds.request_channel_id</code>.
     */
    public GuildsRecord setRequestChannelId(Long value) {
        set(8, value);
        return this;
    }

    /**
     * Getter for <code>public.guilds.request_channel_id</code>.
     */
    public Long getRequestChannelId() {
        return (Long) get(8);
    }

    /**
     * Setter for <code>public.guilds.requests_enabled</code>.
     */
    public GuildsRecord setRequestsEnabled(Boolean value) {
        set(9, value);
        return this;
    }

    /**
     * Getter for <code>public.guilds.requests_enabled</code>.
     */
    public Boolean getRequestsEnabled() {
        return (Boolean) get(9);
    }

    /**
     * Setter for <code>public.guilds.stream_announcement_channel_id</code>.
     */
    public GuildsRecord setStreamAnnouncementChannelId(Long value) {
        set(10, value);
        return this;
    }

    /**
     * Getter for <code>public.guilds.stream_announcement_channel_id</code>.
     */
    public Long getStreamAnnouncementChannelId() {
        return (Long) get(10);
    }

    /**
     * Setter for <code>public.guilds.stream_announcement_message</code>.
     */
    public GuildsRecord setStreamAnnouncementMessage(String value) {
        set(11, value);
        return this;
    }

    /**
     * Getter for <code>public.guilds.stream_announcement_message</code>.
     */
    public String getStreamAnnouncementMessage() {
        return (String) get(11);
    }

    /**
     * Setter for <code>public.guilds.nsfw_enabled</code>.
     */
    public GuildsRecord setNsfwEnabled(Boolean value) {
        set(12, value);
        return this;
    }

    /**
     * Getter for <code>public.guilds.nsfw_enabled</code>.
     */
    public Boolean getNsfwEnabled() {
        return (Boolean) get(12);
    }

    /**
     * Setter for <code>public.guilds.inactive_role_id</code>.
     */
    public GuildsRecord setInactiveRoleId(Long value) {
        set(13, value);
        return this;
    }

    /**
     * Getter for <code>public.guilds.inactive_role_id</code>.
     */
    public Long getInactiveRoleId() {
        return (Long) get(13);
    }

    /**
     * Setter for <code>public.guilds.inactive_duration</code>.
     */
    public GuildsRecord setInactiveDuration(YearToSecond value) {
        set(14, value);
        return this;
    }

    /**
     * Getter for <code>public.guilds.inactive_duration</code>.
     */
    public YearToSecond getInactiveDuration() {
        return (YearToSecond) get(14);
    }

    /**
     * Setter for <code>public.guilds.inactive_role_enabled</code>.
     */
    public GuildsRecord setInactiveRoleEnabled(Boolean value) {
        set(15, value);
        return this;
    }

    /**
     * Getter for <code>public.guilds.inactive_role_enabled</code>.
     */
    public Boolean getInactiveRoleEnabled() {
        return (Boolean) get(15);
    }

    /**
     * Setter for <code>public.guilds.dj_role_id</code>.
     */
    public GuildsRecord setDjRoleId(Long value) {
        set(16, value);
        return this;
    }

    /**
     * Getter for <code>public.guilds.dj_role_id</code>.
     */
    public Long getDjRoleId() {
        return (Long) get(16);
    }

    /**
     * Setter for <code>public.guilds.snipes_enabled</code>.
     */
    public GuildsRecord setSnipesEnabled(Boolean value) {
        set(17, value);
        return this;
    }

    /**
     * Getter for <code>public.guilds.snipes_enabled</code>.
     */
    public Boolean getSnipesEnabled() {
        return (Boolean) get(17);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record18 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row18<Long, Long, String, Boolean, String, Boolean, Long, Boolean, Long, Boolean, Long, String, Boolean, Long, YearToSecond, Boolean, Long, Boolean> fieldsRow() {
        return (Row18) super.fieldsRow();
    }

    @Override
    public Row18<Long, Long, String, Boolean, String, Boolean, Long, Boolean, Long, Boolean, Long, String, Boolean, Long, YearToSecond, Boolean, Long, Boolean> valuesRow() {
        return (Row18) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return Guilds.GUILDS.ID;
    }

    @Override
    public Field<Long> field2() {
        return Guilds.GUILDS.ANNOUNCEMENT_CHANNEL_ID;
    }

    @Override
    public Field<String> field3() {
        return Guilds.GUILDS.JOIN_MESSAGE;
    }

    @Override
    public Field<Boolean> field4() {
        return Guilds.GUILDS.JOIN_MESSAGES_ENABLED;
    }

    @Override
    public Field<String> field5() {
        return Guilds.GUILDS.LEAVE_MESSAGE;
    }

    @Override
    public Field<Boolean> field6() {
        return Guilds.GUILDS.LEAVE_MESSAGES_ENABLED;
    }

    @Override
    public Field<Long> field7() {
        return Guilds.GUILDS.LOG_CHANNEL_ID;
    }

    @Override
    public Field<Boolean> field8() {
        return Guilds.GUILDS.LOG_MESSAGES_ENABLED;
    }

    @Override
    public Field<Long> field9() {
        return Guilds.GUILDS.REQUEST_CHANNEL_ID;
    }

    @Override
    public Field<Boolean> field10() {
        return Guilds.GUILDS.REQUESTS_ENABLED;
    }

    @Override
    public Field<Long> field11() {
        return Guilds.GUILDS.STREAM_ANNOUNCEMENT_CHANNEL_ID;
    }

    @Override
    public Field<String> field12() {
        return Guilds.GUILDS.STREAM_ANNOUNCEMENT_MESSAGE;
    }

    @Override
    public Field<Boolean> field13() {
        return Guilds.GUILDS.NSFW_ENABLED;
    }

    @Override
    public Field<Long> field14() {
        return Guilds.GUILDS.INACTIVE_ROLE_ID;
    }

    @Override
    public Field<YearToSecond> field15() {
        return Guilds.GUILDS.INACTIVE_DURATION;
    }

    @Override
    public Field<Boolean> field16() {
        return Guilds.GUILDS.INACTIVE_ROLE_ENABLED;
    }

    @Override
    public Field<Long> field17() {
        return Guilds.GUILDS.DJ_ROLE_ID;
    }

    @Override
    public Field<Boolean> field18() {
        return Guilds.GUILDS.SNIPES_ENABLED;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public Long component2() {
        return getAnnouncementChannelId();
    }

    @Override
    public String component3() {
        return getJoinMessage();
    }

    @Override
    public Boolean component4() {
        return getJoinMessagesEnabled();
    }

    @Override
    public String component5() {
        return getLeaveMessage();
    }

    @Override
    public Boolean component6() {
        return getLeaveMessagesEnabled();
    }

    @Override
    public Long component7() {
        return getLogChannelId();
    }

    @Override
    public Boolean component8() {
        return getLogMessagesEnabled();
    }

    @Override
    public Long component9() {
        return getRequestChannelId();
    }

    @Override
    public Boolean component10() {
        return getRequestsEnabled();
    }

    @Override
    public Long component11() {
        return getStreamAnnouncementChannelId();
    }

    @Override
    public String component12() {
        return getStreamAnnouncementMessage();
    }

    @Override
    public Boolean component13() {
        return getNsfwEnabled();
    }

    @Override
    public Long component14() {
        return getInactiveRoleId();
    }

    @Override
    public YearToSecond component15() {
        return getInactiveDuration();
    }

    @Override
    public Boolean component16() {
        return getInactiveRoleEnabled();
    }

    @Override
    public Long component17() {
        return getDjRoleId();
    }

    @Override
    public Boolean component18() {
        return getSnipesEnabled();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public Long value2() {
        return getAnnouncementChannelId();
    }

    @Override
    public String value3() {
        return getJoinMessage();
    }

    @Override
    public Boolean value4() {
        return getJoinMessagesEnabled();
    }

    @Override
    public String value5() {
        return getLeaveMessage();
    }

    @Override
    public Boolean value6() {
        return getLeaveMessagesEnabled();
    }

    @Override
    public Long value7() {
        return getLogChannelId();
    }

    @Override
    public Boolean value8() {
        return getLogMessagesEnabled();
    }

    @Override
    public Long value9() {
        return getRequestChannelId();
    }

    @Override
    public Boolean value10() {
        return getRequestsEnabled();
    }

    @Override
    public Long value11() {
        return getStreamAnnouncementChannelId();
    }

    @Override
    public String value12() {
        return getStreamAnnouncementMessage();
    }

    @Override
    public Boolean value13() {
        return getNsfwEnabled();
    }

    @Override
    public Long value14() {
        return getInactiveRoleId();
    }

    @Override
    public YearToSecond value15() {
        return getInactiveDuration();
    }

    @Override
    public Boolean value16() {
        return getInactiveRoleEnabled();
    }

    @Override
    public Long value17() {
        return getDjRoleId();
    }

    @Override
    public Boolean value18() {
        return getSnipesEnabled();
    }

    @Override
    public GuildsRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public GuildsRecord value2(Long value) {
        setAnnouncementChannelId(value);
        return this;
    }

    @Override
    public GuildsRecord value3(String value) {
        setJoinMessage(value);
        return this;
    }

    @Override
    public GuildsRecord value4(Boolean value) {
        setJoinMessagesEnabled(value);
        return this;
    }

    @Override
    public GuildsRecord value5(String value) {
        setLeaveMessage(value);
        return this;
    }

    @Override
    public GuildsRecord value6(Boolean value) {
        setLeaveMessagesEnabled(value);
        return this;
    }

    @Override
    public GuildsRecord value7(Long value) {
        setLogChannelId(value);
        return this;
    }

    @Override
    public GuildsRecord value8(Boolean value) {
        setLogMessagesEnabled(value);
        return this;
    }

    @Override
    public GuildsRecord value9(Long value) {
        setRequestChannelId(value);
        return this;
    }

    @Override
    public GuildsRecord value10(Boolean value) {
        setRequestsEnabled(value);
        return this;
    }

    @Override
    public GuildsRecord value11(Long value) {
        setStreamAnnouncementChannelId(value);
        return this;
    }

    @Override
    public GuildsRecord value12(String value) {
        setStreamAnnouncementMessage(value);
        return this;
    }

    @Override
    public GuildsRecord value13(Boolean value) {
        setNsfwEnabled(value);
        return this;
    }

    @Override
    public GuildsRecord value14(Long value) {
        setInactiveRoleId(value);
        return this;
    }

    @Override
    public GuildsRecord value15(YearToSecond value) {
        setInactiveDuration(value);
        return this;
    }

    @Override
    public GuildsRecord value16(Boolean value) {
        setInactiveRoleEnabled(value);
        return this;
    }

    @Override
    public GuildsRecord value17(Long value) {
        setDjRoleId(value);
        return this;
    }

    @Override
    public GuildsRecord value18(Boolean value) {
        setSnipesEnabled(value);
        return this;
    }

    @Override
    public GuildsRecord values(Long value1, Long value2, String value3, Boolean value4, String value5, Boolean value6, Long value7, Boolean value8, Long value9, Boolean value10, Long value11, String value12, Boolean value13, Long value14, YearToSecond value15, Boolean value16, Long value17, Boolean value18) {
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
        value16(value16);
        value17(value17);
        value18(value18);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached GuildsRecord
     */
    public GuildsRecord() {
        super(Guilds.GUILDS);
    }

    /**
     * Create a detached, initialised GuildsRecord
     */
    public GuildsRecord(Long id, Long announcementChannelId, String joinMessage, Boolean joinMessagesEnabled, String leaveMessage, Boolean leaveMessagesEnabled, Long logChannelId, Boolean logMessagesEnabled, Long requestChannelId, Boolean requestsEnabled, Long streamAnnouncementChannelId, String streamAnnouncementMessage, Boolean nsfwEnabled, Long inactiveRoleId, YearToSecond inactiveDuration, Boolean inactiveRoleEnabled, Long djRoleId, Boolean snipesEnabled) {
        super(Guilds.GUILDS);

        setId(id);
        setAnnouncementChannelId(announcementChannelId);
        setJoinMessage(joinMessage);
        setJoinMessagesEnabled(joinMessagesEnabled);
        setLeaveMessage(leaveMessage);
        setLeaveMessagesEnabled(leaveMessagesEnabled);
        setLogChannelId(logChannelId);
        setLogMessagesEnabled(logMessagesEnabled);
        setRequestChannelId(requestChannelId);
        setRequestsEnabled(requestsEnabled);
        setStreamAnnouncementChannelId(streamAnnouncementChannelId);
        setStreamAnnouncementMessage(streamAnnouncementMessage);
        setNsfwEnabled(nsfwEnabled);
        setInactiveRoleId(inactiveRoleId);
        setInactiveDuration(inactiveDuration);
        setInactiveRoleEnabled(inactiveRoleEnabled);
        setDjRoleId(djRoleId);
        setSnipesEnabled(snipesEnabled);
    }
}
