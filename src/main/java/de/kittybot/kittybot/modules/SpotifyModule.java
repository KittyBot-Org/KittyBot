package de.kittybot.kittybot.modules;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.model_objects.specification.Track;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import de.kittybot.kittybot.objects.module.Module;
import de.kittybot.kittybot.objects.music.MusicManager;
import de.kittybot.kittybot.objects.music.SearchProvider;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
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
	protected void onEnable(){
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

	public void load(GuildCommandContext ctx, MusicManager manager, Matcher matcher){
		switch(matcher.group(3)){
			case "album":
				loadAlbum(matcher.group(4), ctx, manager);
				break;
			case "track":
				loadTrack(matcher.group(4), ctx, manager);
				break;
			case "playlist":
				loadPlaylist(matcher.group(4), ctx, manager);
				break;
		}
	}

	private void loadAlbum(String id, GuildCommandContext ctx, MusicManager manager){
		this.spotify.getAlbumsTracks(id).build().executeAsync().thenAcceptAsync(tracks -> {
			var items = tracks.getItems();
			var toLoad = new ArrayList<String>();
			for(var track : items){
				toLoad.add("ytsearch:" + track.getArtists()[0].getName() + " " + track.getName());
			}
			loadTracks(id, ctx, manager, toLoad);
		}).exceptionally(throwable -> {
			ctx.error(throwable.getMessage().contains("invalid id") ? "Album not found" : "There was an error while loading the album");
			return null;
		});
	}

	private void loadTrack(String id, GuildCommandContext ctx, MusicManager manager){
		this.spotify.getTrack(id).build().executeAsync().thenAcceptAsync(track ->
			this.modules.get(MusicModule.class).play(ctx, track.getArtists()[0].getName() + " " + track.getName(), SearchProvider.YOUTUBE)
		).exceptionally(throwable -> {
			ctx.error(throwable.getMessage().contains("invalid id") ? "Track not found" : "There was an error while loading the track");
			return null;
		});
	}

	private void loadPlaylist(String id, GuildCommandContext ctx, MusicManager manager){
		this.spotify.getPlaylistsItems(id).build().executeAsync().thenAcceptAsync(tracks -> {
			var items = tracks.getItems();
			var toLoad = new ArrayList<String>();
			for(var item : items){
				var track = (Track) item.getTrack();
				toLoad.add("ytsearch:" + track.getArtists()[0].getName() + " " + track.getName());
			}
			loadTracks(id, ctx, manager, toLoad);
		}).exceptionally(throwable -> {
			ctx.error(throwable.getMessage().contains("Invalid playlist Id") ? "Playlist not found" : "There was an error while loading the playlist");
			return null;
		});
	}

	private void loadTracks(String id, GuildCommandContext ctx, MusicManager manager, List<String> toLoad){
		ctx.reply("Loading tracks...\nThis may take a while");
		var restClient = manager.getScheduler().getLink().getRestClient();
		Utils.all(toLoad.stream().map(restClient::getYoutubeSearchResult).collect(Collectors.toList()))
			.thenAcceptAsync(results -> {
				var tracks = results.stream().map(result -> {
					if(result.isEmpty()){
						return null;
					}
					var track = result.get(0);
					track.setUserData(ctx.getUserId());
					return track;
				}).filter(Objects::nonNull).collect(Collectors.toList());
				if(tracks.isEmpty()){
					ctx.getThread().sendMessage("No tracks on youtube found").queue();
					return;
				}
				manager.connectToChannel(ctx);
				var toPlay = tracks.remove(0);
				var embed = manager.getScheduler().queue(toPlay, tracks);
				if(embed == null){
					return;
				}
				ctx.getThread().editOriginal("").addEmbeds(embed)
					.queue(success -> manager.getScheduler().tryPlay(toPlay), error -> manager.getScheduler().tryPlay(toPlay));
			})
			.exceptionally(error -> {
				ctx.getThread().editOriginal("")
					.addEmbeds(ctx.getEmbed().setColor(Color.RED).setDescription("Something went wrong while fetching your tracks: \n" + error.getMessage()).build()).queue();
				return null;
			});
	}

}
