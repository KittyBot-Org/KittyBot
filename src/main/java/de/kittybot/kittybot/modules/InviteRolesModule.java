package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.objects.module.Module;
import de.kittybot.kittybot.utils.exporters.Metrics;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.role.RoleDeleteEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Set;

@SuppressWarnings("unused")
public class InviteRolesModule extends Module{

	private static final Set<Class<? extends Module>> DEPENDENCIES = Set.of(InviteModule.class);

	@Override
	public Set<Class<? extends Module>> getDependencies(){
		return DEPENDENCIES;
	}

	@Override
	public void onGuildInviteDelete(@Nonnull GuildInviteDeleteEvent event){
		this.modules.get(GuildSettingsModule.class).removeInviteRoles(event.getGuild().getIdLong(), event.getCode());
	}

	@Override
	public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event){
		var userId = event.getUser().getIdLong();
		var guildId = event.getGuild().getIdLong();
		var invite = this.modules.get(InviteModule.class).getUsedInvite(guildId, userId);
		if(invite == null){
			return;
		}
		var roles = this.modules.get(GuildSettingsModule.class).getInviteRoles(guildId, invite.getCode());
		if(roles == null){
			return;
		}
		Metrics.ACTIONS.labels("invite_roles").inc();
		var selfMember = event.getGuild().getSelfMember();
		for(var roleId : roles){
			var role = event.getGuild().getRoleById(roleId);
			if(role == null){
				continue;
			}
			if(!selfMember.canInteract(role)){
				continue;
			}
			event.getGuild().addRoleToMember(userId, role).queue();
		}
	}

	@Override
	public void onRoleDelete(@NotNull RoleDeleteEvent event){
		this.modules.get(GuildSettingsModule.class).removeInviteRole(event.getGuild().getIdLong(), event.getRole().getIdLong());
	}

}
