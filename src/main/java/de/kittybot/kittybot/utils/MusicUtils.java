package de.kittybot.kittybot.utils;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.kittybot.kittybot.modules.PaginatorModule;
import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.objects.module.Modules;
import de.kittybot.kittybot.objects.music.TrackScheduler;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;

public class MusicUtils{

	private MusicUtils(){}

	public static String formatTracks(String message, Collection<AudioTrack> tracks){
		var trackMessage = new StringBuilder(message).append("\n");
		for(var track : tracks){
			var name = formatTrackWithInfo(track) + "\n";
			if(trackMessage.length() + name.length() >= 2048){
				break;
			}
			trackMessage.append(name);
		}
		return trackMessage.toString();
	}

	public static String formatTrackWithInfo(AudioTrack track){
		var info = track.getInfo();
		return formatTrack(track) + " - " + TimeUtils.formatDuration(info.length) + " [" + MessageUtils.getUserMention(track.getUserData(Long.class)) + "]";
	}

	public static String formatTrack(AudioTrack track){
		var info = track.getInfo();
		return MessageUtils.maskLink("`" + info.title + "`", info.uri);
	}

	public static boolean checkCommandRequirements(CommandContext ctx, TrackScheduler scheduler){
		if(scheduler == null){
			ctx.error("No active player found");
			return false;
		}
		return checkMusicRequirements(ctx);
	}

	public static boolean checkMusicRequirements(CommandContext ctx){
		var voiceState = ctx.getMember().getVoiceState();
		if(voiceState == null || voiceState.getChannel() == null){
			ctx.error("Please connect to a voice channel to use music commands");
			return false;
		}
		var myVoiceState = ctx.getSelfMember().getVoiceState();
		if(myVoiceState != null && myVoiceState.getChannel() != null && voiceState.getChannel().getIdLong() != myVoiceState.getChannel().getIdLong()){
			ctx.error("Please connect to the same voice channel as me to use music commands");
			return false;
		}
		return true;
	}

	public static boolean checkMusicPermissions(CommandContext ctx, TrackScheduler scheduler){
		var member = ctx.getMember();
		if(member.hasPermission(Permission.ADMINISTRATOR) || ctx.get(SettingsModule.class).hasDJRole(member)){
			return true;
		}
		var track = scheduler.getPlayingTrack();
		if(track == null){
			return false;
		}
		if(track.getUserData(Long.class) != member.getIdLong()){
			ctx.error("You are not the song requester or the DJ");
			return false;
		}
		return true;
	}

	public static void sendTracks(Collection<AudioTrack> tracks, Modules modules, TextChannel channel, long authorId, String baseMessage){
		if(channel == null){
			return;
		}
		var trackMessage = new StringBuilder("**").append(baseMessage).append(":**\n");
		var pages = new ArrayList<String>();

		var i = 1;
		for(var track : tracks){
			var formattedTrack = i + ". " + MusicUtils.formatTrackWithInfo(track) + "\n";
			if(trackMessage.length() + formattedTrack.length() >= 2048){
				pages.add(trackMessage.toString());
				trackMessage = new StringBuilder();
			}
			trackMessage.append(formattedTrack);
			i++;
		}
		pages.add(trackMessage.toString());

		modules.get(PaginatorModule.class).create(
			channel,
			authorId,
			pages.size(),
			(page, embedBuilder) -> embedBuilder.setColor(Colors.KITTYBOT_BLUE)
				.setDescription(pages.get(page))
				.setTimestamp(Instant.now())
		);
	}

}
