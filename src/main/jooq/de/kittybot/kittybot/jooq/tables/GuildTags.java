/*
 * This file is generated by jOOQ.
 */
package de.kittybot.kittybot.jooq.tables;


import de.kittybot.kittybot.jooq.Keys;
import de.kittybot.kittybot.jooq.Public;
import de.kittybot.kittybot.jooq.tables.records.GuildTagsRecord;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row8;
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
public class GuildTags extends TableImpl<GuildTagsRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.guild_tags</code>
     */
    public static final GuildTags GUILD_TAGS = new GuildTags();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<GuildTagsRecord> getRecordType() {
        return GuildTagsRecord.class;
    }

    /**
     * The column <code>public.guild_tags.id</code>.
     */
    public final TableField<GuildTagsRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.guild_tags.name</code>.
     */
    public final TableField<GuildTagsRecord, String> NAME = createField(DSL.name("name"), SQLDataType.VARCHAR(64).nullable(false), this, "");

    /**
     * The column <code>public.guild_tags.guild_id</code>.
     */
    public final TableField<GuildTagsRecord, Long> GUILD_ID = createField(DSL.name("guild_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.guild_tags.user_id</code>.
     */
    public final TableField<GuildTagsRecord, Long> USER_ID = createField(DSL.name("user_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.guild_tags.content</code>.
     */
    public final TableField<GuildTagsRecord, String> CONTENT = createField(DSL.name("content"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>public.guild_tags.created_at</code>.
     */
    public final TableField<GuildTagsRecord, LocalDateTime> CREATED_AT = createField(DSL.name("created_at"), SQLDataType.LOCALDATETIME(6).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP", SQLDataType.LOCALDATETIME)), this, "");

    /**
     * The column <code>public.guild_tags.updated_at</code>.
     */
    public final TableField<GuildTagsRecord, LocalDateTime> UPDATED_AT = createField(DSL.name("updated_at"), SQLDataType.LOCALDATETIME(6), this, "");

    /**
     * The column <code>public.guild_tags.command_id</code>.
     */
    public final TableField<GuildTagsRecord, Long> COMMAND_ID = createField(DSL.name("command_id"), SQLDataType.BIGINT.nullable(false).defaultValue(DSL.field("'-1'::integer", SQLDataType.BIGINT)), this, "");

    private GuildTags(Name alias, Table<GuildTagsRecord> aliased) {
        this(alias, aliased, null);
    }

    private GuildTags(Name alias, Table<GuildTagsRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.guild_tags</code> table reference
     */
    public GuildTags(String alias) {
        this(DSL.name(alias), GUILD_TAGS);
    }

    /**
     * Create an aliased <code>public.guild_tags</code> table reference
     */
    public GuildTags(Name alias) {
        this(alias, GUILD_TAGS);
    }

    /**
     * Create a <code>public.guild_tags</code> table reference
     */
    public GuildTags() {
        this(DSL.name("guild_tags"), null);
    }

    public <O extends Record> GuildTags(Table<O> child, ForeignKey<O, GuildTagsRecord> key) {
        super(child, key, GUILD_TAGS);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<GuildTagsRecord, Long> getIdentity() {
        return (Identity<GuildTagsRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<GuildTagsRecord> getPrimaryKey() {
        return Keys.GUILD_TAGS_PKEY;
    }

    @Override
    public List<UniqueKey<GuildTagsRecord>> getKeys() {
        return Arrays.<UniqueKey<GuildTagsRecord>>asList(Keys.GUILD_TAGS_PKEY, Keys.GUILD_TAGS_NAME_GUILD_ID_KEY);
    }

    @Override
    public List<ForeignKey<GuildTagsRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<GuildTagsRecord, ?>>asList(Keys.GUILD_TAGS__GUILD_TAGS_GUILD_ID_FKEY);
    }

    public Guilds guilds() {
        return new Guilds(this, Keys.GUILD_TAGS__GUILD_TAGS_GUILD_ID_FKEY);
    }

    @Override
    public GuildTags as(String alias) {
        return new GuildTags(DSL.name(alias), this);
    }

    @Override
    public GuildTags as(Name alias) {
        return new GuildTags(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public GuildTags rename(String name) {
        return new GuildTags(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public GuildTags rename(Name name) {
        return new GuildTags(name, null);
    }

    // -------------------------------------------------------------------------
    // Row8 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row8<Long, String, Long, Long, String, LocalDateTime, LocalDateTime, Long> fieldsRow() {
        return (Row8) super.fieldsRow();
    }
}
