package de.kittybot.kittybot.modules;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import de.kittybot.kittybot.module.Modules;
import de.kittybot.kittybot.utils.exporters.Metrics;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class InviteRolesModule extends ListenerAdapter{

	private final Modules modules;

	public InviteRolesModule(Modules modules){
		this.modules = modules;
	}

	@Override
	public void onGuildInviteDelete(@Nonnull GuildInviteDeleteEvent event){
		this.modules.getGuildSettingsModule().removeInviteRoles(event.getGuild().getIdLong(), event.getCode());
	}

	@Override
	public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event){
		Metrics.ACTIONS.labels("invite_roles").inc();
		var userId = event.getUser().getIdLong();
		var invite = this.modules.getInviteModule().getUsedInvite(event.getGuild().getIdLong(), userId);
		if(invite == null){
			return;
		}
		var roles = this.modules.getGuildSettingsModule().getInviteRoles(event.getGuild().getIdLong(), invite.getCode());
		for(var roleId : roles){
			var role = event.getGuild().getRoleById(roleId);
			if(role == null){
				continue;
			}
			event.getGuild().addRoleToMember(userId, role).queue();
		}
	}

	@Override
	public void onRoleDelete(@NotNull RoleDeleteEvent event){
		this.modules.getGuildSettingsModule().removeInviteRole(event.getGuild().getIdLong(), event.getRole().getIdLong());
	}

}
