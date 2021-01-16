/*
 * This file is generated by jOOQ.
 */
package de.kittybot.kittybot.jooq.tables.records;


import de.kittybot.kittybot.jooq.tables.StreamUsers;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class StreamUsersRecord extends UpdatableRecordImpl<StreamUsersRecord> implements Record4<Long, Long, String, Integer> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.stream_users.id</code>.
     */
    public StreamUsersRecord setId(Long value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>public.stream_users.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.stream_users.guild_id</code>.
     */
    public StreamUsersRecord setGuildId(Long value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>public.stream_users.guild_id</code>.
     */
    public Long getGuildId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>public.stream_users.user_name</code>.
     */
    public StreamUsersRecord setUserName(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>public.stream_users.user_name</code>.
     */
    public String getUserName() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.stream_users.stream_type</code>.
     */
    public StreamUsersRecord setStreamType(Integer value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>public.stream_users.stream_type</code>.
     */
    public Integer getStreamType() {
        return (Integer) get(3);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record4 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row4<Long, Long, String, Integer> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    @Override
    public Row4<Long, Long, String, Integer> valuesRow() {
        return (Row4) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return StreamUsers.STREAM_USERS.ID;
    }

    @Override
    public Field<Long> field2() {
        return StreamUsers.STREAM_USERS.GUILD_ID;
    }

    @Override
    public Field<String> field3() {
        return StreamUsers.STREAM_USERS.USER_NAME;
    }

    @Override
    public Field<Integer> field4() {
        return StreamUsers.STREAM_USERS.STREAM_TYPE;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public Long component2() {
        return getGuildId();
    }

    @Override
    public String component3() {
        return getUserName();
    }

    @Override
    public Integer component4() {
        return getStreamType();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public Long value2() {
        return getGuildId();
    }

    @Override
    public String value3() {
        return getUserName();
    }

    @Override
    public Integer value4() {
        return getStreamType();
    }

    @Override
    public StreamUsersRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public StreamUsersRecord value2(Long value) {
        setGuildId(value);
        return this;
    }

    @Override
    public StreamUsersRecord value3(String value) {
        setUserName(value);
        return this;
    }

    @Override
    public StreamUsersRecord value4(Integer value) {
        setStreamType(value);
        return this;
    }

    @Override
    public StreamUsersRecord values(Long value1, Long value2, String value3, Integer value4) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached StreamUsersRecord
     */
    public StreamUsersRecord() {
        super(StreamUsers.STREAM_USERS);
    }

    /**
     * Create a detached, initialised StreamUsersRecord
     */
    public StreamUsersRecord(Long id, Long guildId, String userName, Integer streamType) {
        super(StreamUsers.STREAM_USERS);

        setId(id);
        setGuildId(guildId);
        setUserName(userName);
        setStreamType(streamType);
    }
}