package de.kittybot.kittybot.managers;

import de.kittybot.kittybot.main.KittyBot;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class InviteRolesManager extends ListenerAdapter{

	private final KittyBot main;

	public InviteRolesManager(KittyBot main){
		this.main = main;
	}

	@Override
	public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event){

	}

	@Override
	public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event){
		//MetricsExporter.ACTIONS.labels("invite_roles").inc();
	}

}
