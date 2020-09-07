package de.anteiku.kittybot.objects.spotify;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.wrapper.spotify.model_objects.specification.Track;
import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.objects.MusicPlayer;
import de.anteiku.kittybot.objects.cache.MusicPlayerCache;
import de.anteiku.kittybot.objects.command.CommandContext;

import java.util.ArrayList;
import java.util.regex.Matcher;

import static de.anteiku.kittybot.objects.command.ACommand.sendError;

public class SpotifyLoader{

	public static void load(CommandContext ctx, Matcher matcher){
		switch(matcher.group(3)){
			case "album":
				loadAlbum(matcher.group(4), ctx);
				break;
			case "track":
				loadTrack(matcher.group(4), ctx);
				break;
			case "playlist":
				loadPlaylist(matcher.group(4), ctx);
				break;
			default:
		}
	}

	private static void loadAlbum(String id, CommandContext ctx){
		SpotifyAPI.getAPI().getAlbumsTracks(id).build().executeAsync().thenAcceptAsync(tracks -> {
			var items = tracks.getItems();
			var toLoad = new ArrayList<String>();
			for(var track : items){
				toLoad.add("ytsearch:" + track.getArtists()[0].getName() + " " + track.getName());
			}
			loadTracks(id, ctx, MusicPlayerCache.getMusicPlayer(ctx.getGuild()), toLoad);
		}).exceptionally(throwable -> {
			sendError(ctx, throwable.getMessage().contains("invalid id") ? "Album not found" : "There was an error while loading the album");
			return null;
		});
	}

	private static void loadTrack(String id, CommandContext ctx){
		var player = MusicPlayerCache.getMusicPlayer(ctx.getGuild());
		SpotifyAPI.getAPI().getTrack(id).build().executeAsync().thenAcceptAsync(track -> KittyBot.getAudioPlayerManager().loadItem("ytsearch:" + track.getArtists()[0].getName() + " " + track.getName(), new AudioLoadResultHandler(){
			@Override
			public void trackLoaded(AudioTrack track){
				player.trackLoaded(track);
			}

			@Override
			public void playlistLoaded(AudioPlaylist playlist){
				player.playlistLoaded(playlist);
			}

			@Override
			public void noMatches(){
				sendError(ctx, "No track found");
			}

			@Override
			public void loadFailed(FriendlyException exception){
				player.loadFailed(exception);
			}
		})).exceptionally(throwable -> {
			sendError(ctx, throwable.getMessage().contains("invalid id") ? "Track not found" : "There was an error while loading the track");
			return null;
		});
	}

	private static void loadPlaylist(String id, CommandContext ctx){
		SpotifyAPI.getAPI().getPlaylistsItems(id).build().executeAsync().thenAcceptAsync(tracks -> {
			var items = tracks.getItems();
			var toLoad = new ArrayList<String>();
			for(var item : items){
				var track = (Track) item.getTrack();
				toLoad.add("ytsearch:" + track.getArtists()[0].getName() + " " + track.getName());
			}
			loadTracks(id, ctx, MusicPlayerCache.getMusicPlayer(ctx.getGuild()), toLoad);
		}).exceptionally(throwable -> {
			sendError(ctx, throwable.getMessage().contains("Invalid playlist Id") ? "Playlist not found" : "There was an error while loading the playlist");
			return null;
		});
	}

	private static void loadTracks(String id, CommandContext ctx, MusicPlayer player, ArrayList<String> toLoad){
		var future = player.getFuture();
		var audioManager = KittyBot.getAudioPlayerManager();
		var queuedTracks = new ArrayList<AudioTrack>();
		toLoad.forEach(query -> audioManager.loadItemOrdered(id, query, new AudioLoadResultHandler(){
			@Override
			public void trackLoaded(AudioTrack track){
				track.setUserData(ctx.getUser().getId());
				player.queue(track);
			}

			@Override
			public void playlistLoaded(AudioPlaylist playlist){
				var size = toLoad.size();
				if(playlist.isSearchResult()){
					var track = playlist.getTracks().get(0);
					if(!player.lengthCheck(track.getDuration()) && size == 1){
						sendError(ctx, "The maximum length of a track is 20 minutes");
						return;
					}
					track.setUserData(ctx.getUser().getId());
					queuedTracks.add(track);
					player.queue(track);
				}
				else{
					for(AudioTrack track : playlist.getTracks()){
						if(!player.lengthCheck(track.getDuration())){
							if(size == 1){
								sendError(ctx, "The maximum length of a track is 20 minutes");
								return;
							}
							else{
								continue;
							}
						}
						track.setUserData(ctx.getUser().getId());
						queuedTracks.add(track);
						player.queue(track);
					}
				}
			}

			@Override
			public void noMatches(){}

			@Override
			public void loadFailed(FriendlyException exception){
				player.loadFailed(exception);
			}
		}));
		if(!player.getQueue().isEmpty()){
			player.sendQueuedTracks(ctx, queuedTracks);
		}
		if(future != null){
			future.cancel(true);
		}
		player.connectToChannel(ctx);
	}

}