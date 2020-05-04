package de.anteiku.kittybot.trackmanger;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import de.anteiku.kittybot.commands.commands.PlayCommand;
import de.anteiku.kittybot.trackmanger.trackevents.*;
import lavalink.client.player.LavalinkPlayer;
import lavalink.client.player.event.IPlayerEventListener;
import lavalink.client.player.event.PlayerEvent;
import lavalink.client.player.event.TrackStartEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This class schedules tracks for the audio player. It contains the queue of tracks.
 * thx to https://github.com/DV8FromTheWorld/Yui <3
 */
public class TrackScheduler extends AudioEventAdapter implements IPlayerEventListener{
	
	private boolean repeating = false;
	public final LavalinkPlayer player;
	public final Queue<AudioTrack> queue;
	public final Deque<AudioTrack> history;
	public AudioTrack lastTrack;
	public AudioTrack currentTrack;
	
	public TrackScheduler(LavalinkPlayer player){
		this.player = player;
		this.queue = new LinkedList<>();
		this.history = new LinkedList<>();
	}
	
	
	public void queue(AudioTrack track){
		if(player.getPlayingTrack() == null){
			player.playTrack(track);
		}
		else{
			queue.offer(track);
		}
	}
	
	
	public boolean nextTrack(){
		AudioTrack track = queue.poll();
		history.push(currentTrack);
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
	
	@Override
	public void onPlayerPause(AudioPlayer player) {
		System.out.println("onPlayerPause");
	}
	
	@Override
	public void onPlayerResume(AudioPlayer player) {
		System.out.println("onPlayerResume");
	}
	
	@Override
	public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
		this.history.push(currentTrack);
		System.out.println("trigger");
		this.lastTrack = track;
		if(endReason.mayStartNext){
			if(repeating){
				player.startTrack(lastTrack.makeClone(), false);
			}
			else{
				nextTrack();
			}
		}
	}
	
	@Override
	public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
		System.out.println("onTrackException");
	}
	
	@Override
	public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
		System.out.println("onTrackStuck");
	}
	
	public boolean isRepeating(){
		return repeating;
	}
	
	public void setRepeating(boolean repeating){
		this.repeating = repeating;
	}
	
	public boolean shuffle(){
		System.out.println("Shuffle: " + queue.toString());
		if(queue.size() > 1){
			Collections.shuffle((List<?>) queue);
			System.out.println("Shuffle: " + queue.toString());
			return true;
		}
		return false;
	}
	
	@Override
	public void onEvent(PlayerEvent event){
		if(event instanceof TrackStartEvent){
			this.currentTrack = ((TrackStartEvent)event).getTrack();
		}
		else if(event instanceof TrackShuffleEvent){
			if(shuffle()){
				sendAnswer(((TrackShuffleEvent)event).getMessage(), "All queued tracks shuffled", false);
			}
			else{
				sendError(((TrackShuffleEvent)event).getMessage(), "You need more tracks to shuffle");
			}
		}
		else if(event instanceof TrackForwardEvent){
			if(!nextTrack()){
				sendError(((TrackForwardEvent)event).getMessage(), "No tracks in queue");
			}
			((TrackForwardEvent)event).getMessage().editMessage(PlayCommand.buildMusicControlMessage(player).build()).queue();
		}
		else if(event instanceof TrackBackEvent){
			if(!lastTrack()){
				sendError(((TrackBackEvent)event).getMessage(), "Track history is empty");
			}
			((TrackBackEvent)event).getMessage().editMessage(PlayCommand.buildMusicControlMessage(player).build()).queue();
		}
		else if(event instanceof TrackQueueEvent){
			queue(((TrackQueueEvent)event).getTrack());
			AudioTrackInfo info = ((TrackQueueEvent)event).getTrack().getInfo();
			sendAnswer(((TrackQueueEvent)event).getMessage(), "Successfully queued [" + info.title + "](" + info.uri + ")", true);
		}
		else if(event instanceof PlaylistQueueEvent){
			AudioPlaylist playlist = ((PlaylistQueueEvent)event).getPlaylist();
			List<AudioTrack> tracks = playlist.getTracks();
			for(AudioTrack track : tracks){
				queue(track);
			}
			sendAnswer(((PlaylistQueueEvent)event).getMessage(), "Successfully queued playlist [" + playlist.getName() +"](" + playlist.getSelectedTrack().getInfo().uri + ") with " + tracks.size() + " tracks", true);
		}
	}
	
	private void sendError(Message command, String error){
		command.getChannel().sendMessage(new EmbedBuilder()
			.setColor(Color.RED)
			.addField("Error:", error, true)
			.setFooter(command.getMember().getEffectiveName(), command.getAuthor().getEffectiveAvatarUrl())
			.setTimestamp(Instant.now())
			.build()
		).queue(
			message -> message.delete().queueAfter(10, TimeUnit.SECONDS)
		);
	}
	
	private void sendAnswer(Message command, String answer, boolean delete){
		command.getChannel().sendMessage(new EmbedBuilder()
			.setColor(Color.GREEN)
			.setDescription(answer)
			.setFooter(command.getMember().getEffectiveName(), command.getAuthor().getEffectiveAvatarUrl())
			.setTimestamp(Instant.now())
			.build()
		).queue(
				message -> {
					if(delete) command.delete().queueAfter(5, TimeUnit.SECONDS);
				}
		);
	}
	
}
