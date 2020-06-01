package de.anteiku.kittybot.trackmanger.trackevents;

import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import lavalink.client.player.IPlayer;
import lavalink.client.player.event.PlayerEvent;
import net.dv8tion.jda.api.entities.Message;

public class PlaylistQueueEvent extends PlayerEvent{

	private final AudioPlaylist playlist;
	private final Message message;

	public PlaylistQueueEvent(IPlayer player, AudioPlaylist playlist, Message message){
		super(player);
		this.playlist = playlist;
		this.message = message;
	}

	public AudioPlaylist getPlaylist(){
		return playlist;
	}

	public Message getMessage(){
		return message;
	}

}
