package de.kittybot.kittybot.utils;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.kittybot.kittybot.objects.Config;
import de.kittybot.kittybot.objects.SelfAssignableRole;
import de.kittybot.kittybot.objects.requests.API;
import de.kittybot.kittybot.objects.requests.Requester;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.utils.MiscUtil;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Utils{

	private Utils(){}

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

	public static Set<SelfAssignableRole> toSet(String guildId, String groupId, List<Role> roles, List<Emote> emotes){
		return roles.stream().map(role ->
				emotes.get(roles.indexOf(role)) == null ? null : new SelfAssignableRole(guildId, groupId, role.getId(), emotes.get(roles.indexOf(role)).getId())
		).filter(Objects::nonNull).collect(Collectors.toSet());
	}

	public static boolean isSnowflake(String id){
		try{
			MiscUtil.parseSnowflake(id);
			return true;
		}
		catch(NumberFormatException ignored){
			return false;
		}
	}

	public static String formatDuration(long length){
		var duration = Duration.ofMillis(length);
		var seconds = duration.toSecondsPart();
		return String.format("%d:%s", duration.toMinutes(), seconds > 9 ? seconds : "0" + seconds);
	}

	public static String formatDurationDHMS(long length){
		Duration duration = Duration.ofMillis(length);
		return String.format(
				"%sd %s:%s:%s", duration.toDays(), fTime(duration.toHoursPart()), fTime(duration.toMinutesPart()), fTime(duration.toSecondsPart()));
	}

	public static String fTime(int time){
		return time > 9 ? String.valueOf(time) : "0" + time;
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
		if(Config.isSet(Config.DISCORD_BOTS_TOKEN)){
			Requester.updateStats(API.DISCORD_BOTS, guildCount);
		}
		if(Config.isSet(Config.TOP_GG_TOKEN)){
			Requester.updateStats(API.TOP_GG, guildCount);
		}
		if(Config.isSet(Config.DISCORD_EXTREME_LIST_TOKEN)){
			Requester.updateStats(API.DISCORD_EXTREME_LIST, guildCount);
		}
		if(Config.isSet(Config.DISCORD_BOATS_TOKEN)){
			Requester.updateStats(API.DISCORD_BOATS, guildCount);
		}
	}

}
