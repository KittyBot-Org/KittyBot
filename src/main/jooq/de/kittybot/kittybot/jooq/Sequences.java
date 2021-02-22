/*
 * This file is generated by jOOQ.
 */
package de.kittybot.kittybot.jooq;


import org.jooq.Sequence;
import org.jooq.impl.Internal;
import org.jooq.impl.SQLDataType;


/**
 * Convenience access to all sequences in public.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Sequences {

    /**
     * The sequence <code>public.bot_disabled_channels_id_seq</code>
     */
    public static final Sequence<Long> BOT_DISABLED_CHANNELS_ID_SEQ = Internal.createSequence("bot_disabled_channels_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.bot_ignored_members_id_seq</code>
     */
    public static final Sequence<Long> BOT_IGNORED_MEMBERS_ID_SEQ = Internal.createSequence("bot_ignored_members_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.guild_invite_roles_id_seq</code>
     */
    public static final Sequence<Long> GUILD_INVITE_ROLES_ID_SEQ = Internal.createSequence("guild_invite_roles_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.guild_invites_id_seq</code>
     */
    public static final Sequence<Long> GUILD_INVITES_ID_SEQ = Internal.createSequence("guild_invites_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.guild_tags_id_seq</code>
     */
    public static final Sequence<Long> GUILD_TAGS_ID_SEQ = Internal.createSequence("guild_tags_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.member_roles_id_seq</code>
     */
    public static final Sequence<Long> MEMBER_ROLES_ID_SEQ = Internal.createSequence("member_roles_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.notifications_id_seq</code>
     */
    public static final Sequence<Long> NOTIFICATIONS_ID_SEQ = Internal.createSequence("notifications_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.reactive_messages_id_seq</code>
     */
    public static final Sequence<Long> REACTIVE_MESSAGES_ID_SEQ = Internal.createSequence("reactive_messages_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.requests_id_seq</code>
     */
    public static final Sequence<Long> REQUESTS_ID_SEQ = Internal.createSequence("requests_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.self_assignable_role_groups_id_seq</code>
     */
    public static final Sequence<Long> SELF_ASSIGNABLE_ROLE_GROUPS_ID_SEQ = Internal.createSequence("self_assignable_role_groups_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.self_assignable_role_messages_id_seq</code>
     */
    public static final Sequence<Long> SELF_ASSIGNABLE_ROLE_MESSAGES_ID_SEQ = Internal.createSequence("self_assignable_role_messages_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.self_assignable_roles_id_seq</code>
     */
    public static final Sequence<Long> SELF_ASSIGNABLE_ROLES_ID_SEQ = Internal.createSequence("self_assignable_roles_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.sessions_id_seq</code>
     */
    public static final Sequence<Long> SESSIONS_ID_SEQ = Internal.createSequence("sessions_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.snipe_disabled_channels_id_seq</code>
     */
    public static final Sequence<Long> SNIPE_DISABLED_CHANNELS_ID_SEQ = Internal.createSequence("snipe_disabled_channels_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.stream_user_events_id_seq</code>
     */
    public static final Sequence<Long> STREAM_USER_EVENTS_ID_SEQ = Internal.createSequence("stream_user_events_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.stream_users_id_seq</code>
     */
    public static final Sequence<Long> STREAM_USERS_ID_SEQ = Internal.createSequence("stream_users_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.user_statistics_id_seq</code>
     */
    public static final Sequence<Long> USER_STATISTICS_ID_SEQ = Internal.createSequence("user_statistics_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);

    /**
     * The sequence <code>public.voters_id_seq</code>
     */
    public static final Sequence<Long> VOTERS_ID_SEQ = Internal.createSequence("voters_id_seq", Public.PUBLIC, SQLDataType.BIGINT.nullable(false), null, null, null, null, false, null);
}
