/*
 * This file is generated by jOOQ.
 */
package de.kittybot.kittybot.jooq.tables.records;


import de.kittybot.kittybot.jooq.tables.SelfAssignableRoleMessages;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class SelfAssignableRoleMessagesRecord extends UpdatableRecordImpl<SelfAssignableRoleMessagesRecord> implements Record3<Long, Long, Long> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.self_assignable_role_messages.id</code>.
     */
    public SelfAssignableRoleMessagesRecord setId(Long value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>public.self_assignable_role_messages.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.self_assignable_role_messages.guild_id</code>.
     */
    public SelfAssignableRoleMessagesRecord setGuildId(Long value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>public.self_assignable_role_messages.guild_id</code>.
     */
    public Long getGuildId() {
        return (Long) get(1);
    }

    /**
     * Setter for <code>public.self_assignable_role_messages.message_id</code>.
     */
    public SelfAssignableRoleMessagesRecord setMessageId(Long value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>public.self_assignable_role_messages.message_id</code>.
     */
    public Long getMessageId() {
        return (Long) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row3<Long, Long, Long> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    @Override
    public Row3<Long, Long, Long> valuesRow() {
        return (Row3) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return SelfAssignableRoleMessages.SELF_ASSIGNABLE_ROLE_MESSAGES.ID;
    }

    @Override
    public Field<Long> field2() {
        return SelfAssignableRoleMessages.SELF_ASSIGNABLE_ROLE_MESSAGES.GUILD_ID;
    }

    @Override
    public Field<Long> field3() {
        return SelfAssignableRoleMessages.SELF_ASSIGNABLE_ROLE_MESSAGES.MESSAGE_ID;
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
        return getMessageId();
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
        return getMessageId();
    }

    @Override
    public SelfAssignableRoleMessagesRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public SelfAssignableRoleMessagesRecord value2(Long value) {
        setGuildId(value);
        return this;
    }

    @Override
    public SelfAssignableRoleMessagesRecord value3(Long value) {
        setMessageId(value);
        return this;
    }

    @Override
    public SelfAssignableRoleMessagesRecord values(Long value1, Long value2, Long value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached SelfAssignableRoleMessagesRecord
     */
    public SelfAssignableRoleMessagesRecord() {
        super(SelfAssignableRoleMessages.SELF_ASSIGNABLE_ROLE_MESSAGES);
    }

    /**
     * Create a detached, initialised SelfAssignableRoleMessagesRecord
     */
    public SelfAssignableRoleMessagesRecord(Long id, Long guildId, Long messageId) {
        super(SelfAssignableRoleMessages.SELF_ASSIGNABLE_ROLE_MESSAGES);

        setId(id);
        setGuildId(guildId);
        setMessageId(messageId);
    }
}