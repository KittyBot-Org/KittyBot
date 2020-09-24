/*
 * This file is generated by jOOQ.
 */
package de.kittybot.kittybot.database.jooq.tables;


import de.kittybot.kittybot.database.jooq.Keys;
import de.kittybot.kittybot.database.jooq.Public;
import de.kittybot.kittybot.database.jooq.tables.records.CommandsRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row6;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Commands extends TableImpl<CommandsRecord> {

    private static final long serialVersionUID = 1505630354;

    /**
     * The reference instance of <code>public.commands</code>
     */
    public static final Commands COMMANDS = new Commands();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<CommandsRecord> getRecordType() {
        return CommandsRecord.class;
    }

    /**
     * The column <code>public.commands.message_id</code>.
     */
    public final TableField<CommandsRecord, String> MESSAGE_ID = createField(DSL.name("message_id"), org.jooq.impl.SQLDataType.VARCHAR(18).nullable(false), this, "");

    /**
     * The column <code>public.commands.guild_id</code>.
     */
    public final TableField<CommandsRecord, String> GUILD_ID = createField(DSL.name("guild_id"), org.jooq.impl.SQLDataType.VARCHAR(18).nullable(false), this, "");

    /**
     * The column <code>public.commands.user_id</code>.
     */
    public final TableField<CommandsRecord, String> USER_ID = createField(DSL.name("user_id"), org.jooq.impl.SQLDataType.VARCHAR(18).nullable(false), this, "");

    /**
     * The column <code>public.commands.command</code>.
     */
    public final TableField<CommandsRecord, String> COMMAND = createField(DSL.name("command"), org.jooq.impl.SQLDataType.VARCHAR(18).nullable(false), this, "");

    /**
     * The column <code>public.commands.processing_time</code>.
     */
    public final TableField<CommandsRecord, LocalDateTime> PROCESSING_TIME = createField(DSL.name("processing_time"), org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * The column <code>public.commands.time</code>.
     */
    public final TableField<CommandsRecord, LocalDateTime> TIME = createField(DSL.name("time"), org.jooq.impl.SQLDataType.LOCALDATETIME.nullable(false), this, "");

    /**
     * Create a <code>public.commands</code> table reference
     */
    public Commands() {
        this(DSL.name("commands"), null);
    }

    /**
     * Create an aliased <code>public.commands</code> table reference
     */
    public Commands(String alias) {
        this(DSL.name(alias), COMMANDS);
    }

    /**
     * Create an aliased <code>public.commands</code> table reference
     */
    public Commands(Name alias) {
        this(alias, COMMANDS);
    }

    private Commands(Name alias, Table<CommandsRecord> aliased) {
        this(alias, aliased, null);
    }

    private Commands(Name alias, Table<CommandsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    public <O extends Record> Commands(Table<O> child, ForeignKey<O, CommandsRecord> key) {
        super(child, key, COMMANDS);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public UniqueKey<CommandsRecord> getPrimaryKey() {
        return Keys.COMMANDS_PKEY;
    }

    @Override
    public List<UniqueKey<CommandsRecord>> getKeys() {
        return Arrays.<UniqueKey<CommandsRecord>>asList(Keys.COMMANDS_PKEY);
    }

    @Override
    public Commands as(String alias) {
        return new Commands(DSL.name(alias), this);
    }

    @Override
    public Commands as(Name alias) {
        return new Commands(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Commands rename(String name) {
        return new Commands(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Commands rename(Name name) {
        return new Commands(name, null);
    }

    // -------------------------------------------------------------------------
    // Row6 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row6<String, String, String, String, LocalDateTime, LocalDateTime> fieldsRow() {
        return (Row6) super.fieldsRow();
    }
}
