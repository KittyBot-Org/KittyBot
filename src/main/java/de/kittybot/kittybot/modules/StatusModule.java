package de.kittybot.kittybot.modules;

import net.dv8tion.jda.api.hooks.ListenerAdapter;
import de.kittybot.kittybot.module.Modules;
import de.kittybot.kittybot.utils.FileUtils;
import de.kittybot.kittybot.utils.Utils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class StatusModule extends ListenerAdapter{

	private final List<String> status_messages;
	private final Modules modules;

	public StatusModule(Modules modules){
		this.modules = modules;
		this.status_messages = FileUtils.loadMessageFile("status");
	}

	@Override
	public void onReady(@Nonnull ReadyEvent event){
		this.modules.getScheduler().scheduleAtFixedRate(this::newRandomStatus, 0, 2, TimeUnit.MINUTES);
	}

	public void newRandomStatus(){
		this.modules.getJDA().getPresence().setPresence(OnlineStatus.ONLINE, generateRandomMessage(this.modules.getJDA()));
	}

	private Activity generateRandomMessage(JDA jda){
		if(status_messages == null || status_messages.isEmpty()){
			return Activity.watching("you \uD83D\uDC40");
		}
		var randomMessage = status_messages.get(ThreadLocalRandom.current().nextInt(status_messages.size() - 1));

		var activityMessage = randomMessage.split("\\s+", 2);
		var type = activityMessage[0].toUpperCase();
		var message = activityMessage[1];

		message = message.replace("${total_users}", String.valueOf(Utils.getUserCount(jda)));
		message = message.replace("${total_guilds}", String.valueOf(jda.getGuildCache().size()));
		return Activity.of(Activity.ActivityType.valueOf(type), message);
	}

}
