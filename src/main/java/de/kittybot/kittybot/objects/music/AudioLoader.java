package de.kittybot.kittybot.objects.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.kittybot.kittybot.KittyBot;
import de.kittybot.kittybot.cache.MusicManagerCache;
import net.dv8tion.jda.api.entities.Guild;

public class AudioLoader{

	private AudioLoader(){}

	public static void loadQuery(final String query, final String requesterId, final Guild guild, final String channelId){
		final var musicManager = MusicManagerCache.getMusicManager(guild, true);

		KittyBot.getAudioPlayerManager().loadItemOrdered(musicManager, query, new AudioLoadResultHandler(){
			@Override
			public void trackLoaded(final AudioTrack track){

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

	private void queue(final AudioTrack track, final GuildMusicManager musicManager){
		if(!musicManager.getAudioPlayer().startTrack(track, true)){
			musicManager.getQueue().offer(track);
		}
	}
}