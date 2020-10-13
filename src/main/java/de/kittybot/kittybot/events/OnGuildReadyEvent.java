package de.kittybot.kittybot.events;

import de.kittybot.kittybot.cache.InviteCache;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class OnGuildReadyEvent extends ListenerAdapter{

	@Override
	public void onGuildReady(GuildReadyEvent event){
		InviteCache.initCaching(event.getGuild());
	}

}
