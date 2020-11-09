package de.kittybot.kittybot.objects;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import de.kittybot.kittybot.KittyBot;
import de.kittybot.kittybot.cache.GuildSettingsCache;
import de.kittybot.kittybot.cache.MusicPlayerCache;
import de.kittybot.kittybot.cache.ReactiveMessageCache;
import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.CommandContext;
import de.kittybot.kittybot.utils.MusicUtils;
import lavalink.client.player.IPlayer;
import lavalink.client.player.LavalinkPlayer;
import lavalink.client.player.event.PlayerEventListenerAdapter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.Queue;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static de.kittybot.kittybot.objects.command.ACommand.sendError;
import static de.kittybot.kittybot.utils.Utils.formatDuration;
import static de.kittybot.kittybot.utils.Utils.pluralize;

public class MusicPlayer extends PlayerEventListenerAdapter{

	public static final Pattern URL_PATTERN = Pattern.compile("^(https?://)?((www|m)\\.)?youtu(\\.be|be\\.com)/(playlist\\?list=([a-zA-Z0-9-_]+))?((watch\\?v=)?([a-zA-Z0-9-_]{11})(&list=([a-zA-Z0-9-_]+))?)?");
	private static final Logger LOG = LoggerFactory.getLogger(MusicPlayer.class);
	private final LavalinkPlayer player;
	private final Queue<AudioTrack> queue;
	private final Deque<AudioTrack> history;
	private String messageId;
	private String channelId;
	private ScheduledFuture<?> future;
	private ACommand command;
	private CommandContext ctx;

	public MusicPlayer(LavalinkPlayer player){
		this.player = player;
		this.queue = new LinkedList<>();
		this.history = new LinkedList<>();
	}

	public Queue<AudioTrack> getQueue(){
		return queue;
	}

	public Deque<AudioTrack> getHistory(){
		return history;
	}

	public void loadItem(ACommand command, CommandContext ctx){
		this.command = command;
		this.ctx = ctx;
		var argStr = String.join(" ", ctx.getArgs());
		final var query = URL_PATTERN.matcher(argStr).matches() ? argStr : "ytsearch:" + argStr;
		KittyBot.getAudioPlayerManager().loadItem(query, new AudioLoadResultHandler(){

			@Override
			public void trackLoaded(AudioTrack track){
				track.setUserData(ctx.getUser().getId());
				queue(track);
				if(!queue.isEmpty()){
					sendQueuedTracks(command, ctx, Collections.singletonList(track));
				}
				if(future != null){
					future.cancel(true);
				}
				connectToChannel(ctx);
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
					for(var track : playlist.getTracks()){
						track.setUserData(ctx.getUser().getId());
						queuedTracks.add(track);
						queue(track);
					}
				}
				if(!queue.isEmpty()){
					sendQueuedTracks(command, ctx, queuedTracks);
				}
				if(future != null){
					future.cancel(true);
				}
				connectToChannel(ctx);
			}

			@Override
			public void noMatches(){
				sendError(ctx, "No track found for: " + argStr);
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

	private void sendQueuedTracks(ACommand command, CommandContext ctx, List<AudioTrack> tracks){
		var message = new StringBuilder("Queued **").append(tracks.size()).append("** ").append(pluralize("track", tracks)).append(".");
		message.append("\n\nTo see the current queue, type `").append(GuildSettingsCache.getCommandPrefix(ctx.getGuild().getId())).append("queue`.");
		ACommand.sendSuccess(ctx, message.toString());
	}

	public void connectToChannel(CommandContext ctx){
		var voiceState = ctx.getMember().getVoiceState();
		if(voiceState != null && voiceState.getChannel() != null){
			KittyBot.getLavalink().getLink(ctx.getGuild()).connect(voiceState.getChannel());
		}
	}

	public boolean pause(){
		var paused = !player.isPaused();
		player.setPaused(paused);
		return paused;
	}

	public void changeVolume(int volumeStep){
		var oldVolume = player.getVolume();
		var newVolume = MusicUtils.parseVolume(volumeStep, oldVolume);
		if(newVolume == oldVolume){
			return;
		}
		player.setVolume(newVolume);
	}

	public boolean shuffle(){
		System.out.println("Shuffle: " + queue);
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
	public void onTrackStart(IPlayer player, AudioTrack track){
		if(messageId != null){
			ReactiveMessageCache.removeReactiveMessage(ctx.getGuild(), messageId);
		}
		sendMusicController(command, ctx);
	}

	public void sendMusicController(ACommand command, CommandContext ctx){
		var msg = ctx.getMessage();
		msg.getChannel()
				.sendMessage(buildMusicControlMessage()
						.setTimestamp(Instant.now())
						.build())
				.queue(message -> {
					messageId = message.getId();
					channelId = message.getChannel().getId();
					ReactiveMessageCache.addReactiveMessage(ctx, message, command, "-1");
					message.addReaction(Emojis.VOLUME_DOWN).queue();
					message.addReaction(Emojis.VOLUME_UP).queue();
					message.addReaction(Emojis.BACK).queue();
					message.addReaction("PlayPause:744945002416963634").queue();
					message.addReaction(Emojis.FORWARD).queue();
					message.addReaction(Emojis.SHUFFLE).queue();
					message.addReaction(Emojis.X).queue();
				});
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
					.setThumbnail("https://i.ytimg.com/vi/" + info.identifier + "/hqdefault.jpg")
					.addField("Author", info.author, true)
					.addField("Length", duration, true)
					.addField("Volume", player.getVolume() + "%", true);
			if(player.isPaused()){
				embed.setAuthor("Paused at " + formatDuration(player.getTrackPosition()) + "/" + duration);
				embed.setColor(Color.ORANGE);
			}
			else{
				embed.setAuthor("Playing...");
				embed.setColor(Color.GREEN);
			}

			var member = ctx.getGuild().getMemberById(getRequesterId());
			var user = ctx.getJDA().getUserById(getRequesterId());
			embed.setFooter("Requested by " + (member == null ? user.getName() : member.getEffectiveName()), user.getEffectiveAvatarUrl());
		}
		return embed;
	}

	public String getRequesterId(){
		var playing = player.getPlayingTrack();
		return playing == null ? null : playing.getUserData(String.class);
	}

	@Override
	public void onTrackEnd(IPlayer player, AudioTrack track, AudioTrackEndReason endReason){
		this.history.push(track);
		var guild = KittyBot.getJda().getGuildById(this.player.getLink().getGuildId());
		if(guild == null){
			return;
		}
		if((endReason.mayStartNext && !nextTrack()) || (queue.isEmpty() && player.getPlayingTrack() == null)){
			future = KittyBot.getScheduler().schedule(() -> MusicPlayerCache.destroyMusicPlayer(guild), 2, TimeUnit.MINUTES);
		}
	}

	public boolean nextTrack(){
		var track = queue.poll();
		var channel = KittyBot.getJda().getTextChannelById(channelId);
		if(track != null){
			player.playTrack(track);
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
		channel.editMessageById(messageId, buildMusicControlMessage().setTimestamp(Instant.now()).build()).queue();
	}

	@Override
	public void onTrackException(IPlayer player, AudioTrack track, Exception e){
		LOG.error("Track exception", e);
	}

	@Override
	public void onTrackStuck(IPlayer player, AudioTrack track, long thresholdMs){
		LOG.error("Track is stuck in guild {}", this.player.getLink().getGuildId());
	}

	public LavalinkPlayer getPlayer(){
		return player;
	}

	public boolean previousTrack(){
		var track = history.poll();
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
