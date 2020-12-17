/*
 * This file is generated by jOOQ.
 */
package de.kittybot.kittybot.jooq;


import de.kittybot.kittybot.jooq.tables.BotDisabledChannels;
import de.kittybot.kittybot.jooq.tables.GuildInviteRoles;
import de.kittybot.kittybot.jooq.tables.GuildInvites;
import de.kittybot.kittybot.jooq.tables.Guilds;
import de.kittybot.kittybot.jooq.tables.ReactiveMessages;
import de.kittybot.kittybot.jooq.tables.Requests;
import de.kittybot.kittybot.jooq.tables.SelfAssignableRoleGroups;
import de.kittybot.kittybot.jooq.tables.SelfAssignableRoles;
import de.kittybot.kittybot.jooq.tables.Sessions;
import de.kittybot.kittybot.jooq.tables.SnipeDisabledChannels;
import de.kittybot.kittybot.jooq.tables.StreamUserEvents;
import de.kittybot.kittybot.jooq.tables.StreamUsers;
import de.kittybot.kittybot.jooq.tables.UserStatistics;
import de.kittybot.kittybot.jooq.tables.records.BotDisabledChannelsRecord;
import de.kittybot.kittybot.jooq.tables.records.GuildInviteRolesRecord;
import de.kittybot.kittybot.jooq.tables.records.GuildInvitesRecord;
import de.kittybot.kittybot.jooq.tables.records.GuildsRecord;
import de.kittybot.kittybot.jooq.tables.records.ReactiveMessagesRecord;
import de.kittybot.kittybot.jooq.tables.records.RequestsRecord;
import de.kittybot.kittybot.jooq.tables.records.SelfAssignableRoleGroupsRecord;
import de.kittybot.kittybot.jooq.tables.records.SelfAssignableRolesRecord;
import de.kittybot.kittybot.jooq.tables.records.SessionsRecord;
import de.kittybot.kittybot.jooq.tables.records.SnipeDisabledChannelsRecord;
import de.kittybot.kittybot.jooq.tables.records.StreamUserEventsRecord;
import de.kittybot.kittybot.jooq.tables.records.StreamUsersRecord;
import de.kittybot.kittybot.jooq.tables.records.UserStatisticsRecord;

import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;


/**
 * A class modelling foreign key relationships and constraints of tables in 
 * public.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<BotDisabledChannelsRecord> BOT_DISABLED_CHANNELS_PKEY = Internal.createUniqueKey(BotDisabledChannels.BOT_DISABLED_CHANNELS, DSL.name("bot_disabled_channels_pkey"), new TableField[] { BotDisabledChannels.BOT_DISABLED_CHANNELS.GUILD_ID, BotDisabledChannels.BOT_DISABLED_CHANNELS.CHANNEL_ID }, true);
    public static final UniqueKey<GuildInviteRolesRecord> GUILD_INVITE_ROLES_PKEY = Internal.createUniqueKey(GuildInviteRoles.GUILD_INVITE_ROLES, DSL.name("guild_invite_roles_pkey"), new TableField[] { GuildInviteRoles.GUILD_INVITE_ROLES.GUILD_INVITE_ROLE_ID }, true);
    public static final UniqueKey<GuildInvitesRecord> GUILD_INVITES_PKEY = Internal.createUniqueKey(GuildInvites.GUILD_INVITES, DSL.name("guild_invites_pkey"), new TableField[] { GuildInvites.GUILD_INVITES.GUILD_INVITE_ID }, true);
    public static final UniqueKey<GuildsRecord> GUILDS_PKEY = Internal.createUniqueKey(Guilds.GUILDS, DSL.name("guilds_pkey"), new TableField[] { Guilds.GUILDS.GUILD_ID }, true);
    public static final UniqueKey<ReactiveMessagesRecord> REACTIVE_MESSAGES_PKEY = Internal.createUniqueKey(ReactiveMessages.REACTIVE_MESSAGES, DSL.name("reactive_messages_pkey"), new TableField[] { ReactiveMessages.REACTIVE_MESSAGES.REACTIVE_MESSAGE_ID }, true);
    public static final UniqueKey<RequestsRecord> REQUESTS_PKEY = Internal.createUniqueKey(Requests.REQUESTS, DSL.name("requests_pkey"), new TableField[] { Requests.REQUESTS.REQUEST_ID }, true);
    public static final UniqueKey<SelfAssignableRoleGroupsRecord> SELF_ASSIGNABLE_ROLE_GROUPS_PKEY = Internal.createUniqueKey(SelfAssignableRoleGroups.SELF_ASSIGNABLE_ROLE_GROUPS, DSL.name("self_assignable_role_groups_pkey"), new TableField[] { SelfAssignableRoleGroups.SELF_ASSIGNABLE_ROLE_GROUPS.SELF_ASSIGNABLE_ROLE_GROUP_ID }, true);
    public static final UniqueKey<SelfAssignableRolesRecord> SELF_ASSIGNABLE_ROLES_PKEY = Internal.createUniqueKey(SelfAssignableRoles.SELF_ASSIGNABLE_ROLES, DSL.name("self_assignable_roles_pkey"), new TableField[] { SelfAssignableRoles.SELF_ASSIGNABLE_ROLES.SELF_ASSIGNABLE_ROLE_ID }, true);
    public static final UniqueKey<SessionsRecord> SESSIONS_PKEY = Internal.createUniqueKey(Sessions.SESSIONS, DSL.name("sessions_pkey"), new TableField[] { Sessions.SESSIONS.USER_ID }, true);
    public static final UniqueKey<SnipeDisabledChannelsRecord> SNIPE_DISABLED_CHANNELS_PKEY = Internal.createUniqueKey(SnipeDisabledChannels.SNIPE_DISABLED_CHANNELS, DSL.name("snipe_disabled_channels_pkey"), new TableField[] { SnipeDisabledChannels.SNIPE_DISABLED_CHANNELS.SNIPE_DISABLED_CHANNEL_ID }, true);
    public static final UniqueKey<StreamUserEventsRecord> STREAM_USER_EVENTS_PKEY = Internal.createUniqueKey(StreamUserEvents.STREAM_USER_EVENTS, DSL.name("stream_user_events_pkey"), new TableField[] { StreamUserEvents.STREAM_USER_EVENTS.STEAM_USER_EVENT_ID }, true);
    public static final UniqueKey<StreamUsersRecord> STREAM_USERS_PKEY = Internal.createUniqueKey(StreamUsers.STREAM_USERS, DSL.name("stream_users_pkey"), new TableField[] { StreamUsers.STREAM_USERS.STREAM_USER_ID }, true);
    public static final UniqueKey<UserStatisticsRecord> USER_STATISTICS_PKEY = Internal.createUniqueKey(UserStatistics.USER_STATISTICS, DSL.name("user_statistics_pkey"), new TableField[] { UserStatistics.USER_STATISTICS.USER_STATISTIC_ID }, true);
}
