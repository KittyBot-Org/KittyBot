package de.kittybot.kittybot.events;

import net.dv8tion.jda.api.events.emote.EmoteRemovedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class OnEmoteEvent extends ListenerAdapter{


	@Override
	public void onEmoteRemoved(EmoteRemovedEvent event){
		//TODO check if emote is mapped for a self-assignable role
	}

}
