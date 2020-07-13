package de.anteiku.kittybot.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import de.anteiku.kittybot.utils.Emotes;
import lavalink.client.player.IPlayer;
import lavalink.client.player.LavalinkPlayer;
import lavalink.client.player.event.PlayerEventListenerAdapter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Queue;
import java.util.*;

/**
 * This class schedules tracks for the audio player. It contains the queue of tracks.
 * thx to https://github.com/DV8FromTheWorld/Yui <3
 */
public class MusicPlayer extends PlayerEventListenerAdapter {

	private static final int VOLUME_MAX = 200;
	// ^(http(s)??\:\/\/)?(www|m\.)?((youtube\.com\/watch\?v=)|(youtu.be\/))([a-zA-Z0-9\-_])+
	public static final String URL_PATTERN = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";


	public final LavalinkPlayer player;
	private final Queue<AudioTrack> queue;
	private final Deque<AudioTrack> history;
	private boolean repeating = false;
	private String messageId;

	public MusicPlayer(LavalinkPlayer player){
		this.player = player;
		this.queue = new LinkedList<>();
		this.history = new LinkedList<>();
	}
	public void loadItem(ACommand command, GuildMessageReceivedEvent event, String... args){
		String search = String.join(" ", args);
		if(!search.matches(URL_PATTERN)){
			search = "ytsearch:" + search;
		}
		command.main.audioPlayerManager.loadItem(search, new AudioLoadResultHandler(){

			@Override
			public void trackLoaded(AudioTrack track){
				track.setUserData(event.getAuthor().getId());
				queue(track);
				if(messageId == null){
					sendMusicController(command, event);
				}
			}

			@Override
			public void playlistLoaded(AudioPlaylist playlist){
				for(AudioTrack track : playlist.getTracks()){
					track.setUserData(event.getAuthor().getId());
					queue(track);
				}
				if(messageId == null){
					sendMusicController(command, event);
				}
			}

			@Override
			public void noMatches(){
				command.sendError(event, "No matches found for ");
			}

			@Override
			public void loadFailed(FriendlyException exception){
				command.sendError(event, "Failed to load track");
			}
		});
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

	public LavalinkPlayer getPlayer(){
		return player;
	}

	@Override
	public void onPlayerPause(IPlayer player){
		System.out.println("onPlayerPause");
	}

	@Override
	public void onPlayerResume(IPlayer player){
		System.out.println("onPlayerResume");
	}

	@Override
	public void onTrackEnd(IPlayer player, AudioTrack track, AudioTrackEndReason endReason){
		System.out.println("onTrackEnd");
		this.history.push(track);
		if(endReason.mayStartNext){
			if(repeating){
				player.playTrack(track.makeClone());
			}
			else{
				nextTrack();
			}
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

	public void sendMusicController(ACommand command, GuildMessageReceivedEvent event){
		var msg = event.getMessage();
		msg.getChannel().sendMessage(buildMusicControlMessage()
			.setFooter(msg.getMember().getEffectiveName(), msg.getAuthor().getEffectiveAvatarUrl())
			.setTimestamp(Instant.now())
			.build()
		).queue(
			message -> {
				messageId = message.getId();
				command.main.commandManager.addReactiveMessage(event, message, command, "-1");
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

	public void updateMusicControlMessage(TextChannel channel, Member member){
		channel.editMessageById(messageId, buildMusicControlMessage()
			.setFooter(member.getEffectiveName(), member.getUser().getEffectiveAvatarUrl())
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
			Duration duration = Duration.ofMillis(info.length);
			var seconds = duration.toSecondsPart();
			embed.setColor(Color.GREEN)
				.setTitle(info.title, info.uri)
				.setThumbnail("https://i.ytimg.com/vi/" + info.identifier + "/maxresdefault.jpg")
				.addField("Author", info.author, true)
				.addField("Length", duration.toMinutes() + ":" + (seconds > 9 ? seconds : "0" + seconds), true)
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
