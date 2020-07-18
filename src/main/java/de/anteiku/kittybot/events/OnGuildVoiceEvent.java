package de.anteiku.kittybot.events;

import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class OnGuildVoiceEvent extends ListenerAdapter{

	private final KittyBot main;

	public OnGuildVoiceEvent(KittyBot main){
		this.main = main;
	}

	@Override
	public void onGuildVoiceJoin(GuildVoiceJoinEvent event){

	}

	@Override
	public void onGuildVoiceMove(GuildVoiceMoveEvent event){

	}

	@Override
	public void onGuildVoiceLeave(GuildVoiceLeaveEvent event){

	}

}
