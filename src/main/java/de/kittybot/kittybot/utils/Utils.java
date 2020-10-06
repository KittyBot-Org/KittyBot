package de.kittybot.kittybot.utils;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.kittybot.kittybot.objects.SelfAssignableRole;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Role;

import java.time.Duration;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class Utils{

	private static final String CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	public static String generate(int length){
		StringBuilder builder = new StringBuilder();
		while(length-- != 0){
			builder.append(CHARS.charAt((int) (ThreadLocalRandom.current().nextDouble() * CHARS.length())));
		}
		return builder.toString();
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

	public static Set<SelfAssignableRole> toSet(String guildId, String groupId, List<Role> roles, List<Emote> emotes){
		var list = new HashSet<SelfAssignableRole>();
		int i = 0;
		for(Role role : roles){
			if(emotes.size() <= i){
				break;
			}
			list.add(new SelfAssignableRole(guildId, groupId, role.getId(), emotes.get(i).getId()));
			i++;
		}
		return list;
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
		return count != 1 ? text + "s" : text;
	}

}
