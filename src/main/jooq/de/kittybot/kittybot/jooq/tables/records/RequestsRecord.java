/*
 * This file is generated by jOOQ.
 */
package de.kittybot.kittybot.jooq.tables.records;


import de.kittybot.kittybot.jooq.tables.Requests;

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
public class RequestsRecord extends UpdatableRecordImpl<RequestsRecord> implements Record8<Long, Long, Long, String, String, Boolean, Boolean, LocalDateTime> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.requests.id</code>.
     */
    public RequestsRecord setId(Long value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>public.requests.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.requests.guild_id</code>.
     */
    public RequestsRecord setGuildId(Long value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>public.requests.guild_id</code>.
     */
    public Long getGuildId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>public.requests.user_id</code>.
     */
    public RequestsRecord setUserId(Long value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>public.requests.user_id</code>.
     */
    public Long getUserId() {
        return (Long) get(2);
    }

    /**
     * Setter for <code>public.requests.title</code>.
     */
    public RequestsRecord setTitle(String value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>public.requests.title</code>.
     */
    public String getTitle() {
        return (String) get(3);
    }

    /**
     * Setter for <code>public.requests.body</code>.
     */
    public RequestsRecord setBody(String value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>public.requests.body</code>.
     */
    public String getBody() {
        return (String) get(4);
    }

    /**
     * Setter for <code>public.requests.answered</code>.
     */
    public RequestsRecord setAnswered(Boolean value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>public.requests.answered</code>.
     */
    public Boolean getAnswered() {
        return (Boolean) get(5);
    }

    /**
     * Setter for <code>public.requests.accepted</code>.
     */
    public RequestsRecord setAccepted(Boolean value) {
        set(6, value);
        return this;
    }

    /**
     * Getter for <code>public.requests.accepted</code>.
     */
    public Boolean getAccepted() {
        return (Boolean) get(6);
    }

    /**
     * Setter for <code>public.requests.creation_at</code>.
     */
    public RequestsRecord setCreationAt(LocalDateTime value) {
        set(7, value);
        return this;
    }

    /**
     * Getter for <code>public.requests.creation_at</code>.
     */
    public LocalDateTime getCreationAt() {
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
    public Row8<Long, Long, Long, String, String, Boolean, Boolean, LocalDateTime> fieldsRow() {
        return (Row8) super.fieldsRow();
    }

    @Override
    public Row8<Long, Long, Long, String, String, Boolean, Boolean, LocalDateTime> valuesRow() {
        return (Row8) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return Requests.REQUESTS.ID;
    }

    @Override
    public Field<Long> field2() {
        return Requests.REQUESTS.GUILD_ID;
    }

    @Override
    public Field<Long> field3() {
        return Requests.REQUESTS.USER_ID;
    }

    @Override
    public Field<String> field4() {
        return Requests.REQUESTS.TITLE;
    }

    @Override
    public Field<String> field5() {
        return Requests.REQUESTS.BODY;
    }

    @Override
    public Field<Boolean> field6() {
        return Requests.REQUESTS.ANSWERED;
    }

    @Override
    public Field<Boolean> field7() {
        return Requests.REQUESTS.ACCEPTED;
    }

    @Override
    public Field<LocalDateTime> field8() {
        return Requests.REQUESTS.CREATION_AT;
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
    public Long component3() {
        return getUserId();
    }

    @Override
    public String component4() {
        return getTitle();
    }

    @Override
    public String component5() {
        return getBody();
    }

    @Override
    public Boolean component6() {
        return getAnswered();
    }

    @Override
    public Boolean component7() {
        return getAccepted();
    }

    @Override
    public LocalDateTime component8() {
        return getCreationAt();
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
    public Long value3() {
        return getUserId();
    }

    @Override
    public String value4() {
        return getTitle();
    }

    @Override
    public String value5() {
        return getBody();
    }

    @Override
    public Boolean value6() {
        return getAnswered();
    }

    @Override
    public Boolean value7() {
        return getAccepted();
    }

    @Override
    public LocalDateTime value8() {
        return getCreationAt();
    }

    @Override
    public RequestsRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public RequestsRecord value2(Long value) {
        setGuildId(value);
        return this;
    }

    @Override
    public RequestsRecord value3(Long value) {
        setUserId(value);
        return this;
    }

    @Override
    public RequestsRecord value4(String value) {
        setTitle(value);
        return this;
    }

    @Override
    public RequestsRecord value5(String value) {
        setBody(value);
        return this;
    }

    @Override
    public RequestsRecord value6(Boolean value) {
        setAnswered(value);
        return this;
    }

    @Override
    public RequestsRecord value7(Boolean value) {
        setAccepted(value);
        return this;
    }

    @Override
    public RequestsRecord value8(LocalDateTime value) {
        setCreationAt(value);
        return this;
    }

    @Override
    public RequestsRecord values(Long value1, Long value2, Long value3, String value4, String value5, Boolean value6, Boolean value7, LocalDateTime value8) {
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
     * Create a detached RequestsRecord
     */
    public RequestsRecord() {
        super(Requests.REQUESTS);
    }

    /**
     * Create a detached, initialised RequestsRecord
     */
    public RequestsRecord(Long id, Long guildId, Long userId, String title, String body, Boolean answered, Boolean accepted, LocalDateTime creationAt) {
        super(Requests.REQUESTS);

        setId(id);
        setGuildId(guildId);
        setUserId(userId);
        setTitle(title);
        setBody(body);
        setAnswered(answered);
        setAccepted(accepted);
        setCreationAt(creationAt);
    }
}