package de.kittybot.kittybot.utils;

import de.kittybot.kittybot.cache.MusicPlayerCache;
import de.kittybot.kittybot.objects.command.CommandContext;

import static de.kittybot.kittybot.objects.command.ACommand.sendError;

public class MusicUtils{

	private MusicUtils(){}

	public static void seekTrack(final CommandContext ctx){
		final var voiceState = ctx.getMember().getVoiceState();
		if(voiceState == null){
			return;
		}
		if(!voiceState.inVoiceChannel()){
			sendError(ctx, "To use this command you need to be connected to a voice channel");
			return;
		}
		final var musicPlayer = MusicPlayerCache.getMusicPlayer(ctx.getGuild());
		if(musicPlayer == null){
			sendError(ctx, "No active music player found!");
			return;
		}
		final var player = musicPlayer.getPlayer();
		if(!player.getLink().getChannel().equals(voiceState.getChannel().getId())){
			sendError(ctx, "To use this command you need to be connected to the same voice channel as me");
			return;
		}
		final var playing = player.getPlayingTrack();
		if(playing == null){
			sendError(ctx, "There is currently no song playing");
			return;
		}
		if(!musicPlayer.getRequesterId().equals(ctx.getUser().getId())){
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
		final var position = player.getTrackPosition();
		switch(ctx.getCommand()){
			case "goto": // TODO this is a temp solution to also check aliases
			case "seek":
				if(toSeek >= duration && !musicPlayer.nextTrack()){
					player.stopTrack();
				}
				player.seekTo(toSeek);
				break;
			case "forward":
				if(position + toSeek >= duration){
					if(!musicPlayer.nextTrack()){
						player.stopTrack();
					}
					break;
				}
				player.seekTo(position + toSeek);
				break;
			case "rewind":
				if(position - toSeek <= 0){
					if(!musicPlayer.previousTrack()){
						player.stopTrack();
					}
					break;
				}
				player.seekTo(position - toSeek);
				break;
			default:
		}
		musicPlayer.updateMusicControlMessage(ctx.getChannel());
	}

}