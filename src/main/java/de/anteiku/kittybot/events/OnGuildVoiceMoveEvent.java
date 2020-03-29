package de.anteiku.kittybot.events;

import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class OnGuildVoiceMoveEvent extends ListenerAdapter{
	
	private KittyBot main;
	
	public OnGuildVoiceMoveEvent(KittyBot main){
		this.main = main;
	}
	
	public void onGuildVoiceMoveEvent(GuildVoiceMoveEvent event){
	}
	
}
