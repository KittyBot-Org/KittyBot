package de.kittybot.kittybot.modules;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import de.kittybot.kittybot.objects.module.Module;
import de.kittybot.kittybot.objects.music.MusicManager;
import de.kittybot.kittybot.objects.music.SearchProvider;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class SpotifyModule extends Module{

	private static final Logger LOG = LoggerFactory.getLogger(SpotifyModule.class);

	private SpotifyApi spotify;
	private ClientCredentialsRequest clientCredentialsRequest;
	private int hits;

	@Override
	public void onEnable(){
		this.spotify = new SpotifyApi.Builder().setClientId(Config.SPOTIFY_CLIENT_ID).setClientSecret(Config.SPOTIFY_CLIENT_SECRET).build();
		this.clientCredentialsRequest = this.spotify.clientCredentials().build();
		this.modules.scheduleAtFixedRate(this::refreshAccessToken, 0, 1, TimeUnit.HOURS);
		this.hits = 0;
	}

	private void refreshAccessToken(){
		try{
			this.spotify.setAccessToken(this.clientCredentialsRequest.execute().getAccessToken());
			this.hits = 0;
		}
		catch(Exception e){
			this.hits++;
			if(this.hits < 10){
				LOG.warn("Updating the access token failed. Retrying in 10 seconds", e);
				this.modules.schedule(this::refreshAccessToken, 10, TimeUnit.SECONDS);
				return;
			}
			LOG.error("Updating the access token failed. Retrying in 20 seconds", e);
			this.modules.schedule(this::refreshAccessToken, 20, TimeUnit.SECONDS);
		}
	}

	public void load(GuildInteraction ia, MusicManager manager, Matcher matcher){
		switch(matcher.group(3)){
			case "album":
				loadAlbum(matcher.group(4), ia, manager);
				break;
			case "track":
				loadTrack(matcher.group(4), ia, manager);
				break;
			case "playlist":
				loadPlaylist(matcher.group(4), ia, manager);
				break;
		}
	}

	private void loadAlbum(String id, GuildInteraction ia, MusicManager manager){
		this.spotify.getAlbumsTracks(id).build().executeAsync().thenAcceptAsync(tracks -> {
			var items = tracks.getItems();
			var toLoad = new ArrayList<String>();
			for(var track : items){
				toLoad.add("ytsearch:" + track.getArtists()[0].getName() + " " + track.getName());
			}
			loadTracks(id, ia, manager, toLoad);
		}).exceptionally(throwable -> {
			ia.editError(throwable.getMessage().contains("invalid id") ? "Album not found" : "There was an error while loading the album").queue();
			return null;
		});
	}

	private void loadTrack(String id, GuildInteraction ia, MusicManager manager){
		this.spotify.getTrack(id).build().executeAsync().thenAcceptAsync(track ->
			this.modules.get(MusicModule.class).play(ia, track.getArtists()[0].getName() + " " + track.getName(), SearchProvider.YOUTUBE)
		).exceptionally(throwable -> {
			ia.editError(throwable.getMessage().contains("invalid id") ? "Track not found" : "There was an error while loading the track").queue();
			return null;
		});
	}

	private void loadPlaylist(String id, GuildInteraction ia, MusicManager manager){
		this.spotify.getPlaylistsItems(id).build().executeAsync().thenAcceptAsync(tracks -> {
			var items = tracks.getItems();
			var toLoad = new ArrayList<String>();
			for(var item : items){
				var track = (Track) item.getTrack();
				toLoad.add("ytsearch:" + track.getArtists()[0].getName() + " " + track.getName());
			}
			loadTracks(id, ia, manager, toLoad);
		}).exceptionally(throwable -> {
			ia.editError(throwable.getMessage().contains("Invalid playlist Id") ? "Playlist not found" : "There was an error while loading the playlist").queue();
			return null;
		});
	}

	private void loadTracks(String id, GuildInteraction ia, MusicManager manager, List<String> toLoad){
		var restClient = manager.getScheduler().getLink().getRestClient();
		Utils.all(toLoad.stream().map(restClient::getYoutubeSearchResult).collect(Collectors.toList()))
			.thenAcceptAsync(results -> {
				var tracks = results.stream().map(result -> {
					if(result.isEmpty()){
						return null;
					}
					var track = result.get(0);
					track.setUserData(ia.getUserId());
					return track;
				}).filter(Objects::nonNull).collect(Collectors.toList());

				if(tracks.isEmpty()){
					ia.followupError("No tracks on youtube found");
					return;
				}
				manager.connectToChannel(ia);
				manager.getScheduler().queue(ia, tracks.remove(0), tracks);
			})
			.exceptionally(error -> {
				ia.editError("Something went wrong while fetching your tracks: \n" + error.getMessage()).queue();
				return null;
			});
	}

}
