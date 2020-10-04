package de.kittybot.kittybot.objects.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEvent;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import de.kittybot.kittybot.KittyBot;
import de.kittybot.kittybot.cache.ReactiveMessageCache;
import de.kittybot.kittybot.objects.Emojis;
import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.CommandContext;
import de.kittybot.kittybot.utils.AudioUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.time.Instant;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import static de.kittybot.kittybot.objects.command.ACommand.sendError;
import static de.kittybot.kittybot.utils.Utils.formatDuration;

public class GuildMusicManager extends AudioEventAdapter {
	private final AudioPlayer player;
	private final BlockingQueue<AudioTrack> queue;
	private final Deque<AudioTrack> history;

	private String controllerMessageId;
	private CommandContext ctx;
	private ACommand command;
	private String channelId;

	public GuildMusicManager(final AudioPlayerManager manager){
		this.player = manager.createPlayer();
		this.queue = new LinkedBlockingQueue<>();
		this.history = new LinkedBlockingDeque<>();
		player.addListener(this);
	}

	public void loadQuery(final ACommand command, final CommandContext ctx){
		this.command = command;
		this.ctx = ctx;
		if (channelId == null){
			this.channelId = ctx.getChannel().getId();
		}
		var query = String.join(" ", ctx.getArgs());
		query = AudioUtils.YOUTUBE_URL_PATTERN.matcher(query).matches() ? query : "ytsearch:" + query; // TODO maybe make this more flexible

		KittyBot.getAudioPlayerManager().loadItem(query, new AudioLoadResultHandler(){

			@Override
			public void trackLoaded(final AudioTrack track){
				if(!AudioUtils.isValidLength(track)){
					sendError(ctx, "The track can't be longer than 20 minutes");
					return;
				}
				track.setUserData(ctx.getUser().getId());
				queue(track);
				connectToChannel(ctx);
			}

			@Override
			public void playlistLoaded(final AudioPlaylist playlist){

			}

			@Override
			public void noMatches(){

			}

			@Override
			public void loadFailed(final FriendlyException exception){

			}
		});
	}

	// events

	@Override
	public void onEvent(final AudioEvent event){
		updateMusicControlMessage(ctx.getJDA().getTextChannelById(channelId));
	}

	@Override
	public void onTrackStart(final AudioPlayer player, final AudioTrack track){
		if(controllerMessageId != null){
			ReactiveMessageCache.removeReactiveMessage(ctx.getGuild(), controllerMessageId);
		}
		sendMusicController(command, ctx);
	}

	@Override
	public void onTrackEnd(final AudioPlayer player, final AudioTrack track, final AudioTrackEndReason endReason){
		history.push(track);
		if (endReason.mayStartNext){
			nextTrack();
		}
	}

	// methods

	public void queue(final AudioTrack track){
		if (!player.startTrack(track, true)){
			queue.offer(track);
		}
	}

	public boolean previousTrack(){
		final var track = history.poll();
		if(track != null){
			track.setPosition(0);
			player.playTrack(track);
			return true;
		}
		player.stopTrack();
		return false;
	}

	public boolean nextTrack(){
		return player.startTrack(queue.poll(), false);
	}

	public void pause(){
		player.setPaused(!player.isPaused());
	}

	public void shuffle(){
		if(queue.size() > 1){
			Collections.shuffle((List<?>) queue);
		}
	}

	public void changeVolume(final int volumeStep){
		var volume = player.getVolume();
		if(volume > 0){
			if(volume + volumeStep < 200){
				volume += volumeStep;
			}
			else{
				volume = 200;
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
	}

	private void connectToChannel(final CommandContext ctx){
		final var audioManager = ctx.getGuild().getAudioManager();
		if(audioManager.isConnected()){
			return;
		}
		// just to be sure
		AudioUtils.checkVoiceChannel(ctx).thenAcceptAsync(connectFailureReason -> {
			if(connectFailureReason != null){
				sendError(ctx, "I can't play music as " + connectFailureReason.getReason() + ".");
				return;
			}
			//noinspection ConstantConditions it's unlikely to fail after checking
			audioManager.openAudioConnection(ctx.getMember().getVoiceState().getChannel());
		});
	}

	public void sendMusicController(ACommand command, CommandContext ctx){
		var msg = ctx.getMessage();
		msg.getChannel()
				.sendMessage(buildMusicControlMessage()
						.setTimestamp(Instant.now())
						.build())
				.queue(message -> {
					controllerMessageId = message.getId();
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

	private EmbedBuilder buildMusicControlMessage(){
		var embed = new EmbedBuilder();
		var track = player.getPlayingTrack();

		if(track == null){
			embed.setAuthor("Nothing to play")
					.setColor(Color.RED)
					.addField("Author", "", true)
					.addField("Length", "", true)
					.addField("Volume", player.getVolume() + "%", true);
		}
		else{
			var info = track.getInfo();
			var duration = formatDuration(info.length);
			var paused = player.isPaused();
			embed.setTitle(info.title, info.uri)
					.setThumbnail("https://i.ytimg.com/vi/" + info.identifier + "/hqdefault.jpg")
					.addField("Author", info.author, true)
					.addField("Length", duration, true)
					.addField("Volume", player.getVolume() + "%", true);
			embed.setAuthor(player.isPaused() ? "Paused at " + formatDuration(track.getPosition()) + "/" + duration : "Playing");
			embed.setColor(paused ? Color.ORANGE : Color.GREEN);

			var member = ctx.getGuild().getMemberById(getRequesterId());
			var user = ctx.getJDA().getUserById(getRequesterId());
			//noinspection ConstantConditions shut
			embed.setFooter("Requested by " + member.getEffectiveName(), user.getEffectiveAvatarUrl());
		}
		return embed;
	}

	public void updateMusicControlMessage(TextChannel channel){
		if(channel == null || controllerMessageId == null){
			return;
		}
		channel.editMessageById(controllerMessageId, buildMusicControlMessage().setTimestamp(Instant.now()).build()).queue();
	}

	public String getRequesterId(){
		final var playingTrack = player.getPlayingTrack();
		return playingTrack == null ? null : playingTrack.getUserData(String.class);
	}

	public String getControllerMessageId(){
		return controllerMessageId;
	}

	public String getChannelId(){
		return channelId;
	}

	public AudioPlayerSendHandler getSendHandler(){
		return new AudioPlayerSendHandler(player);
	}

	public Queue<AudioTrack> getQueue(){
		return queue;
	}

	public Deque<AudioTrack> getHistory(){
		return history;
	}

	public AudioPlayer getPlayer(){
		return player;
	}
}
