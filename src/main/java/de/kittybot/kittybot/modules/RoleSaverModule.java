package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.module.Module;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberUpdateEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static de.kittybot.kittybot.jooq.Tables.MEMBER_ROLES;

@SuppressWarnings("unused")
public class RoleSaverModule extends Module{

	@Override
	public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event){
		var member = event.getMember();
		var guild = event.getGuild();
		var roles = retrieveGuildUserRoles(guild.getIdLong(), member.getIdLong());
		guild.modifyMemberRoles(member, roles.stream().map(guild::getRoleById).filter(Objects::nonNull).collect(Collectors.toSet()), null).queue();
	}

	@Override
	public void onGuildMemberUpdate(@Nonnull GuildMemberUpdateEvent event){
		var member = event.getMember();
		insertGuildUserRoles(event.getGuild().getIdLong(), member.getIdLong(), member.getRoles().stream().map(Role::getIdLong).collect(Collectors.toSet()));
	}

	@Override
	public void onRoleDelete(@Nonnull RoleDeleteEvent event){
		deleteGuildUserRole(event.getGuild().getIdLong(), event.getRole().getIdLong());
	}

	private void deleteGuildUserRole(long guildId, long roleId){
		this.modules.get(DatabaseModule.class).getCtx().deleteFrom(MEMBER_ROLES)
			.where(MEMBER_ROLES.GUILD_ID.eq(guildId).and(MEMBER_ROLES.ROLE_ID.eq(roleId)))
			.execute();
	}

	private void insertGuildUserRoles(long guildId, long userId, Set<Long> roles){
		var ctxRoles = this.modules.get(DatabaseModule.class).getCtx().insertInto(MEMBER_ROLES)
			.columns(MEMBER_ROLES.GUILD_ID, MEMBER_ROLES.USER_ID, MEMBER_ROLES.ROLE_ID);
		for(var role : roles){
			ctxRoles.values(guildId, userId, role);
		}
		ctxRoles.onDuplicateKeyIgnore().execute();
	}

	private List<Long> retrieveGuildUserRoles(long guildId, long userId){
		var dbModule = this.modules.get(DatabaseModule.class);
		try(var ctx = dbModule.getCtx().selectFrom(MEMBER_ROLES)){
			return ctx.where(MEMBER_ROLES.GUILD_ID.eq(guildId).and(MEMBER_ROLES.USER_ID.eq(userId))).fetch().map(entry -> entry.get(MEMBER_ROLES.ROLE_ID));
		}
	}

}
