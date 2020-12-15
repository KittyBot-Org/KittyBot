/*
 * This file is generated by jOOQ.
 */
package de.kittybot.kittybot.database.jooq;


import de.kittybot.kittybot.database.jooq.tables.Commands;
import de.kittybot.kittybot.database.jooq.tables.Guilds;
import de.kittybot.kittybot.database.jooq.tables.ReactiveMessages;
import de.kittybot.kittybot.database.jooq.tables.SelfAssignableRoles;
import de.kittybot.kittybot.database.jooq.tables.Sessions;
import de.kittybot.kittybot.database.jooq.tables.UserStatistics;

import java.util.Arrays;
import java.util.List;

import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Public extends SchemaImpl {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public</code>
     */
    public static final Public PUBLIC = new Public();

    /**
     * The table <code>public.commands</code>.
     */
    public final Commands COMMANDS = Commands.COMMANDS;

    /**
     * The table <code>public.guilds</code>.
     */
    public final Guilds GUILDS = Guilds.GUILDS;

    /**
     * The table <code>public.reactive_messages</code>.
     */
    public final ReactiveMessages REACTIVE_MESSAGES = ReactiveMessages.REACTIVE_MESSAGES;

    /**
     * The table <code>public.self_assignable_roles</code>.
     */
    public final SelfAssignableRoles SELF_ASSIGNABLE_ROLES = SelfAssignableRoles.SELF_ASSIGNABLE_ROLES;

    /**
     * The table <code>public.sessions</code>.
     */
    public final Sessions SESSIONS = Sessions.SESSIONS;

    /**
     * The table <code>public.user_statistics</code>.
     */
    public final UserStatistics USER_STATISTICS = UserStatistics.USER_STATISTICS;

    /**
     * No further instances allowed
     */
    private Public() {
        super("public", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        return Arrays.<Table<?>>asList(
            Commands.COMMANDS,
            Guilds.GUILDS,
            ReactiveMessages.REACTIVE_MESSAGES,
            SelfAssignableRoles.SELF_ASSIGNABLE_ROLES,
            Sessions.SESSIONS,
            UserStatistics.USER_STATISTICS);
    }
}