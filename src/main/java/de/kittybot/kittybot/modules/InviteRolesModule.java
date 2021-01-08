package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.module.Module;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import de.kittybot.kittybot.module.Modules;
import de.kittybot.kittybot.utils.exporters.Metrics;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class InviteRolesModule extends Module{

	private final Modules modules;

	public InviteRolesModule(Modules modules){
		this.modules = modules;
	}

	@Override
	public void onGuildInviteDelete(@Nonnull GuildInviteDeleteEvent event){
		this.modules.get(SettingsModule.class).removeInviteRoles(event.getGuild().getIdLong(), event.getCode());
	}

	@Override
	public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event){
		Metrics.ACTIONS.labels("invite_roles").inc();
		var userId = event.getUser().getIdLong();
		var invite = this.modules.get(InviteModule.class).getUsedInvite(event.getGuild().getIdLong(), userId);
		if(invite == null){
			return;
		}
		var roles = this.modules.get(SettingsModule.class).getInviteRoles(event.getGuild().getIdLong(), invite.getCode());
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
		this.modules.get(SettingsModule.class).removeInviteRole(event.getGuild().getIdLong(), event.getRole().getIdLong());
	}

}
