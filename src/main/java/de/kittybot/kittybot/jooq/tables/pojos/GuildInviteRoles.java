/*
 * This file is generated by jOOQ.
 */
package de.kittybot.kittybot.jooq.tables.pojos;


import java.io.Serializable;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class GuildInviteRoles implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Long guildInviteRoleId;
    private final Long guildInviteId;
    private final Long roleId;

    public GuildInviteRoles(GuildInviteRoles value) {
        this.guildInviteRoleId = value.guildInviteRoleId;
        this.guildInviteId = value.guildInviteId;
        this.roleId = value.roleId;
    }

    public GuildInviteRoles(
        Long guildInviteRoleId,
        Long guildInviteId,
        Long roleId
    ) {
        this.guildInviteRoleId = guildInviteRoleId;
        this.guildInviteId = guildInviteId;
        this.roleId = roleId;
    }

    /**
     * Getter for <code>public.guild_invite_roles.guild_invite_role_id</code>.
     */
    public Long getGuildInviteRoleId() {
        return this.guildInviteRoleId;
    }

    /**
     * Getter for <code>public.guild_invite_roles.guild_invite_id</code>.
     */
    public Long getGuildInviteId() {
        return this.guildInviteId;
    }

    /**
     * Getter for <code>public.guild_invite_roles.role_id</code>.
     */
    public Long getRoleId() {
        return this.roleId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("GuildInviteRoles (");

        sb.append(guildInviteRoleId);
        sb.append(", ").append(guildInviteId);
        sb.append(", ").append(roleId);

        sb.append(")");
        return sb.toString();
    }
}
