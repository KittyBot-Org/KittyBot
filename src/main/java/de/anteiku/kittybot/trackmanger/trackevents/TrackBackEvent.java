package de.anteiku.kittybot.trackmanger.trackevents;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lavalink.client.player.IPlayer;
import lavalink.client.player.event.PlayerEvent;
import net.dv8tion.jda.api.entities.Message;

public class TrackBackEvent extends PlayerEvent{
	
	private Message message;
	
	public TrackBackEvent(IPlayer player, Message message){
		super(player);
		this.message = message;
	}
	
	public Message getMessage(){
		return message;
	}
	
	
}
