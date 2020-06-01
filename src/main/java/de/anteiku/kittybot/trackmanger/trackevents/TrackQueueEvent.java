package de.anteiku.kittybot.trackmanger.trackevents;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lavalink.client.player.IPlayer;
import lavalink.client.player.event.PlayerEvent;
import net.dv8tion.jda.api.entities.Message;

public class TrackQueueEvent extends PlayerEvent{

	private final AudioTrack track;
	private final Message message;

	public TrackQueueEvent(IPlayer player, AudioTrack track, Message message){
		super(player);
		this.track = track;
		this.message = message;
	}

	public AudioTrack getTrack(){
		return track;
	}

	public Message getMessage(){
		return message;
	}

}
