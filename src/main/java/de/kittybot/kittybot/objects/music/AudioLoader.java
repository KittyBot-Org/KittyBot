package de.kittybot.kittybot.objects.music;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import lavalink.client.io.jda.JdaLink;

import java.util.Collections;
import java.util.stream.Collectors;

public class AudioLoader implements AudioLoadResultHandler{


	private final CommandContext ctx;
	private final MusicManager manager;

	public AudioLoader(CommandContext ctx, MusicManager manager){
		this.ctx = ctx;
		this.manager = manager;
	}

	@Override
	public void trackLoaded(AudioTrack track){
		this.manager.connectToChannel(ctx);
		track.setUserData(ctx.getUser().getIdLong());
		this.manager.getScheduler().queue(ctx, track, Collections.emptyList());
	}

	@Override
	public void playlistLoaded(AudioPlaylist playlist){
		this.manager.connectToChannel(ctx);
		for(var track : playlist.getTracks()){
			track.setUserData(ctx.getUser().getIdLong());
		}
		var firstTrack = playlist.getTracks().get(0);
		if(playlist.isSearchResult()){
			firstTrack.setUserData(ctx.getUser().getIdLong());
			this.manager.getScheduler().queue(ctx, firstTrack, Collections.emptyList());
			return;
		}

		var toPlay = playlist.getSelectedTrack() == null ? firstTrack : playlist.getSelectedTrack();
		this.manager.getScheduler().queue(ctx, toPlay, playlist.getTracks().stream().filter(track -> !track.equals(toPlay)).collect(Collectors.toList()));
	}

	@Override
	public void noMatches(){
		ctx.reply("No track found");
	}

	@Override
	public void loadFailed(FriendlyException e){
		ctx.reply("Failed to load track:\n" + e.getMessage());
	}

}
