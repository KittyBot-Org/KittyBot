package de.anteiku.kittybot.events;

import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class OnGuildVoiceLeaveEvent extends ListenerAdapter{
	
	private final KittyBot main;
	
	public OnGuildVoiceLeaveEvent(KittyBot main){
		this.main = main;
	}
	
	public void onGuildVoiceLeaveEvent(GuildVoiceLeaveEvent event){
	}
	
}
