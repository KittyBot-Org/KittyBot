package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.module.Module;
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

public class StatusModule extends Module{

	private List<String> status_messages;

	@Override
	public void onEnable(){
		this.status_messages = FileUtils.loadMessageFile("status");
	}

	@Override
	public void onReady(@Nonnull ReadyEvent event){
		this.modules.getScheduler().scheduleAtFixedRate(this::newRandomStatus, 0, 2, TimeUnit.MINUTES);
	}

	public void newRandomStatus(){
		var jda = this.modules.getJDA(0);
		jda.getPresence().setPresence(OnlineStatus.ONLINE, generateRandomMessage(jda));
	}

	private Activity generateRandomMessage(JDA jda){
		if(status_messages == null || status_messages.isEmpty()){
			return Activity.watching("you \uD83D\uDC40");
		}
		var randomMessage = status_messages.get(ThreadLocalRandom.current().nextInt(status_messages.size() - 1));

		var activityMessage = randomMessage.split("\\s+", 2);
		var type = activityMessage[0].toUpperCase();
		var message = activityMessage[1];

		message = message.replace("${total_users}", String.valueOf(Utils.getUserCount(this.modules.getShardManager())));
		message = message.replace("${total_guilds}", String.valueOf(jda.getGuildCache().size()));
		return Activity.of(Activity.ActivityType.valueOf(type), message);
	}

}
