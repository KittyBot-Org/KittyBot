/*
 * This file is generated by jOOQ.
 */
package de.kittybot.kittybot.jooq.tables;


import de.kittybot.kittybot.jooq.Keys;
import de.kittybot.kittybot.jooq.Public;
import de.kittybot.kittybot.jooq.tables.records.SelfAssignableRoleMessagesRecord;

import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row3;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class SelfAssignableRoleMessages extends TableImpl<SelfAssignableRoleMessagesRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.self_assignable_role_messages</code>
     */
    public static final SelfAssignableRoleMessages SELF_ASSIGNABLE_ROLE_MESSAGES = new SelfAssignableRoleMessages();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<SelfAssignableRoleMessagesRecord> getRecordType() {
        return SelfAssignableRoleMessagesRecord.class;
    }

    /**
     * The column <code>public.self_assignable_role_messages.id</code>.
     */
    public final TableField<SelfAssignableRoleMessagesRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.self_assignable_role_messages.guild_id</code>.
     */
    public final TableField<SelfAssignableRoleMessagesRecord, Long> GUILD_ID = createField(DSL.name("guild_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.self_assignable_role_messages.message_id</code>.
     */
    public final TableField<SelfAssignableRoleMessagesRecord, Long> MESSAGE_ID = createField(DSL.name("message_id"), SQLDataType.BIGINT.nullable(false), this, "");

    private SelfAssignableRoleMessages(Name alias, Table<SelfAssignableRoleMessagesRecord> aliased) {
        this(alias, aliased, null);
    }

    private SelfAssignableRoleMessages(Name alias, Table<SelfAssignableRoleMessagesRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.self_assignable_role_messages</code> table reference
     */
    public SelfAssignableRoleMessages(String alias) {
        this(DSL.name(alias), SELF_ASSIGNABLE_ROLE_MESSAGES);
    }

    /**
     * Create an aliased <code>public.self_assignable_role_messages</code> table reference
     */
    public SelfAssignableRoleMessages(Name alias) {
        this(alias, SELF_ASSIGNABLE_ROLE_MESSAGES);
    }

    /**
     * Create a <code>public.self_assignable_role_messages</code> table reference
     */
    public SelfAssignableRoleMessages() {
        this(DSL.name("self_assignable_role_messages"), null);
    }

    public <O extends Record> SelfAssignableRoleMessages(Table<O> child, ForeignKey<O, SelfAssignableRoleMessagesRecord> key) {
        super(child, key, SELF_ASSIGNABLE_ROLE_MESSAGES);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<SelfAssignableRoleMessagesRecord, Long> getIdentity() {
        return (Identity<SelfAssignableRoleMessagesRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<SelfAssignableRoleMessagesRecord> getPrimaryKey() {
        return Keys.SELF_ASSIGNABLE_ROLE_MESSAGES_PKEY;
    }

    @Override
    public List<UniqueKey<SelfAssignableRoleMessagesRecord>> getKeys() {
        return Arrays.<UniqueKey<SelfAssignableRoleMessagesRecord>>asList(Keys.SELF_ASSIGNABLE_ROLE_MESSAGES_PKEY, Keys.SELF_ASSIGNABLE_ROLE_MESSAGES_GUILD_ID_MESSAGE_ID_KEY);
    }

    @Override
    public List<ForeignKey<SelfAssignableRoleMessagesRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<SelfAssignableRoleMessagesRecord, ?>>asList(Keys.SELF_ASSIGNABLE_ROLE_MESSAGES__SELF_ASSIGNABLE_ROLE_MESSAGES_GUILD_ID_FKEY);
    }

    public Guilds guilds() {
        return new Guilds(this, Keys.SELF_ASSIGNABLE_ROLE_MESSAGES__SELF_ASSIGNABLE_ROLE_MESSAGES_GUILD_ID_FKEY);
    }

    @Override
    public SelfAssignableRoleMessages as(String alias) {
        return new SelfAssignableRoleMessages(DSL.name(alias), this);
    }

    @Override
    public SelfAssignableRoleMessages as(Name alias) {
        return new SelfAssignableRoleMessages(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public SelfAssignableRoleMessages rename(String name) {
        return new SelfAssignableRoleMessages(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public SelfAssignableRoleMessages rename(Name name) {
        return new SelfAssignableRoleMessages(name, null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<Long, Long, Long> fieldsRow() {
        return (Row3) super.fieldsRow();
    }
}
