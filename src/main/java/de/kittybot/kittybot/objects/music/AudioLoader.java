package de.kittybot.kittybot.objects.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;

import java.util.Collections;
import java.util.stream.Collectors;

public class AudioLoader implements AudioLoadResultHandler{


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
		if(playlist.isSearchResult()){
			this.manager.getScheduler().queue(this.ia, firstTrack, Collections.emptyList());
			return;
		}

		var toPlay = playlist.getSelectedTrack() == null ? firstTrack : playlist.getSelectedTrack();
		this.manager.getScheduler().queue(this.ia, toPlay, playlist.getTracks().stream().filter(track -> !track.equals(toPlay)).collect(Collectors.toList()));
	}

	@Override
	public void noMatches(){
		this.ia.reply("No track found");
	}

	@Override
	public void loadFailed(FriendlyException e){
		this.ia.reply("Failed to load track:\n" + e.getMessage());
	}

}
