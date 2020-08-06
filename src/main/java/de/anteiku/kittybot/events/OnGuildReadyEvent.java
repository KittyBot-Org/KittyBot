package de.anteiku.kittybot.events;

import de.anteiku.kittybot.objects.Cache;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class OnGuildReadyEvent extends ListenerAdapter{

	@Override
	public final void onGuildReady(GuildReadyEvent event){
		Cache.initGuildInviteCache(event.getGuild());
	}

}
