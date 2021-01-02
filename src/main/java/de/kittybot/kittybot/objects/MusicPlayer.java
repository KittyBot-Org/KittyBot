package de.kittybot.kittybot.objects;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.kittybot.kittybot.main.KittyBot;
import lavalink.client.io.jda.JdaLink;
import lavalink.client.player.IPlayer;
import lavalink.client.player.LavalinkPlayer;
import lavalink.client.player.event.PlayerEventListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Pattern;

public class MusicPlayer extends PlayerEventListenerAdapter{

	public static final Pattern URL_PATTERN = Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]?");
	private static final Logger LOG = LoggerFactory.getLogger(KittyBot.class);

	private final JdaLink link;
	private final LavalinkPlayer player;
	private final Queue<AudioTrack> queue;
	private final Deque<AudioTrack> history;
	private final long guildId;
	private final long channelId;

	public MusicPlayer(JdaLink link, long guildId, long channelId){
		this.link = link;
		this.player = link.getPlayer();
		this.player.addListener(this);
		this.guildId = guildId;
		this.channelId = channelId;
		this.queue = new LinkedList<>();
		this.history = new LinkedList<>();
	}

	@Override
	public void onPlayerPause(IPlayer player){

	}

	@Override
	public void onPlayerResume(IPlayer player){

	}

	public void destroy(){
		this.link.destroy();
	}

	public void sendController(){

	}

	public void queue(AudioTrack track){
		if(player.getPlayingTrack() == null){
			player.playTrack(track);
			return;
		}
		queue.offer(track);
	}

	public boolean pause(){
		var paused = !player.isPaused();
		player.setPaused(paused);
		return paused;
	}

	public void setVolume(int volume){
		player.setVolume(volume);
	}

	public long getRequesterId(AudioTrack track){
		return track.getUserData(Long.class);
	}

	public JdaLink getLink(){
		return this.link;
	}

	public Queue<AudioTrack> getQueue(){
		return queue;
	}

	public Deque<AudioTrack> getHistory(){
		return history;
	}

}
