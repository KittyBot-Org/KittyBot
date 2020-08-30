package de.anteiku.kittybot.objects;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.utils.MessageUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class StatusManager{

	private static final List<String> STATUS_MESSAGES = MessageUtils.loadMessageFile("status");

	public static void newRandomStatus(){
		var jda = KittyBot.getJda();
		jda.getPresence().setActivity(generateRandomMessage(jda));
	}

	private static Activity generateRandomMessage(JDA jda){
		if(STATUS_MESSAGES == null || STATUS_MESSAGES.isEmpty()){
			return Activity.watching("you \uD83D\uDC40");
		}
		String randomMessage = STATUS_MESSAGES.get(ThreadLocalRandom.current().nextInt(STATUS_MESSAGES.size() - 1));

		var activityMessage = randomMessage.split(" ", 1);
		var message = activityMessage[1];

		message = message.replace("${total_users}", String.valueOf(jda.getUserCache().size()));
		message = message.replace("${total_guilds}", String.valueOf(jda.getGuildCache().size()));
		return Activity.of(Activity.ActivityType.valueOf(activityMessage[0]), message);
	}

}
