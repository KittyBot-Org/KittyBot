package de.kittybot.kittybot.utils;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.kittybot.kittybot.objects.command.CommandContext;
import net.dv8tion.jda.api.Permission;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static de.kittybot.kittybot.utils.AudioUtils.ConnectFailureReason.*;
import static java.util.concurrent.CompletableFuture.completedFuture;

public class AudioUtils {
	public static final Pattern YOUTUBE_URL_PATTERN = Pattern.compile("^(https?://)?((www|m)\\.)?youtu(\\.be|be\\.com)/(playlist\\?list=([a-zA-Z0-9-_]+))?((watch\\?v=)?([a-zA-Z0-9-_]{11})(&list=([a-zA-Z0-9-_]+))?)?");

	public static CompletionStage<ConnectFailureReason> checkVoiceChannel(final CommandContext ctx){
		final var voiceState = ctx.getMember().getVoiceState();
		if (voiceState == null){
			return completedFuture(NO_CHANNEL);
		}
		final var voiceChannel = voiceState.getChannel();
		if (voiceChannel == null){
			return completedFuture(NO_CHANNEL);
		}
		final var selfMember = ctx.getGuild().getSelfMember();
		if (!selfMember.hasAccess(voiceChannel)){
			return completedFuture(NO_PERMS);
		}
		final var userLimit = voiceChannel.getUserLimit();
		if (userLimit != 0 && voiceChannel.getMembers().size() >= userLimit){
			return completedFuture(CHANNEL_FULL);
		}
		if (!selfMember.hasPermission(voiceChannel, Permission.VOICE_SPEAK)){
			return completedFuture(CANT_SPEAK);
		}
		return completedFuture(null);
	}

	public static boolean isValidLength(final AudioTrack track){
		return TimeUnit.MILLISECONDS.toMinutes(track.getDuration()) <= 20;
	}

	public enum ConnectFailureReason{
		NO_CHANNEL("you're not connected to any channel"),
		NO_PERMS("i don't have permissions to join your channel"),
		CHANNEL_FULL("the voice channel you're in is full"),
		CANT_SPEAK("i can't speak in your channel");

		private final String reason;

		ConnectFailureReason(final String reason) {
			this.reason = reason;
		}

		public String getReason(){
			return reason;
		}
	}
}
