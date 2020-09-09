package de.anteiku.kittybot.objects;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.objects.cache.GuildSettingsCache;
import de.anteiku.kittybot.objects.cache.MusicPlayerCache;
import de.anteiku.kittybot.objects.cache.ReactiveMessageCache;
import de.anteiku.kittybot.objects.command.ACommand;
import de.anteiku.kittybot.objects.command.CommandContext;
import de.anteiku.kittybot.objects.spotify.SpotifyLoader;
import lavalink.client.player.IPlayer;
import lavalink.client.player.LavalinkPlayer;
import lavalink.client.player.event.PlayerEventListenerAdapter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.Queue;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static de.anteiku.kittybot.objects.command.ACommand.sendAnswer;
import static de.anteiku.kittybot.objects.command.ACommand.sendError;
import static de.anteiku.kittybot.utils.Utils.*;

public class MusicPlayer extends PlayerEventListenerAdapter{

	public static final Pattern YOUTUBE_URL_PATTERN = Pattern.compile("^(https?://)?((www|m)\\.)?youtu(\\.be|be\\.com)/(playlist\\?list=([a-zA-Z0-9-_]+))?((watch\\?v=)?([a-zA-Z0-9-_]{11})(&list=([a-zA-Z0-9-_]+))?)?");
	public static final Pattern SPOTIFY_URL_PATTERN = Pattern.compile("^(https?://)?(www\\.)?open\\.spotify\\.com/(track|album|playlist)/([a-zA-Z0-9-_]+)(\\?si=[a-zA-Z0-9-_]+)?");
	private static final Logger LOG = LoggerFactory.getLogger(MusicPlayer.class);
	private static final int VOLUME_MAX = 200;
	private final LavalinkPlayer player;
	private final Queue<AudioTrack> queue;
	private final Deque<AudioTrack> history;
	private String messageId;
	private String channelId;
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
		String argStr = String.join(" ", ctx.getArgs());
		var matcher = SPOTIFY_URL_PATTERN.matcher(argStr);
		if(matcher.matches()){
			SpotifyLoader.load(ctx, matcher);
			return;
		}
		final String query = YOUTUBE_URL_PATTERN.matcher(argStr).matches() ? argStr : "ytsearch:" + argStr;
		KittyBot.getAudioPlayerManager().loadItem(query, new AudioLoadResultHandler(){

			@Override
			public void trackLoaded(AudioTrack track){
				MusicPlayer.this.trackLoaded(track);
			}

			@Override
			public void playlistLoaded(AudioPlaylist playlist){
				MusicPlayer.this.playlistLoaded(playlist);
			}

			@Override
			public void noMatches(){
				sendError(ctx, "No track found for: " + argStr);
			}

			@Override
			public void loadFailed(FriendlyException exception){
				MusicPlayer.this.loadFailed(exception);
			}
		});
	}

	public void trackLoaded(AudioTrack track){
		if(!lengthCheck(track.getDuration())){
			sendError(ctx, "The maximum length of a track is 20 minutes");
			return;
		}
		track.setUserData(ctx.getUser().getId());
		queue(track);
		if(!queue.isEmpty()){
			sendQueuedTracks(ctx, Collections.singletonList(track));
		}
		connectToChannel(ctx);
	}

	public void playlistLoaded(AudioPlaylist playlist){
		var queuedTracks = new ArrayList<AudioTrack>();
		if(playlist.isSearchResult()){
			var track = playlist.getTracks().get(0);
			if(!lengthCheck(track.getDuration())){
				sendError(ctx, "The maximum length of a track is 20 minutes");
				return;
			}
			track.setUserData(ctx.getUser().getId());
			queuedTracks.add(track);
			queue(track);
		}
		else{
			for(AudioTrack track : playlist.getTracks()){
				if(!lengthCheck(track.getDuration())){
					if(playlist.getTracks().size() == 1){
						sendError(ctx, "The maximum length of a track is 20 minutes");
						return;
					}
					else{
						continue;
					}
				}
				track.setUserData(ctx.getUser().getId());
				queuedTracks.add(track);
				queue(track);
			}
		}
		if(!queue.isEmpty()){
			sendQueuedTracks(ctx, queuedTracks);
		}
		connectToChannel(ctx);
	}

	public void loadFailed(FriendlyException exception){
		sendError(ctx, exception.getMessage().contains("Track information is unavailable")
				? "Playing **age restricted videos doesn't work** as YouTube changed the response. We're waiting for a fix from the audio library we're using."
				: "Failed to load track");
	}

	public boolean lengthCheck(long duration){
		return TimeUnit.MILLISECONDS.toMinutes(duration) <= 20;
	}

	public void queue(AudioTrack track){
		if(player.getPlayingTrack() == null){
			player.playTrack(track);
		}
		else{
			queue.offer(track);
		}
	}

	public void sendQueuedTracks(CommandContext ctx, List<AudioTrack> tracks){
		var message = new StringBuilder("Queued **").append(tracks.size()).append("** ").append(pluralize("track", tracks));
		if(tracks.size() == 1){
			var track = tracks.get(0);
			message.append(":\n").append(formatTrackTitle(track)).append(" ").append(formatDuration(track.getDuration()));
		}
		message.append("\n\nTo see the current queue, type `").append(GuildSettingsCache.getCommandPrefix(ctx.getGuild().getId())).append("queue`.");
		sendAnswer(ctx, message.toString());
	}

	public void connectToChannel(CommandContext ctx){
		var voiceState = ctx.getMember().getVoiceState();
		if(voiceState != null && voiceState.getChannel() != null){
			KittyBot.getLavalink().getLink(ctx.getGuild()).connect(voiceState.getChannel());
		}
	}

	public boolean canInteract(AudioTrack track, User user){
		return user.getId().equals(track.getUserData(String.class)) || isDJ(ctx.getGuild().getMember(user));
	}

	public boolean isDJ(Member member){
		return member.getRoles().stream().anyMatch(role -> role.getId().equals(GuildSettingsCache.getDJRoleId(member.getGuild().getId())));
	}

	public boolean pause(){
		var paused = !player.isPaused();
		player.setPaused(paused);
		updateMusicControlMessage();
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
		updateMusicControlMessage();
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
				embed.setAuthor("Paused at " + formatDuration(getPlayer().getTrackPosition()) + "/" + duration);
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

	public LavalinkPlayer getPlayer(){
		return player;
	}

	public String getRequesterId(){
		var playing = player.getPlayingTrack();
		return playing == null ? null : playing.getUserData(String.class);
	}

	@Override
	public void onTrackEnd(IPlayer player, AudioTrack track, AudioTrackEndReason endReason){
		this.history.push(track);
		var guild = KittyBot.getJda().getGuildById(getPlayer().getLink().getGuildId());
		if(guild == null){
			return;
		}
		if(endReason.mayStartNext && !nextTrack()){
			KittyBot.getScheduler().schedule(() -> {
				if(player.getPlayingTrack() == null){
					MusicPlayerCache.destroyMusicPlayer(guild);
				}
			}, 2, TimeUnit.MINUTES);
		}
	}

	public boolean nextTrack(){
		AudioTrack track = queue.poll();
		player.setPaused(false);
		if(track != null){
			player.playTrack(track);
			updateMusicControlMessage();
			return true;
		}
		player.stopTrack();
		updateMusicControlMessage();
		return false;
	}

	public void updateMusicControlMessage(){
		var channel = KittyBot.getJda().getTextChannelById(channelId);
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

	public boolean previousTrack(){
		AudioTrack track = history.poll();
		if(track != null){
			track.setPosition(0);
			player.playTrack(track);
			updateMusicControlMessage();
			return true;
		}
		player.stopTrack();
		updateMusicControlMessage();
		return false;
	}

	public String getMessageId(){
		return messageId;
	}

}
