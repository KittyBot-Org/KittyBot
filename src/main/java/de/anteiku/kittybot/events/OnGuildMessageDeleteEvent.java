package de.anteiku.kittybot.events;

import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class OnGuildMessageDeleteEvent extends ListenerAdapter{

	private final KittyBot main;

	public OnGuildMessageDeleteEvent(KittyBot main){
		this.main = main;
	}

	@Override
	public void onGuildMessageDelete(GuildMessageDeleteEvent event){
		KittyBot.commandManager.processCommandResponseDelete(event.getChannel(), event.getMessageId());
	}

}
