package de.kittybot.kittybot.utils;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.kittybot.kittybot.objects.Config;
import de.kittybot.kittybot.objects.requests.API;
import de.kittybot.kittybot.objects.requests.Requester;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Utils{

	private static final String CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	private Utils(){}

	public static String generate(int length){
		StringBuilder sb = new StringBuilder(length);
		for(var i = 0; i < length; i++){
			sb.append(CHARS.charAt(ThreadLocalRandom.current().nextInt(CHARS.length())));
		}
		return sb.toString();
	}

	public static boolean isEnable(String string){
		return string.equalsIgnoreCase("enable") || string.equalsIgnoreCase("true") || string.equalsIgnoreCase("on") || string.equalsIgnoreCase("an");
	}

	public static boolean isDisable(String string){
		return string.equalsIgnoreCase("disable") || string.equalsIgnoreCase("false") || string.equalsIgnoreCase("off") || string.equalsIgnoreCase("aus");
	}

	public static boolean isHelp(String string){
		return string.equalsIgnoreCase("?") || string.equalsIgnoreCase("help") || string.equalsIgnoreCase("hilfe");
	}

	public static Set<String> toSet(List<Role> roles){
		return roles.stream().map(Role::getId).collect(Collectors.toSet());
	}

	public static Map<String, String> toMap(List<Role> roles, List<Emote> emotes){
		Map<String, String> map = new HashMap<>();
		int i = 0;
		for(Role role : roles){
			if(emotes.size() <= i){
				break;
			}
			map.put(role.getId(), emotes.get(i).getId());
			i++;
		}
		return map;
	}

	public static String formatDuration(long length){
		Duration duration = Duration.ofMillis(length);
		var seconds = duration.toSecondsPart();
		return String.format("%d:%s", duration.toMinutes(), seconds > 9 ? seconds : "0" + seconds);
	}

	public static String formatTrackTitle(AudioTrack track){
		var info = track.getInfo();
		return "[" + info.title + "]" + "(" + info.uri + ")";
	}

	public static <T> String pluralize(String text, Collection<T> collection){
		return pluralize(text, collection.size());
	}

	public static String pluralize(String text, int count){
		return count == 1 ? text : text + "s";
	}

	public static int getUserCount(JDA jda){
		//noinspection ConstantConditions shut
		return jda.getGuildCache().applyStream(guildStream -> guildStream.mapToInt(Guild::getMemberCount).sum());
	}

	public static void updateStats(final int guildCount){
		if(Config.isSet(Config.TOP_GG_TOKEN)){
			Requester.updateStats(API.TOP_GG, guildCount);
		}
		if(Config.isSet(Config.DISCORD_BOTS_TOKEN)){
			Requester.updateStats(API.DISCORD_BOTS, guildCount);
		}
	}

}
