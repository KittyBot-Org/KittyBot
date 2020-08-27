package de.anteiku.kittybot.events;

import de.anteiku.kittybot.objects.BotLists;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class OnReadyEvent extends ListenerAdapter{


	@Override
	public final void onReady(@NotNull ReadyEvent event){
		BotLists.update(event.getJDA(), event.getGuildTotalCount());
	}

}
