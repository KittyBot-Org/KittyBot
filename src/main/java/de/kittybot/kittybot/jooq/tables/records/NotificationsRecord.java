/*
 * This file is generated by jOOQ.
 */
package de.kittybot.kittybot.jooq.tables.records;


import de.kittybot.kittybot.jooq.tables.Notifications;

import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record8;
import org.jooq.Row8;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class NotificationsRecord extends UpdatableRecordImpl<NotificationsRecord> implements Record8<Long, Long, Long, Long, Long, String, LocalDateTime, LocalDateTime> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.notifications.notification_id</code>.
     */
    public NotificationsRecord setNotificationId(Long value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>public.notifications.notification_id</code>.
     */
    public Long getNotificationId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.notifications.guild_id</code>.
     */
    public NotificationsRecord setGuildId(Long value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>public.notifications.guild_id</code>.
     */
    public Long getGuildId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>public.notifications.channel_id</code>.
     */
    public NotificationsRecord setChannelId(Long value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>public.notifications.channel_id</code>.
     */
    public Long getChannelId() {
        return (Long) get(2);
    }

    /**
     * Setter for <code>public.notifications.message_id</code>.
     */
    public NotificationsRecord setMessageId(Long value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>public.notifications.message_id</code>.
     */
    public Long getMessageId() {
        return (Long) get(3);
    }

    /**
     * Setter for <code>public.notifications.user_id</code>.
     */
    public NotificationsRecord setUserId(Long value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>public.notifications.user_id</code>.
     */
    public Long getUserId() {
        return (Long) get(4);
    }

    /**
     * Setter for <code>public.notifications.content</code>.
     */
    public NotificationsRecord setContent(String value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>public.notifications.content</code>.
     */
    public String getContent() {
        return (String) get(5);
    }

    /**
     * Setter for <code>public.notifications.creation_time</code>.
     */
    public NotificationsRecord setCreationTime(LocalDateTime value) {
        set(6, value);
        return this;
    }

    /**
     * Getter for <code>public.notifications.creation_time</code>.
     */
    public LocalDateTime getCreationTime() {
        return (LocalDateTime) get(6);
    }

    /**
     * Setter for <code>public.notifications.notification_time</code>.
     */
    public NotificationsRecord setNotificationTime(LocalDateTime value) {
        set(7, value);
        return this;
    }

    /**
     * Getter for <code>public.notifications.notification_time</code>.
     */
    public LocalDateTime getNotificationTime() {
        return (LocalDateTime) get(7);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record8 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row8<Long, Long, Long, Long, Long, String, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row8) super.fieldsRow();
    }

    @Override
    public Row8<Long, Long, Long, Long, Long, String, LocalDateTime, LocalDateTime> valuesRow() {
        return (Row8) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return Notifications.NOTIFICATIONS.NOTIFICATION_ID;
    }

    @Override
    public Field<Long> field2() {
        return Notifications.NOTIFICATIONS.GUILD_ID;
    }

    @Override
    public Field<Long> field3() {
        return Notifications.NOTIFICATIONS.CHANNEL_ID;
    }

    @Override
    public Field<Long> field4() {
        return Notifications.NOTIFICATIONS.MESSAGE_ID;
    }

    @Override
    public Field<Long> field5() {
        return Notifications.NOTIFICATIONS.USER_ID;
    }

    @Override
    public Field<String> field6() {
        return Notifications.NOTIFICATIONS.CONTENT;
    }

    @Override
    public Field<LocalDateTime> field7() {
        return Notifications.NOTIFICATIONS.CREATION_TIME;
    }

    @Override
    public Field<LocalDateTime> field8() {
        return Notifications.NOTIFICATIONS.NOTIFICATION_TIME;
    }

    @Override
    public Long component1() {
        return getNotificationId();
    }

    @Override
    public Long component2() {
        return getGuildId();
    }

    @Override
    public Long component3() {
        return getChannelId();
    }

    @Override
    public Long component4() {
        return getMessageId();
    }

    @Override
    public Long component5() {
        return getUserId();
    }

    @Override
    public String component6() {
        return getContent();
    }

    @Override
    public LocalDateTime component7() {
        return getCreationTime();
    }

    @Override
    public LocalDateTime component8() {
        return getNotificationTime();
    }

    @Override
    public Long value1() {
        return getNotificationId();
    }

    @Override
    public Long value2() {
        return getGuildId();
    }

    @Override
    public Long value3() {
        return getChannelId();
    }

    @Override
    public Long value4() {
        return getMessageId();
    }

    @Override
    public Long value5() {
        return getUserId();
    }

    @Override
    public String value6() {
        return getContent();
    }

    @Override
    public LocalDateTime value7() {
        return getCreationTime();
    }

    @Override
    public LocalDateTime value8() {
        return getNotificationTime();
    }

    @Override
    public NotificationsRecord value1(Long value) {
        setNotificationId(value);
        return this;
    }

    @Override
    public NotificationsRecord value2(Long value) {
        setGuildId(value);
        return this;
    }

    @Override
    public NotificationsRecord value3(Long value) {
        setChannelId(value);
        return this;
    }

    @Override
    public NotificationsRecord value4(Long value) {
        setMessageId(value);
        return this;
    }

    @Override
    public NotificationsRecord value5(Long value) {
        setUserId(value);
        return this;
    }

    @Override
    public NotificationsRecord value6(String value) {
        setContent(value);
        return this;
    }

    @Override
    public NotificationsRecord value7(LocalDateTime value) {
        setCreationTime(value);
        return this;
    }

    @Override
    public NotificationsRecord value8(LocalDateTime value) {
        setNotificationTime(value);
        return this;
    }

    @Override
    public NotificationsRecord values(Long value1, Long value2, Long value3, Long value4, Long value5, String value6, LocalDateTime value7, LocalDateTime value8) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached NotificationsRecord
     */
    public NotificationsRecord() {
        super(Notifications.NOTIFICATIONS);
    }

    /**
     * Create a detached, initialised NotificationsRecord
     */
    public NotificationsRecord(Long notificationId, Long guildId, Long channelId, Long messageId, Long userId, String content, LocalDateTime creationTime, LocalDateTime notificationTime) {
        super(Notifications.NOTIFICATIONS);

        setNotificationId(notificationId);
        setGuildId(guildId);
        setChannelId(channelId);
        setMessageId(messageId);
        setUserId(userId);
        setContent(content);
        setCreationTime(creationTime);
        setNotificationTime(notificationTime);
    }
}