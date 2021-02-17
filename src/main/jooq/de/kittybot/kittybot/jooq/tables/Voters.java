/*
 * This file is generated by jOOQ.
 */
package de.kittybot.kittybot.jooq.tables;


import de.kittybot.kittybot.jooq.Keys;
import de.kittybot.kittybot.jooq.Public;
import de.kittybot.kittybot.jooq.tables.records.VotersRecord;

import java.time.LocalDateTime;
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
public class Voters extends TableImpl<VotersRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.voters</code>
     */
    public static final Voters VOTERS = new Voters();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<VotersRecord> getRecordType() {
        return VotersRecord.class;
    }

    /**
     * The column <code>public.voters.id</code>.
     */
    public final TableField<VotersRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.voters.user_id</code>.
     */
    public final TableField<VotersRecord, Long> USER_ID = createField(DSL.name("user_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.voters.vote_expiry</code>.
     */
    public final TableField<VotersRecord, LocalDateTime> VOTE_EXPIRY = createField(DSL.name("vote_expiry"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "");

    private Voters(Name alias, Table<VotersRecord> aliased) {
        this(alias, aliased, null);
    }

    private Voters(Name alias, Table<VotersRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.voters</code> table reference
     */
    public Voters(String alias) {
        this(DSL.name(alias), VOTERS);
    }

    /**
     * Create an aliased <code>public.voters</code> table reference
     */
    public Voters(Name alias) {
        this(alias, VOTERS);
    }

    /**
     * Create a <code>public.voters</code> table reference
     */
    public Voters() {
        this(DSL.name("voters"), null);
    }

    public <O extends Record> Voters(Table<O> child, ForeignKey<O, VotersRecord> key) {
        super(child, key, VOTERS);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<VotersRecord, Long> getIdentity() {
        return (Identity<VotersRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<VotersRecord> getPrimaryKey() {
        return Keys.VOTERS_PKEY;
    }

    @Override
    public List<UniqueKey<VotersRecord>> getKeys() {
        return Arrays.<UniqueKey<VotersRecord>>asList(Keys.VOTERS_PKEY, Keys.VOTERS_USER_ID_KEY);
    }

    @Override
    public Voters as(String alias) {
        return new Voters(DSL.name(alias), this);
    }

    @Override
    public Voters as(Name alias) {
        return new Voters(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Voters rename(String name) {
        return new Voters(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Voters rename(Name name) {
        return new Voters(name, null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<Long, Long, LocalDateTime> fieldsRow() {
        return (Row3) super.fieldsRow();
    }
}