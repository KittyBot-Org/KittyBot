package de.anteiku.kittybot.events;

import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class OnGuildMemberUpdateBoostTimeEvent extends ListenerAdapter{
	
	private final KittyBot main;
	
	public OnGuildMemberUpdateBoostTimeEvent(KittyBot main){
		this.main = main;
	}
	
	@Override
	public void onGuildMemberUpdateBoostTime(GuildMemberUpdateBoostTimeEvent event){
		//TODO
	}
	
}
