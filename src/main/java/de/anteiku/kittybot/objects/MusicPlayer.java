package de.anteiku.kittybot.objects;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.objects.command.ACommand;
import de.anteiku.kittybot.objects.command.CommandContext;
import de.anteiku.kittybot.utils.Utils;
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
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static de.anteiku.kittybot.objects.command.ACommand.sendError;
import static de.anteiku.kittybot.utils.Utils.formatDuration;
import static de.anteiku.kittybot.utils.Utils.pluralize;

public class MusicPlayer extends PlayerEventListenerAdapter{

	public static final String URL_PATTERN = "^(https?://)?(www|m.)?(\\.)?youtu(\\.be|be\\.com)/(watch\\?v=)?([a-zA-Z0-9-_]{11})";
	private static final int VOLUME_MAX = 200;
	private final LavalinkPlayer player;
	private final Queue<AudioTrack> queue;
	private final Deque<AudioTrack> history;
	private String messageId;
	private String channelId;
	private ScheduledFuture<?> future;

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
		KittyBot.getAudioPlayerManager().loadItem(search, new AudioLoadResultHandler(){

			@Override
			public void trackLoaded(AudioTrack track){
				track.setUserData(ctx.getUser().getId());
				queue(track);
				if(messageId == null){
					sendMusicController(command, ctx);
				}
				else{
					if(queue.isEmpty()){
						updateMusicControlMessage(ctx.getChannel());
					}
					sendQueuedTracks(command, ctx, Collections.singletonList(track));
				}
				if(future != null){
					future.cancel(true);
				}
				KittyBot.getLavalink().getLink(ctx.getGuild()).connect(ctx.getMember().getVoiceState().getChannel());
			}

			@Override
			public void playlistLoaded(AudioPlaylist playlist){
				List<AudioTrack> queuedTracks = new ArrayList<>();
				if(playlist.isSearchResult()){
					var track = playlist.getTracks().get(0);
					track.setUserData(ctx.getUser().getId());
					queuedTracks.add(track);
					queue(track);
				}
				else{
					for(AudioTrack track : playlist.getTracks()){
						track.setUserData(ctx.getUser().getId());
						queuedTracks.add(track);
						queue(track);
					}
				}
				if(messageId == null){
					sendMusicController(command, ctx);
				}
				else{
					if(queue.isEmpty()){
						updateMusicControlMessage(ctx.getChannel());
					}
					sendQueuedTracks(command, ctx, queuedTracks);
				}
				if(future != null){
					future.cancel(true);
				}
				KittyBot.getLavalink().getLink(ctx.getGuild()).connect(ctx.getMember().getVoiceState().getChannel());
			}

			@Override
			public void noMatches(){
				sendError(ctx, "No matches found");
			}

			@Override
			public void loadFailed(FriendlyException exception){
				sendError(ctx, "Failed to load track");
			}
		});
	}

	public void queue(AudioTrack track){
		if(player.getPlayingTrack() == null){
			player.playTrack(track);
		}
		else{
			queue.offer(track);
		}
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
					message.addReaction(Emojis.VOLUME_DOWN).queue();
					message.addReaction(Emojis.VOLUME_UP).queue();
					message.addReaction(Emojis.BACK).queue();
					message.addReaction("PlayPause:744945002416963634").queue();
					message.addReaction(Emojis.FORWARD).queue();
					message.addReaction(Emojis.SHUFFLE).queue();
					message.addReaction(Emojis.X).queue();
				}
		);
	}

	private void sendQueuedTracks(ACommand command, CommandContext ctx, List<AudioTrack> tracks){
		var message = new StringBuilder("Queued ").append(tracks.size()).append(" ").append(pluralize("track", tracks)).append(":\n");
		for(AudioTrack track : tracks){
			message.append(Utils.formatTrackTitle(track)).append(" ").append(formatDuration(track.getDuration())).append("\n");
		}
		command.sendAnswer(ctx, message.toString());
	}

	public String getRequesterId(){
		var playing = player.getPlayingTrack();
		return playing == null ? null : playing.getUserData(String.class);
	}

	public boolean pause(){
		var paused = !player.isPaused();
		player.setPaused(paused);
		return paused;
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

	@Override
	public void onPlayerPause(IPlayer player){

	}

	@Override
	public void onPlayerResume(IPlayer player){

	}

	@Override
	public void onTrackEnd(IPlayer player, AudioTrack track, AudioTrackEndReason endReason){
		this.history.push(track);
		var guild = KittyBot.getJda().getGuildById(getPlayer().getLink().getGuildId());
		if(((endReason.mayStartNext && !nextTrack()) || queue.isEmpty()) && guild != null){
			future = KittyBot.getScheduler().schedule(() -> Cache.destroyMusicPlayer(guild), 2, TimeUnit.MINUTES);
		}
	}

	public LavalinkPlayer getPlayer(){
		return player;
	}

	public boolean nextTrack(){
		AudioTrack track = queue.poll();
		history.push(track);
		var channel = KittyBot.getJda().getTextChannelById(channelId);
		if(track != null){
			player.playTrack(track);
			updateMusicControlMessage(channel);
			return true;
		}
		player.stopTrack();
		updateMusicControlMessage(channel);
		return false;
	}

	public void updateMusicControlMessage(TextChannel channel){
		if(channel == null){
			return;
		}
		channel.editMessageById(messageId, buildMusicControlMessage()
				.setTimestamp(Instant.now())
				.build()
		).queue();
	}

	public EmbedBuilder buildMusicControlMessage(){
		var embed = new EmbedBuilder();
		var track = player.getPlayingTrack();

		if(track == null){
			embed.setAuthor("Nothing to play...")
					.setColor(Color.RED)
					.addField("Author", "", true)
					.addField("Length", "", true)
					.addField("Volume", player.getVolume() + "%", true);
		}
		else{
			var info = track.getInfo();
			var duration = formatDuration(info.length);
			embed.setTitle(info.title, info.uri)
					.setThumbnail("https://i.ytimg.com/vi/" + info.identifier + "/maxresdefault.jpg")
					.addField("Author", info.author, true)
					.addField("Length", duration, true)
					.addField("Volume", player.getVolume() + "%", true);
			if(player.isPaused()){
				embed.setAuthor("Paused at " + formatDuration(getPlayer().getTrackPosition()) + "/" + duration);
				embed.setColor(Color.ORANGE);
			}
			else{
				embed.setAuthor("Playing...");
				embed.setColor(Color.GREEN);
			}
		}
		return embed;
	}

	@Override
	public void onTrackException(IPlayer player, AudioTrack track, Exception exception){
		System.out.println(exception.getMessage()); // TODO fix :)
	}

	@Override
	public void onTrackStuck(IPlayer player, AudioTrack track, long thresholdMs){
		System.out.println("onTrackStuck");
	}

	public boolean previousTrack(){
		AudioTrack track = history.poll();
		if(track != null){
			track.setPosition(0);
			player.playTrack(track);
			return true;
		}
		player.stopTrack();
		return false;
	}

	public String getMessageId(){
		return messageId;
	}

}
