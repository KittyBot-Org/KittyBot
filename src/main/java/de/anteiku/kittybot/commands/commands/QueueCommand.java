package de.anteiku.kittybot.commands.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import de.anteiku.kittybot.trackmanger.trackevents.PlaylistQueueEvent;
import de.anteiku.kittybot.trackmanger.trackevents.TrackQueueEvent;
import lavalink.client.player.LavalinkPlayer;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class QueueCommand extends ACommand{
	
	public static String COMMAND = "queue";
	public static String USAGE = "queue <playlist/song/video>";
	public static String DESCRIPTION = "Queues what you want him to play later";
	protected static String[] ALIAS = {"q"};
	
	public QueueCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
		this.main = main;
	}
	
	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		LavalinkPlayer player = main.lavalink.getLink(event.getGuild()).getPlayer();
		
		main.playerManager.loadItem(String.join(" ", args), new AudioLoadResultHandler(){
			
			@Override
			public void trackLoaded(AudioTrack track){
				player.emitEvent(new TrackQueueEvent(player, track,  event.getMessage()));
			}
			
			@Override
			public void playlistLoaded(AudioPlaylist playlist){
				player.emitEvent(new PlaylistQueueEvent(player, playlist, event.getMessage()));
			}
			
			@Override
			public void noMatches(){
				sendError(event, "No matches found");
			}
			
			@Override
			public void loadFailed(FriendlyException exception){
				sendError(event, "Failed to load track");
			}
		});
	}
	
}
