package de.kittybot.kittybot.objects.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import de.kittybot.kittybot.KittyBot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class GuildMusicManager extends AudioEventAdapter{
	// audio player that controls the playback
	private final AudioPlayer audioPlayer;

	// queue, history
	private final Queue<AudioTrack> queue;
	private final Deque<AudioTrack> history;

	// controller channel and message id
	private String controllerChannelId;
	private String controllerMessageId;

	public GuildMusicManager(){
		this.audioPlayer = KittyBot.getAudioPlayerManager().createPlayer();

		this.queue = new LinkedBlockingQueue<>();
		this.history = new LinkedBlockingDeque<>();

		audioPlayer.addListener(this);
	}

	// events

	@Override
	public void onTrackEnd(final AudioPlayer player, final AudioTrack track, final AudioTrackEndReason endReason){
		history.push(track);
		if(endReason.mayStartNext){
			nextTrack();
		}
	}

	// playback stuff

	public void nextTrack(){
		final var nextTrack = queue.poll();
		if(nextTrack == null){
			audioPlayer.stopTrack();
			return;
		}
		audioPlayer.startTrack(nextTrack, false);
	}

	public void previousTrack(){
		final var previousTrack = history.poll();
		if(previousTrack == null){
			audioPlayer.stopTrack();
			return;
		}
		audioPlayer.startTrack(previousTrack, false);
	}

	public void shuffle(){
		final var copy = new ArrayList<>(queue);
		Collections.shuffle(copy);
		queue.clear();
		queue.addAll(copy);
	}

	// queue and history getters

	public Queue<AudioTrack> getQueue(){
		return this.queue;
	}

	public Deque<AudioTrack> getHistory(){
		return this.history;
	}

	// controller getters

	public String getControllerChannelId(){
		return this.controllerChannelId;
	}

	public String getControllerMessageId(){
		return this.controllerMessageId;
	}

	// other methods

	public String getRequesterId(){
		final var playing = audioPlayer.getPlayingTrack();
		return playing == null ? null : playing.getUserData(String.class);
	}

	public void setControllerChannelId(final String channelId){
		this.controllerChannelId = channelId;
	}

	public AudioPlayer getAudioPlayer(){
		return this.audioPlayer;
	}

	public AudioPlayerSendHandler getSendHandler(){
		return new AudioPlayerSendHandler(this.audioPlayer);
	}
}