package de.kittybot.kittybot.objects.music;

import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import lavalink.client.io.FriendlyException;
import lavalink.client.io.LoadResultHandler;
import lavalink.client.player.track.AudioPlaylist;
import lavalink.client.player.track.AudioTrack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AudioLoader implements LoadResultHandler{

	private static final Logger LOG = LoggerFactory.getLogger(AudioLoader.class);

	private final GuildInteraction ia;
	private final MusicManager manager;

	public AudioLoader(GuildInteraction ia, MusicManager manager){
		this.ia = ia;
		this.manager = manager;
	}

	@Override
	public void trackLoaded(AudioTrack track){
		this.manager.connectToChannel(ia);
		track.setUserData(ia.getUserId());
		this.manager.getScheduler().queue(ia, track, Collections.emptyList());
	}

	@Override
	public void playlistLoaded(AudioPlaylist playlist){
		this.manager.connectToChannel(this.ia);
		for(var track : playlist.getTracks()){
			track.setUserData(this.ia.getUserId());
		}
		var firstTrack = playlist.getTracks().get(0);

		var toPlay = playlist.getSelectedTrack() == null ? firstTrack : playlist.getSelectedTrack();
		this.manager.getScheduler().queue(this.ia, toPlay, playlist.getTracks().stream().filter(track -> !track.equals(toPlay)).collect(Collectors.toList()));
	}

	@Override
	public void searchResultLoaded(List<AudioTrack> tracks){
		this.manager.connectToChannel(this.ia);
		var track = tracks.get(0);
		track.setUserData(this.ia.getUserId());
		this.manager.getScheduler().queue(this.ia, track, Collections.emptyList());
	}

	@Override
	public void noMatches(){
		this.ia.reply("No track found");
	}

	@Override
	public void loadFailed(FriendlyException e){
		LOG.error("Failed to load track:", e);
		this.ia.reply("Failed to load track:\n" + e.getMessage());
	}

}
