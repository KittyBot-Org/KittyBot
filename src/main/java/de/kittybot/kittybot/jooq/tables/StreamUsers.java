/*
 * This file is generated by jOOQ.
 */
package de.kittybot.kittybot.jooq.tables;


import de.kittybot.kittybot.jooq.Keys;
import de.kittybot.kittybot.jooq.Public;
import de.kittybot.kittybot.jooq.tables.records.StreamUsersRecord;

import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row4;
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
public class StreamUsers extends TableImpl<StreamUsersRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.stream_users</code>
     */
    public static final StreamUsers STREAM_USERS = new StreamUsers();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<StreamUsersRecord> getRecordType() {
        return StreamUsersRecord.class;
    }

    /**
     * The column <code>public.stream_users.stream_user_id</code>.
     */
    public final TableField<StreamUsersRecord, Long> STREAM_USER_ID = createField(DSL.name("stream_user_id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.stream_users.guild_id</code>.
     */
    public final TableField<StreamUsersRecord, Long> GUILD_ID = createField(DSL.name("guild_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.stream_users.user_name</code>.
     */
    public final TableField<StreamUsersRecord, String> USER_NAME = createField(DSL.name("user_name"), SQLDataType.VARCHAR(32).nullable(false), this, "");

    /**
     * The column <code>public.stream_users.stream_type</code>.
     */
    public final TableField<StreamUsersRecord, Integer> STREAM_TYPE = createField(DSL.name("stream_type"), SQLDataType.INTEGER.nullable(false), this, "");

    private StreamUsers(Name alias, Table<StreamUsersRecord> aliased) {
        this(alias, aliased, null);
    }

    private StreamUsers(Name alias, Table<StreamUsersRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.stream_users</code> table reference
     */
    public StreamUsers(String alias) {
        this(DSL.name(alias), STREAM_USERS);
    }

    /**
     * Create an aliased <code>public.stream_users</code> table reference
     */
    public StreamUsers(Name alias) {
        this(alias, STREAM_USERS);
    }

    /**
     * Create a <code>public.stream_users</code> table reference
     */
    public StreamUsers() {
        this(DSL.name("stream_users"), null);
    }

    public <O extends Record> StreamUsers(Table<O> child, ForeignKey<O, StreamUsersRecord> key) {
        super(child, key, STREAM_USERS);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<StreamUsersRecord, Long> getIdentity() {
        return (Identity<StreamUsersRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<StreamUsersRecord> getPrimaryKey() {
        return Keys.STREAM_USERS_PKEY;
    }

    @Override
    public List<UniqueKey<StreamUsersRecord>> getKeys() {
        return Arrays.<UniqueKey<StreamUsersRecord>>asList(Keys.STREAM_USERS_PKEY);
    }

    @Override
    public List<ForeignKey<StreamUsersRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<StreamUsersRecord, ?>>asList(Keys.STREAM_USERS__STREAM_USERS_GUILD_ID_FKEY);
    }

    public Guilds guilds() {
        return new Guilds(this, Keys.STREAM_USERS__STREAM_USERS_GUILD_ID_FKEY);
    }

    @Override
    public StreamUsers as(String alias) {
        return new StreamUsers(DSL.name(alias), this);
    }

    @Override
    public StreamUsers as(Name alias) {
        return new StreamUsers(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public StreamUsers rename(String name) {
        return new StreamUsers(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public StreamUsers rename(Name name) {
        return new StreamUsers(name, null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<Long, Long, String, Integer> fieldsRow() {
        return (Row4) super.fieldsRow();
    }
}