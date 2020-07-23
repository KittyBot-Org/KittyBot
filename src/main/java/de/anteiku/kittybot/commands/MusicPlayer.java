package de.anteiku.kittybot.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.objects.Cache;
import de.anteiku.kittybot.objects.Emotes;
import de.anteiku.kittybot.Utils;
import lavalink.client.player.IPlayer;
import lavalink.client.player.LavalinkPlayer;
import lavalink.client.player.event.PlayerEventListenerAdapter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.Queue;
import java.util.*;

public class MusicPlayer extends PlayerEventListenerAdapter {

	private static final int VOLUME_MAX = 200;
	// ^(http(s)??\:\/\/)?(www|m\.)?((youtube\.com\/watch\?v=)|(youtu.be\/))([a-zA-Z0-9\-_])+
	public static final String URL_PATTERN = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";


	private final LavalinkPlayer player;
	private final Queue<AudioTrack> queue;
	private final Deque<AudioTrack> history;
	private String messageId;
	private String channelId;

	public MusicPlayer(LavalinkPlayer player){
		this.player = player;
		this.queue = new LinkedList<>();
		this.history = new LinkedList<>();
	}

	public Queue<AudioTrack> getQueue(){
		return queue;
	}

	public void loadItem(ACommand command, CommandContext ctx, String... args){
		String search = String.join(" ", args);
		if(!search.matches(URL_PATTERN)){
			search = "ytsearch:" + search;
		}
		KittyBot.audioPlayerManager.loadItem(search, new AudioLoadResultHandler(){

			@Override
			public void trackLoaded(AudioTrack track){
				track.setUserData(ctx.getUser().getId());
				queue(track);
				if(messageId == null){
					sendMusicController(command, ctx);
				}
				else{
					sendQueuedTracks(command, ctx, Collections.singletonList(track));
				}
			}

			@Override
			public void playlistLoaded(AudioPlaylist playlist){
				for(AudioTrack track : playlist.getTracks()){
					track.setUserData(ctx.getUser().getId());
					queue(track);
				}
				if(messageId == null){
					sendMusicController(command, ctx);
				}
				else{
					sendQueuedTracks(command, ctx, playlist.getTracks());
				}
			}

			@Override
			public void noMatches(){
				command.sendError(ctx, "No matches found for ");
			}

			@Override
			public void loadFailed(FriendlyException exception){
				command.sendError(ctx, "Failed to load track");
			}
		});
	}

	private void sendQueuedTracks(ACommand command, CommandContext ctx, List<AudioTrack> tracks){
		var message = new StringBuilder("Queued ").append(tracks.size()).append(" tracks:\n");
		for(AudioTrack track : tracks){
			message.append(Utils.formatTrackTitle(track)).append(" ").append(Utils.formatDuration(track.getDuration())).append("\n");
		}
		command.sendAnswer(ctx, message.toString());
	}

	public String getRequesterId(){ // credit to @canelex_ for that name :)
		return player.getPlayingTrack().getUserData(String.class);
	}

	public void queue(AudioTrack track){
		if(player.getPlayingTrack() == null){
			player.playTrack(track);
		}
		else{
			queue.offer(track);
		}
	}

	public boolean pause(){
		var paused = !player.isPaused();
		player.setPaused(paused);
		return paused;
	}

	public boolean nextTrack(){
		AudioTrack track = queue.poll();
		history.push(track);
		if(track != null){
			player.playTrack(track);
			return true;
		}
		player.stopTrack();
		return false;
	}

	public boolean lastTrack(){
		AudioTrack track = history.poll();
		if(track != null){
			player.playTrack(track);
			return true;
		}
		player.stopTrack();
		return false;
	}

	public int changeVolume(int volumeStep){
		var volume = player.getVolume();
		if(volume > 0){
			if(volume + volumeStep < VOLUME_MAX){
				volume += volumeStep;
			}
			else{
				volume = VOLUME_MAX;
			}
		}
		else{
			if(volume - volumeStep > 0){
				volume -= volumeStep;
			}
			else{
				volume = 0;
			}
		}
		player.setVolume(volume);
		return volume;
	}

	public boolean shuffle(){
		System.out.println("Shuffle: " + queue.toString());
		if(queue.size() > 1){
			Collections.shuffle((List<?>) queue);
			return true;
		}
		return false;
	}

	public LavalinkPlayer getPlayer(){
		return player;
	}

	@Override
	public void onPlayerPause(IPlayer player){

	}

	@Override
	public void onPlayerResume(IPlayer player){

	}

	@Override
	public void onTrackEnd(IPlayer player, AudioTrack track, AudioTrackEndReason endReason){
		this.history.push(track);
		if(endReason.mayStartNext){
			nextTrack();
		}
	}

	@Override
	public void onTrackException(IPlayer player, AudioTrack track, Exception exception){
		System.out.println("onTrackException");
	}

	@Override
	public void onTrackStuck(IPlayer player, AudioTrack track, long thresholdMs){
		System.out.println("onTrackStuck");
	}

	public void sendMusicController(ACommand command, CommandContext ctx){
		var msg = ctx.getMessage();
		msg.getChannel().sendMessage(buildMusicControlMessage()
			.setFooter(msg.getMember().getEffectiveName(), msg.getAuthor().getEffectiveAvatarUrl())
			.setTimestamp(Instant.now())
			.build()
		).queue(
			message -> {
				messageId = message.getId();
				channelId = message.getChannel().getId();
				Cache.addReactiveMessage(ctx, message, command, "-1");
				message.addReaction(Emotes.VOLUME_DOWN.get()).queue();
				message.addReaction(Emotes.VOLUME_UP.get()).queue();
				message.addReaction(Emotes.BACK.get()).queue();
				message.addReaction(Emotes.PLAY_PAUSE.get()).queue();
				message.addReaction(Emotes.FORWARD.get()).queue();
				message.addReaction(Emotes.SHUFFLE.get()).queue();
				message.addReaction(Emotes.X.get()).queue();
			}
		);
	}

	public void updateMusicControlMessage(TextChannel channel){
		channel.editMessageById(messageId, buildMusicControlMessage()
			.setTimestamp(Instant.now())
			.build()
		).queue();
	}

	public EmbedBuilder buildMusicControlMessage(){
		var embed = new EmbedBuilder();

		if(player.getPlayingTrack() == null){
			embed.setAuthor("Nothing to play...")
				.setColor(Color.RED)
				.addField("Author", "", true)
				.addField("Length", "", true)
				.addField("Volume", player.getVolume() + "%", true);
		}
		else{
			AudioTrackInfo info = player.getPlayingTrack().getInfo();
			embed.setColor(Color.GREEN)
				.setTitle(info.title, info.uri)
				.setThumbnail("https://i.ytimg.com/vi/" + info.identifier + "/maxresdefault.jpg")
				.addField("Author", info.author, true)
				.addField("Length", Utils.formatDuration(info.length), true)
				.addField("Volume", player.getVolume() + "%", true);
			if(player.isPaused()){
				embed.setAuthor("Paused...");
			}
			else{
				embed.setAuthor("Playing...");
			}
		}
		return embed;
	}

}
