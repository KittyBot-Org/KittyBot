package de.kittybot.kittybot.events;

import de.kittybot.kittybot.main.KittyBot;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class OnGuildVoiceEvent extends ListenerAdapter{

	private final KittyBot main;

	public OnGuildVoiceEvent(KittyBot main){
		this.main = main;
	}

	@Override
	public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event){

	}

}
