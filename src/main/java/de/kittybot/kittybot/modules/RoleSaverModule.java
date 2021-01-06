package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.module.Module;
import de.kittybot.kittybot.module.Modules;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberUpdateEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.util.Set;
import java.util.stream.Collectors;

import static de.kittybot.kittybot.jooq.Tables.GUILD_USERS;
import static de.kittybot.kittybot.jooq.Tables.GUILD_USER_ROLES;

public class RoleSaverModule extends Module{

	private static final Logger LOG = LoggerFactory.getLogger(RoleSaverModule.class);

	private final Modules modules;

	public RoleSaverModule(Modules modules){
		this.modules = modules;
	}

	@Override
	public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event){
		var member = event.getMember();
		var guild = event.getGuild();
		var roles = retrieveGuildUserRoles(event.getGuild().getIdLong(), member.getIdLong());
		if(roles == null){
			return;
		}
		guild.modifyMemberRoles(member, roles.stream().map(guild::getRoleById).collect(Collectors.toSet()), null).queue();
	}

	@Override
	public void onGuildMemberUpdate(@Nonnull GuildMemberUpdateEvent event){
		var member = event.getMember();
		insertGuildUserRoles(
				event.getGuild().getIdLong(), member.getIdLong(), member.getRoles().stream().map(Role::getIdLong).collect(Collectors.toSet()));
	}

	@Override
	public void onRoleDelete(@Nonnull RoleDeleteEvent event){
		deleteGuildUserRole(event.getGuild().getIdLong(), event.getRole().getIdLong());
	}

	private void deleteGuildUserRole(long guildId, long roleId){
		var dbModule = this.modules.getDatabaseModule();
		try(var con = dbModule.getCon()){
			dbModule.getCtx(con).deleteFrom(GUILD_USER_ROLES)
					.where(GUILD_USER_ROLES.ROLE_ID.eq(roleId))
					.execute();
		}
		catch(SQLException e){
			LOG.error("Error while deleting guild user roles for guild: {} and role: {}", guildId, roleId, e);
		}
	}

	private void insertGuildUserRoles(long guildId, long userId, Set<Long> roles){
		var dbModule = this.modules.getDatabaseModule();
		try(var con = dbModule.getCon(); var ctx = dbModule.getCtx(con).selectFrom(GUILD_USERS)){
			var res = ctx.where(GUILD_USERS.GUILD_ID.eq(guildId).and(GUILD_USERS.USER_ID.eq(userId))).fetchOne();
			var guildUserId = 0L;
			if(res == null){
				var res2 = dbModule.getCtx(con).insertInto(GUILD_USERS)
						.columns(GUILD_USERS.GUILD_ID, GUILD_USERS.USER_ID)
						.values(guildId, userId)
						.onConflictDoNothing()
						.returningResult(GUILD_USERS.GUILD_USER_ID)
						.fetchOne();
				if(res2 == null){
					LOG.error("Cane we have another problem! Tickle Topi!");
					return;
				}
				guildUserId = res2.get(GUILD_USERS.GUILD_USER_ID);
			}
			else{
				guildUserId = res.getGuildUserId();
			}

			var ctxRoles = dbModule.getCtx(con).insertInto(GUILD_USER_ROLES)
					.columns(GUILD_USER_ROLES.GUILD_USER_ID, GUILD_USER_ROLES.ROLE_ID);
			for(var role : roles){
				ctxRoles.values(guildUserId, role);
			}
			ctxRoles.onDuplicateKeyIgnore().execute();
		}
		catch(SQLException e){
			LOG.error("Error while inserting guild user roles for guild: {} and user: {}", guildId, userId, e);
		}
	}

	private Set<Long> retrieveGuildUserRoles(long guildId, long userId){
		var dbModule = this.modules.getDatabaseModule();
		try(var con = dbModule.getCon(); var ctx = dbModule.getCtx(con).select()){
			var res = ctx.from(GUILD_USER_ROLES).join(GUILD_USERS).on(GUILD_USER_ROLES.GUILD_USER_ID.eq(GUILD_USERS.GUILD_USER_ID))
					.where(GUILD_USERS.GUILD_ID.eq(guildId).and(GUILD_USERS.USER_ID.eq(userId))).fetch();
			return res.stream().map(entry -> entry.get(GUILD_USER_ROLES.ROLE_ID)).collect(Collectors.toSet());
		}
		catch(SQLException e){
			LOG.error("Error while retrieving guild user roles for guild: {} and user: {}", guildId, userId, e);
		}
		return null;
	}

}
