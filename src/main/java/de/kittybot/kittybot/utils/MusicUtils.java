package de.kittybot.kittybot.utils;

import de.kittybot.kittybot.cache.MusicManagerCache;
import de.kittybot.kittybot.objects.command.CommandContext;
import net.dv8tion.jda.api.Permission;

import static de.kittybot.kittybot.objects.command.ACommand.sendError;
import static de.kittybot.kittybot.utils.MusicUtils.ConnectFailureReason.*;

public class MusicUtils{

	private MusicUtils(){}

	public static void seekTrack(final CommandContext ctx){
		final var voiceState = ctx.getMember().getVoiceState();
		if(voiceState == null){
			return;
		}
		final var voiceChannel = voiceState.getChannel();
		if(voiceChannel == null){
			sendError(ctx, "To use this command you need to be connected to a voice channel");
			return;
		}
		final var guild = ctx.getGuild();
		final var musicManager = MusicManagerCache.getMusicManager(guild);
		if(musicManager == null){
			sendError(ctx, "No active music player found!");
			return;
		}
		final var connectedChannel = guild.getAudioManager().getConnectedChannel();
		if(connectedChannel == null){ // someone disconnected the bot for some fucking reason
			MusicManagerCache.destroyMusicManager(guild);
			return;
		}
		if(!connectedChannel.getId().equals(voiceChannel.getId())){
			sendError(ctx, "To use this command you need to be connected to the same voice channel as me");
			return;
		}
		final var player = musicManager.getAudioPlayer();
		final var playing = player.getPlayingTrack();
		if(playing == null){
			sendError(ctx, "There is currently no song playing");
			return;
		}
		if(!musicManager.getRequesterId().equals(ctx.getUser().getId())){
			sendError(ctx, "You have to be the requester of the song to control it");
			return;
		}
		final var args = ctx.getArgs();
		if(args.length == 0){
			sendError(ctx, "Please provide the amount of seconds");
			return;
		}
		var toSeek = 0;
		try{
			toSeek = Integer.parseUnsignedInt(args[0]);
		}
		catch(final NumberFormatException ex){
			sendError(ctx, "Please provide a valid amount of seconds");
			return;
		}
		toSeek *= 1000;
		final var duration = playing.getDuration();
		final var position = playing.getPosition();
		switch(ctx.getCommand()){
			case "goto": // TODO this is a temp solution to also check aliases
			case "seek":
				if(toSeek >= duration){
					musicManager.nextTrack();
					return;
				}
				playing.setPosition(toSeek);
				break;
			case "forward":
				if(position + toSeek >= duration){
					musicManager.nextTrack();
					break;
				}
				playing.setPosition(position + toSeek);
				break;
			case "rewind":
				if(position - toSeek <= 0){
					musicManager.previousTrack();
					break;
				}
				playing.setPosition(position - toSeek);
				break;
			default:
		}
		musicManager.updateMusicControlMessage(ctx.getChannel());
	}

	public static ConnectFailureReason checkVoiceChannel(final CommandContext ctx){
		final var voiceState = ctx.getMember().getVoiceState();
		if(voiceState == null){
			return NO_CHANNEL;
		}
		final var voiceChannel = voiceState.getChannel();
		if(voiceChannel == null){
			return NO_CHANNEL;
		}
		final var selfMember = ctx.getGuild().getSelfMember();
		if(!selfMember.hasAccess(voiceChannel)){
			return NO_PERMS;
		}
		final var userLimit = voiceChannel.getUserLimit();
		if(userLimit != 0 && voiceChannel.getMembers().size() >= userLimit){
			return CHANNEL_FULL;
		}
		if(!selfMember.hasPermission(voiceChannel, Permission.VOICE_SPEAK)){
			return CANT_SPEAK;
		}
		return null;
	}

	public static int parseVolume(final int volume, final int oldVolume){
		return volume < 0 ? Math.max(oldVolume + volume, 0) // a + (-b) = -
				: Math.min(oldVolume + volume, 200);
	}

	public static int parseVolume(final String volumeToParse, final int oldVolume) throws NumberFormatException{
		var volume = Integer.parseInt(volumeToParse);
		if(!(volumeToParse.charAt(0) == '+' || volumeToParse.charAt(0) == '-')){
			return Math.min(volume, 200);
		}
		return parseVolume(volume, oldVolume);
	}

	public enum ConnectFailureReason{
		NO_CHANNEL("you're not connected to any channel"),
		NO_PERMS("i don't have permissions to join your channel"),
		CHANNEL_FULL("the voice channel you're in is full"),
		CANT_SPEAK("i can't speak in your channel");

		private final String reason;

		ConnectFailureReason(final String reason){
			this.reason = reason;
		}

		public String getReason(){
			return reason;
		}
	}

}