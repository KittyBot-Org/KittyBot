package de.anteiku.kittybot.events;

import de.anteiku.kittybot.objects.Cache;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class OnInviteEvent extends ListenerAdapter{

	public OnInviteEvent(){}

	@Override
	public void onGuildInviteCreate(GuildInviteCreateEvent event){
		Cache.addNewInvite(event.getInvite());
	}


	@Override
	public void onGuildInviteDelete(GuildInviteDeleteEvent event){
		Cache.deleteInvite(event.getGuild().getId(), event.getCode());
	}

}
