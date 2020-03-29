package de.anteiku.kittybot.events;

import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class OnGuildVoiceJoinEvent extends ListenerAdapter{
	
	private KittyBot main;
	
	public OnGuildVoiceJoinEvent(KittyBot main){
		this.main = main;
	}
	
	public void onGuildVoiceJoinEvent(GuildVoiceJoinEvent event){
	}
	
}
