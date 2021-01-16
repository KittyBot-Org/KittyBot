/*
 * This file is generated by jOOQ.
 */
package de.kittybot.kittybot.jooq.tables;


import de.kittybot.kittybot.jooq.Keys;
import de.kittybot.kittybot.jooq.Public;
import de.kittybot.kittybot.jooq.tables.records.GuildInviteRolesRecord;

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
public class GuildInviteRoles extends TableImpl<GuildInviteRolesRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.guild_invite_roles</code>
     */
    public static final GuildInviteRoles GUILD_INVITE_ROLES = new GuildInviteRoles();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<GuildInviteRolesRecord> getRecordType() {
        return GuildInviteRolesRecord.class;
    }

    /**
     * The column <code>public.guild_invite_roles.id</code>.
     */
    public final TableField<GuildInviteRolesRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.guild_invite_roles.guild_invite_id</code>.
     */
    public final TableField<GuildInviteRolesRecord, Long> GUILD_INVITE_ID = createField(DSL.name("guild_invite_id"), SQLDataType.BIGINT.nullable(false), this, "");

    /**
     * The column <code>public.guild_invite_roles.role_id</code>.
     */
    public final TableField<GuildInviteRolesRecord, Long> ROLE_ID = createField(DSL.name("role_id"), SQLDataType.BIGINT.nullable(false), this, "");

    private GuildInviteRoles(Name alias, Table<GuildInviteRolesRecord> aliased) {
        this(alias, aliased, null);
    }

    private GuildInviteRoles(Name alias, Table<GuildInviteRolesRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.guild_invite_roles</code> table reference
     */
    public GuildInviteRoles(String alias) {
        this(DSL.name(alias), GUILD_INVITE_ROLES);
    }

    /**
     * Create an aliased <code>public.guild_invite_roles</code> table reference
     */
    public GuildInviteRoles(Name alias) {
        this(alias, GUILD_INVITE_ROLES);
    }

    /**
     * Create a <code>public.guild_invite_roles</code> table reference
     */
    public GuildInviteRoles() {
        this(DSL.name("guild_invite_roles"), null);
    }

    public <O extends Record> GuildInviteRoles(Table<O> child, ForeignKey<O, GuildInviteRolesRecord> key) {
        super(child, key, GUILD_INVITE_ROLES);
    }

    @Override
    public Schema getSchema() {
        return Public.PUBLIC;
    }

    @Override
    public Identity<GuildInviteRolesRecord, Long> getIdentity() {
        return (Identity<GuildInviteRolesRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<GuildInviteRolesRecord> getPrimaryKey() {
        return Keys.GUILD_INVITE_ROLES_PKEY;
    }

    @Override
    public List<UniqueKey<GuildInviteRolesRecord>> getKeys() {
        return Arrays.<UniqueKey<GuildInviteRolesRecord>>asList(Keys.GUILD_INVITE_ROLES_PKEY, Keys.GUILD_INVITE_ROLES_GUILD_INVITE_ID_ROLE_ID_KEY);
    }

    @Override
    public List<ForeignKey<GuildInviteRolesRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<GuildInviteRolesRecord, ?>>asList(Keys.GUILD_INVITE_ROLES__GUILD_INVITE_ROLES_GUILD_INVITE_ID_FKEY);
    }

    public GuildInvites guildInvites() {
        return new GuildInvites(this, Keys.GUILD_INVITE_ROLES__GUILD_INVITE_ROLES_GUILD_INVITE_ID_FKEY);
    }

    @Override
    public GuildInviteRoles as(String alias) {
        return new GuildInviteRoles(DSL.name(alias), this);
    }

    @Override
    public GuildInviteRoles as(Name alias) {
        return new GuildInviteRoles(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public GuildInviteRoles rename(String name) {
        return new GuildInviteRoles(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public GuildInviteRoles rename(Name name) {
        return new GuildInviteRoles(name, null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<Long, Long, Long> fieldsRow() {
        return (Row3) super.fieldsRow();
    }
}