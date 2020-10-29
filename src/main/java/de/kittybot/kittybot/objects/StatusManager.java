package de.kittybot.kittybot.objects;

import de.kittybot.kittybot.KittyBot;
import de.kittybot.kittybot.utils.MessageUtils;
import de.kittybot.kittybot.utils.Utils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class StatusManager{

	private static final List<String> STATUS_MESSAGES = MessageUtils.loadMessageFile("status");

	private StatusManager(){}

	public static void newRandomStatus(){
		var jda = KittyBot.getJda();
		jda.getPresence().setPresence(OnlineStatus.ONLINE, generateRandomMessage(jda));
	}

	private static Activity generateRandomMessage(JDA jda){
		if(STATUS_MESSAGES == null || STATUS_MESSAGES.isEmpty()){
			return Activity.watching("you \uD83D\uDC40");
		}
		var randomMessage = STATUS_MESSAGES.get(ThreadLocalRandom.current().nextInt(STATUS_MESSAGES.size() - 1));

		var activityMessage = randomMessage.split("\\s+", 2);
		var type = activityMessage[0].toUpperCase();
		var message = activityMessage[1];

		message = message.replace("${total_users}", String.valueOf(Utils.getUserCount(jda)));
		message = message.replace("${total_guilds}", String.valueOf(jda.getGuildCache().size()));
		return Activity.of(Activity.ActivityType.valueOf(type), message);
	}

}
