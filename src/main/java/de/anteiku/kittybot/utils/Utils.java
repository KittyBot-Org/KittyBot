package de.anteiku.kittybot.utils;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.anteiku.kittybot.objects.MusicPlayer;
import de.anteiku.kittybot.objects.cache.GuildSettingsCache;
import de.anteiku.kittybot.objects.command.ACommand;
import de.anteiku.kittybot.objects.command.CommandContext;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.entities.Role;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static de.anteiku.kittybot.objects.command.ACommand.sendError;
import static de.anteiku.kittybot.utils.MessageUtils.buildResponse;

public class Utils{

	private static final String CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	public static String generate(int length){
		StringBuilder builder = new StringBuilder();
		while(length-- != 0){
			builder.append(CHARS.charAt(ThreadLocalRandom.current().nextInt() * CHARS.length()));
		}
		return builder.toString();
	}

	public static String getUserMention(String userId){
		if(userId.equals("-1")){
			return "unset";
		}
		return "<@" + userId + ">";
	}

	public static String getRoleMention(String roleId){
		if(roleId.equals("-1")){
			return "unset";
		}
		return "<@&" + roleId + ">";
	}

	public static String getChannelMention(String channelId){
		if(channelId.equals("-1")){
			return "unset";
		}
		return "<#" + channelId + ">";
	}

	public static String getEmoteMention(String emoteId){
		if(emoteId.equals("-1")){
			return "unset";
		}
		return "<:i:" + emoteId + ">";
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
		Set<String> set = new HashSet<>();
		for(Role role : roles){
			set.add(role.getId());
		}
		return set;
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

	public static String[] subArray(String[] array, int start){
		return subArray(array, start, array.length);
	}

	public static String[] subArray(String[] array, int start, int end){
		String[] strings = new String[end - start];
		int a = 0;
		for(int i = 0; i < array.length; i++){
			if(i >= start && i <= end){
				strings[a] = array[i];
				a++;
			}
		}
		return strings;
	}

	public static void processQueue(ACommand command, CommandContext ctx, MusicPlayer player){
		if(player == null){
			sendError(ctx, "No active music player found!");
			return;
		}
		if(ctx.getArgs().length > 0){
			var voiceState = ctx.getMember().getVoiceState();
			if(voiceState != null && !voiceState.inVoiceChannel()){
				sendError(ctx, "To use this command you need to be connected to a voice channel");
				return;
			}
			player.loadItem(command, ctx);
			return;
		}
		var queue = player.getQueue();
		if(queue.isEmpty()){
			sendError(ctx, "There are currently no tracks queued");
			return;
		}
		var message = new StringBuilder("Currently **")
				.append(queue.size())
				.append("** ")
				.append(pluralize("track", queue))
				.append(" ")
				.append(queue.size() > 1 ? "are" : "is")
				.append(" queued:\n\n");
		var trackList = ((List<AudioTrack>) queue);
		for(int i = 0; i < trackList.size(); i++){
			var track = trackList.get(i);
			message.append(i + 1).append(") ").append(formatTrackTitle(track)).append(" ").append(formatDuration(track.getDuration())).append("\n");
		}
		var prefix = GuildSettingsCache.getCommandPrefix(ctx.getGuild().getId());
		message.append("\nTo add more songs to the queue, use `").append(prefix).append("play`").append(" or `").append(prefix).append("queue`.");
		message.append("\nPro tip: To remove songs from the queue, you can use `").append(prefix).append("remove <position>").append("`."); // TODO maybe add a "tip" about being able to skip to a track with given position
		buildResponse(ctx, message);
	}

	public static <T> String pluralize(String text, Collection<T> collection){
		return collection.size() != 1 ? text + "s" : text;
	}

	public static String formatTrackTitle(AudioTrack track){
		var info = track.getInfo();
		return "[`" + info.title + "`]" + "(" + info.uri + ")";
	}

	public static String formatDuration(long length){
		Duration duration = Duration.ofMillis(length);
		var seconds = duration.toSecondsPart();
		return String.format("%d:%s", duration.toMinutes(), seconds > 9 ? seconds : "0" + seconds);
	}

	public static void processHistory(CommandContext ctx, MusicPlayer player){
		if(player == null){
			sendError(ctx, "No active music player found!");
			return;
		}
		var history = player.getHistory();
		if(history.isEmpty()){
			sendError(ctx, "There are currently no tracks in history");
			return;
		}
		var message = new StringBuilder("Currently **")
				.append(history.size())
				.append("** ")
				.append(Utils.pluralize("track", history))
				.append(" ")
				.append(history.size() > 1 ? "are" : "is")
				.append(" in the history:\n\n");
		var historyIterator = history.descendingIterator();
		while(historyIterator.hasNext()){
			var track = historyIterator.next();
			message.append(formatTrackTitle(track)).append(" ").append(formatDuration(track.getDuration())).append("\n");
		}
		message.append("\nNote: The history is sorted from the last track to the most recent one.");
		buildResponse(ctx, message);
	}

}
