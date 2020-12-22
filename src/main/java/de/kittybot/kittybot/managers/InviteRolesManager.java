package de.kittybot.kittybot.managers;

import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.utils.exporters.Metrics;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.stream.Collectors;

public class InviteRolesManager extends ListenerAdapter{

	private final KittyBot main;

	public InviteRolesManager(KittyBot main){
		this.main = main;
	}

	@Override
	public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event){
		Metrics.ACTIONS.labels("invite_roles").inc();
		var userId = event.getUser().getIdLong();
		var invite = this.main.getInviteManager().getUsedInvite(event.getGuild().getIdLong(), userId);
		if(invite == null){
			return;
		}
		var roles = this.main.getGuildSettingsManager().getInviteRoles(event.getGuild().getIdLong(), invite.getCode());
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
		this.main.getGuildSettingsManager().removeInviteRole(event.getGuild().getIdLong(), event.getRole().getIdLong());
	}

}
