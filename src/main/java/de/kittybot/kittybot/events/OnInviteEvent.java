package de.kittybot.kittybot.events;

import de.kittybot.kittybot.objects.cache.InviteCache;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteDeleteEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class OnInviteEvent extends ListenerAdapter{

	@Override
	public void onGuildInviteCreate(GuildInviteCreateEvent event){
		InviteCache.cacheInvite(event.getInvite());
	}

	@Override
	public void onGuildInviteDelete(GuildInviteDeleteEvent event){
		InviteCache.uncacheInvite(event.getGuild().getId(), event.getCode());
	}

}
