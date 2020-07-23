package de.anteiku.kittybot.events;

import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.api.events.emote.EmoteRemovedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class OnEmoteEvent extends ListenerAdapter{

	private final KittyBot main;

	public OnEmoteEvent(KittyBot main){
		this.main = main;
	}

	@Override
	public void onEmoteRemoved(EmoteRemovedEvent event){
		//TODO check if emote is mapped for a self-assignable role
	}

}
