package de.kittybot.kittybot.objects.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.kittybot.kittybot.KittyBot;

import java.util.Deque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class MusicPlayer extends AudioEventAdapter
{
	// audio player that controls the playback
	private final AudioPlayer audioPlayer;

	// queue, history
	private final BlockingQueue<AudioTrack> queue;
	private final Deque<AudioTrack> history;

	public MusicPlayer()
	{
		this.audioPlayer = KittyBot.getAudioPlayerManager().createPlayer();

		this.queue = new LinkedBlockingQueue<>();
		this.history = new LinkedBlockingDeque<>();

		audioPlayer.addListener(this);
	}



	// queue and history getters

	public BlockingQueue<AudioTrack> getQueue()
	{
		return this.queue;
	}

	public Deque<AudioTrack> getHistory()
	{
		return this.history;
	}

	// other methods

	public AudioPlayer getAudioPlayer()
	{
		return this.audioPlayer;
	}

	public AudioPlayerSendHandler getSendHandler()
	{
		return new AudioPlayerSendHandler(this.audioPlayer);
	}
}