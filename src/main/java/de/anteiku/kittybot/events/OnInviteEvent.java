package de.anteiku.kittybot.events;

import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.api.events.emote.EmoteRemovedEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class OnInviteEvent extends ListenerAdapter{

	private final KittyBot main;

	public OnInviteEvent(KittyBot main){
		this.main = main;
	}

	@Override
	public void onGuildInviteCreate(GuildInviteCreateEvent event){
		event.getInvite().
	}


	@Override
	public void onGuildInviteDelete(GuildInviteDeleteEvent event){

	}

}
