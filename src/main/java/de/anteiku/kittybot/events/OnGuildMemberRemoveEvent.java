package de.anteiku.kittybot.events;

import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class OnGuildMemberRemoveEvent extends ListenerAdapter{

	private final KittyBot main;

	public OnGuildMemberRemoveEvent(KittyBot main){
		this.main = main;
	}

	@Override
	public void onGuildMemberRemove(GuildMemberRemoveEvent event){
		//TODO
	}

}
