package de.anteiku.kittybot.events;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.objects.Config;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class OnReadyEvent extends ListenerAdapter{

	private KittyBot main;

	public OnReadyEvent(KittyBot main){
		this.main = main;
	}

	@Override
	public void onReady(ReadyEvent event){
		var jda = event.getJDA();
		main.sendToPublicLogChannel(jda, Config.SUPPORT_GUILD, Config.LOG_CHANNEL, "I'm ready!");
		jda.getPresence().setStatus(OnlineStatus.ONLINE);
		jda.getPresence().setActivity(Activity.watching("you \uD83D\uDC40"));
	}
}

