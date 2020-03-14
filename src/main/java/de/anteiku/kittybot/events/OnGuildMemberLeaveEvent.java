package de.anteiku.kittybot.events;

import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class OnGuildMemberLeaveEvent extends ListenerAdapter{
	
	@SuppressWarnings("unused")
	private KittyBot main;
	
	public OnGuildMemberLeaveEvent(KittyBot main){
		this.main = main;
	}
	
	@Override
	public void onGuildMemberLeave(GuildMemberLeaveEvent event){
	}
	
}
