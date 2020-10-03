package de.kittybot.kittybot.objects;

import de.kittybot.kittybot.KittyBot;
import de.kittybot.kittybot.utils.MessageUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class StatusManager{

	private static final List<String> STATUS_MESSAGES = MessageUtils.loadMessageFile("status");

	public static void newRandomStatus(){
		var jda = KittyBot.getJda();
		jda.getPresence().setPresence(OnlineStatus.ONLINE, generateRandomMessage(jda));
	}

	private static Activity generateRandomMessage(JDA jda){
		if(STATUS_MESSAGES == null || STATUS_MESSAGES.isEmpty()){
			return Activity.watching("you \uD83D\uDC40");
		}
		String randomMessage = STATUS_MESSAGES.get(ThreadLocalRandom.current().nextInt(STATUS_MESSAGES.size() - 1));

		var activityMessage = randomMessage.split("\\s+", 2);
		var type = activityMessage[0].toUpperCase();
		var message = activityMessage[1];

		var guildCache = jda.getGuildCache();
		var memberCount = guildCache.applyStream(guildStream -> guildStream.mapToInt(Guild::getMemberCount).sum());
		message = message.replace("${total_users}", String.valueOf(memberCount));
		message = message.replace("${total_guilds}", String.valueOf(guildCache.size()));
		return Activity.of(Activity.ActivityType.valueOf(type), message);
	}

}
