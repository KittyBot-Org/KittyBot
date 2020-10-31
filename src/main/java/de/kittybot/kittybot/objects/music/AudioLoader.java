package de.kittybot.kittybot.objects.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.kittybot.kittybot.KittyBot;
import de.kittybot.kittybot.cache.GuildSettingsCache;
import de.kittybot.kittybot.cache.MusicManagerCache;
import de.kittybot.kittybot.utils.Utils;
import net.dv8tion.jda.api.entities.Guild;

import java.util.Collections;
import java.util.List;

public class AudioLoader{

	private AudioLoader(){}

	public static void loadQuery(final String query, final String requesterId, final Guild guild){
		final var musicManager = MusicManagerCache.getMusicManager(guild, true);

		KittyBot.getAudioPlayerManager().loadItemOrdered(musicManager, query, new AudioLoadResultHandler(){
			@Override
			public void trackLoaded(final AudioTrack track){
				track.setUserData(requesterId);
				queue(track, musicManager);
				sendQueuedTracks(Collections.singletonList(track), guild);
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

	private static void connectToVoiceChannel(final String requesterId, final Guild guild){
		final var member =
	}

	private static void sendQueuedTracks(final List<AudioTrack> tracks, final Guild guild){
		final var size = tracks.size();
		final var text = "Queued " + (size == 1 ? Utils.formatTrackTitle(tracks.get(0)) : "**" + size + "** tracks.\n\nType `" + GuildSettingsCache.getCommandPrefix(guild.getId()) + "queue` to see the queue.");
		final var musicManager = MusicManagerCache.getMusicManager(guild);
		final var controllerChannelId = musicManager.getControllerChannelId();
		if(controllerChannelId == null){
			return;
		}
		final var channel = guild.getTextChannelById(controllerChannelId);
		if(channel == null || !channel.canTalk()){
			return;
		}
		channel.sendMessage(text).queue();
	}
}