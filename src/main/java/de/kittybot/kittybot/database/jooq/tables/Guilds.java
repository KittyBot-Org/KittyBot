/*
 * This file is generated by jOOQ.
 */
package de.kittybot.kittybot.database.jooq.tables;


import de.kittybot.kittybot.database.jooq.Keys;
import de.kittybot.kittybot.database.jooq.Public;
import de.kittybot.kittybot.database.jooq.tables.records.GuildsRecord;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;

import java.util.Arrays;
import java.util.List;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class Guilds extends TableImpl<GuildsRecord>{

	/**
	 * The reference instance of <code>public.guilds</code>
	 */
	public static final Guilds GUILDS = new Guilds();
	private static final long serialVersionUID = -1814477830;
	/**
	 * The column <code>public.guilds.guild_id</code>.
	 */
	public final TableField<GuildsRecord, String> GUILD_ID = createField(DSL.name("guild_id"), org.jooq.impl.SQLDataType.VARCHAR(18).nullable(false), this, "");
	/**
	 * The column <code>public.guilds.command_prefix</code>.
	 */
	public final TableField<GuildsRecord, String> COMMAND_PREFIX = createField(DSL.name("command_prefix"), org.jooq.impl.SQLDataType.VARCHAR(1).nullable(false), this, "");
	/**
	 * The column <code>public.guilds.request_channel_id</code>.
	 */
	public final TableField<GuildsRecord, String> REQUEST_CHANNEL_ID = createField(DSL.name("request_channel_id"), org.jooq.impl.SQLDataType.VARCHAR(18).nullable(false), this, "");
	/**
	 * The column <code>public.guilds.requests_enabled</code>.
	 */
	public final TableField<GuildsRecord, Boolean> REQUESTS_ENABLED = createField(DSL.name("requests_enabled"), org.jooq.impl.SQLDataType.BOOLEAN.nullable(false), this, "");
	/**
	 * The column <code>public.guilds.announcement_channel_id</code>.
	 */
	public final TableField<GuildsRecord, String> ANNOUNCEMENT_CHANNEL_ID = createField(DSL.name("announcement_channel_id"), org.jooq.impl.SQLDataType.VARCHAR(18).nullable(false), this, "");
	/**
	 * The column <code>public.guilds.join_messages</code>.
	 */
	public final TableField<GuildsRecord, String> JOIN_MESSAGES = createField(DSL.name("join_messages"), org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");
	/**
	 * The column <code>public.guilds.join_messages_enabled</code>.
	 */
	public final TableField<GuildsRecord, Boolean> JOIN_MESSAGES_ENABLED = createField(DSL.name("join_messages_enabled"), org.jooq.impl.SQLDataType.BOOLEAN.nullable(false), this, "");
	/**
	 * The column <code>public.guilds.leave_messages</code>.
	 */
	public final TableField<GuildsRecord, String> LEAVE_MESSAGES = createField(DSL.name("leave_messages"), org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");
	/**
	 * The column <code>public.guilds.leave_messages_enabled</code>.
	 */
	public final TableField<GuildsRecord, Boolean> LEAVE_MESSAGES_ENABLED = createField(DSL.name("leave_messages_enabled"), org.jooq.impl.SQLDataType.BOOLEAN.nullable(false), this, "");
	/**
	 * The column <code>public.guilds.boost_messages</code>.
	 */
	public final TableField<GuildsRecord, String> BOOST_MESSAGES = createField(DSL.name("boost_messages"), org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");
	/**
	 * The column <code>public.guilds.boost_messages_enabled</code>.
	 */
	public final TableField<GuildsRecord, Boolean> BOOST_MESSAGES_ENABLED = createField(DSL.name("boost_messages_enabled"), org.jooq.impl.SQLDataType.BOOLEAN.nullable(false), this, "");
	/**
	 * The column <code>public.guilds.log_channel_id</code>.
	 */
	public final TableField<GuildsRecord, String> LOG_CHANNEL_ID = createField(DSL.name("log_channel_id"), org.jooq.impl.SQLDataType.VARCHAR(18).nullable(false), this, "");
	/**
	 * The column <code>public.guilds.log_messages_enabled</code>.
	 */
	public final TableField<GuildsRecord, Boolean> LOG_MESSAGES_ENABLED = createField(DSL.name("log_messages_enabled"), org.jooq.impl.SQLDataType.BOOLEAN.nullable(false), this, "");
	/**
	 * The column <code>public.guilds.nsfw_enabled</code>.
	 */
	public final TableField<GuildsRecord, Boolean> NSFW_ENABLED = createField(DSL.name("nsfw_enabled"), org.jooq.impl.SQLDataType.BOOLEAN.nullable(false), this, "");
	/**
	 * The column <code>public.guilds.inactive_role_id</code>.
	 */
	public final TableField<GuildsRecord, String> INACTIVE_ROLE_ID = createField(DSL.name("inactive_role_id"), org.jooq.impl.SQLDataType.VARCHAR(18).nullable(false), this, "");

	/**
	 * Create a <code>public.guilds</code> table reference
	 */
	public Guilds(){
		this(DSL.name("guilds"), null);
	}

	private Guilds(Name alias, Table<GuildsRecord> aliased){
		this(alias, aliased, null);
	}

	private Guilds(Name alias, Table<GuildsRecord> aliased, Field<?>[] parameters){
		super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
	}

	/**
	 * Create an aliased <code>public.guilds</code> table reference
	 */
	public Guilds(String alias){
		this(DSL.name(alias), GUILDS);
	}

	/**
	 * Create an aliased <code>public.guilds</code> table reference
	 */
	public Guilds(Name alias){
		this(alias, GUILDS);
	}

	public <O extends Record> Guilds(Table<O> child, ForeignKey<O, GuildsRecord> key){
		super(child, key, GUILDS);
	}

	@Override
	public Guilds as(String alias){
		return new Guilds(DSL.name(alias), this);
	}

	@Override
	public Schema getSchema(){
		return Public.PUBLIC;
	}

	@Override
	public UniqueKey<GuildsRecord> getPrimaryKey(){
		return Keys.GUILDS_PKEY;
	}

	@Override
	public List<UniqueKey<GuildsRecord>> getKeys(){
		return Arrays.<UniqueKey<GuildsRecord>>asList(Keys.GUILDS_PKEY);
	}

	@Override
	public Guilds as(Name alias){
		return new Guilds(alias, this);
	}

	/**
	 * Rename this table
	 */
	@Override
	public Guilds rename(String name){
		return new Guilds(DSL.name(name), null);
	}

	/**
	 * Rename this table
	 */
	@Override
	public Guilds rename(Name name){
		return new Guilds(name, null);
	}

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<GuildsRecord> getRecordType(){
		return GuildsRecord.class;
	}

	// -------------------------------------------------------------------------
	// Row15 type methods
	// -------------------------------------------------------------------------

	@Override
	public Row15<String, String, String, Boolean, String, String, Boolean, String, Boolean, String, Boolean, String, Boolean, Boolean, String> fieldsRow(){
		return (Row15) super.fieldsRow();
	}

}
