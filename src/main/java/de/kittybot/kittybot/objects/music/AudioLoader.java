package de.kittybot.kittybot.objects.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;

import java.util.Collections;
import java.util.stream.Collectors;

public class AudioLoader implements AudioLoadResultHandler{


	private final GuildCommandContext ctx;
	private final MusicManager manager;

	public AudioLoader(GuildCommandContext ctx, MusicManager manager){
		this.ctx = ctx;
		this.manager = manager;
	}

	@Override
	public void trackLoaded(AudioTrack track){
		this.manager.connectToChannel(ctx);
		track.setUserData(ctx.getUserId());
		this.manager.getScheduler().queue(ctx, track, Collections.emptyList());
	}

	@Override
	public void playlistLoaded(AudioPlaylist playlist){
		this.manager.connectToChannel(this.ctx);
		for(var track : playlist.getTracks()){
			track.setUserData(this.ctx.getUserId());
		}
		var firstTrack = playlist.getTracks().get(0);
		if(playlist.isSearchResult()){
			this.manager.getScheduler().queue(this.ctx, firstTrack, Collections.emptyList());
			return;
		}

		var toPlay = playlist.getSelectedTrack() == null ? firstTrack : playlist.getSelectedTrack();
		this.manager.getScheduler().queue(this.ctx, toPlay, playlist.getTracks().stream().filter(track -> !track.equals(toPlay)).collect(Collectors.toList()));
	}

	@Override
	public void noMatches(){
		this.ctx.reply("No track found");
	}

	@Override
	public void loadFailed(FriendlyException e){
		this.ctx.reply("Failed to load track:\n" + e.getMessage());
	}

}
