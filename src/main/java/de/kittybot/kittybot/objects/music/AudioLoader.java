package de.kittybot.kittybot.objects.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.kittybot.kittybot.KittyBot;
import de.kittybot.kittybot.cache.MusicManagerCache;
import de.kittybot.kittybot.objects.command.CommandContext;

import java.util.concurrent.Future;
import java.util.regex.Pattern;

public class AudioLoader{

	public static final Pattern YOUTUBE_URL_PATTERN = Pattern.compile("^(https?://)?((www|m)\\.)?youtu(\\.be|be\\.com)/(playlist\\?list=([a-zA-Z0-9-_]+))?((watch\\?v=)?([a-zA-Z0-9-_]{11})(&list=([a-zA-Z0-9-_]+))?)?");

	private AudioLoader(){}

	public static Future<Void> loadQuery(final CommandContext ctx){
		final var musicManager = MusicManagerCache.getMusicManager(ctx.getGuild(), true);
		var query = String.join(" ", ctx.getArgs());
		query = YOUTUBE_URL_PATTERN.matcher(query).matches() ? query : "ytsearch:" + query;

		return KittyBot.getAudioPlayerManager().loadItemOrdered(musicManager, query, new AudioLoadResultHandler(){
			@Override
			public void trackLoaded(final AudioTrack track){
				track.setUserData(ctx.getUser().getId());
				queue(track, musicManager);
			}

			@Override
			public void playlistLoaded(final AudioPlaylist playlist){

			}

			@Override
			public void noMatches(){

			}

			@Override
			public void loadFailed(final FriendlyException exception){

			}
		});
	}

	private static void queue(final AudioTrack track, final GuildMusicManager musicManager){
		if(!musicManager.startTrack(track, true)){
			musicManager.getQueue().offer(track);
		}
	}
}