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
import de.kittybot.kittybot.cache.PrefixCache;
import de.kittybot.kittybot.cache.ReactiveMessageCache;
import de.kittybot.kittybot.objects.Config;
import de.kittybot.kittybot.objects.Emojis;
import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.CommandContext;
import de.kittybot.kittybot.utils.AudioUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

import static de.kittybot.kittybot.objects.command.ACommand.sendError;
import static de.kittybot.kittybot.utils.Utils.formatDuration;
import static de.kittybot.kittybot.utils.Utils.formatTrackTitle;

public class MusicPlayer extends AudioEventAdapter {
	private final AudioPlayer player;
	private final BlockingQueue<AudioTrack> queue;
	private final Deque<AudioTrack> history;

	private String controllerMessageId;
	private CommandContext ctx;
	private ACommand command;
	private String channelId;

	public MusicPlayer(final AudioPlayerManager manager){
		this.player = manager.createPlayer();
		this.queue = new LinkedBlockingQueue<>();
		this.history = new LinkedBlockingDeque<>();
		player.addListener(this);
	}

	public void loadQuery(final ACommand command, final CommandContext ctx){
		this.command = command;
		this.ctx = ctx;
		if(channelId == null){
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
		super.onEvent(event);
		updateMusicControlMessage();
	}

	@Override
	public void onTrackStart(final AudioPlayer player, final AudioTrack track){
		if(controllerMessageId != null){
			ReactiveMessageCache.removeReactiveMessage(ctx.getGuild(), controllerMessageId);
		}
	}

	@Override
	public void onTrackEnd(final AudioPlayer player, final AudioTrack track, final AudioTrackEndReason endReason){
		history.push(track);
		if(endReason.mayStartNext){
			nextTrack();
		}
	}

	// methods

	public void queue(final AudioTrack track){
		if (!player.startTrack(track, true)){
			queue.offer(track);
			if(queue.size() == 1){
				updateMusicControlMessage();
			}
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
		var oldVolume = player.getVolume();
		var newVolume = volumeStep < 0 ? Math.max(oldVolume + volumeStep, 0) // a + (-b) = -
									   : Math.min(oldVolume + volumeStep, 200);
		if(newVolume == oldVolume){
			return;
		}
		player.setVolume(newVolume);
		updateMusicControlMessage();
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
				.sendMessage(buildMusicControlMessage())
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

	private MessageEmbed buildMusicControlMessage(){
		var prefix = PrefixCache.getCommandPrefix(ctx.getGuild().getId());
		var embedBuilder = new EmbedBuilder();
		var track = player.getPlayingTrack();
		embedBuilder.setAuthor("KittyBot Music", Config.ORIGIN_URL, ctx.getJDA().getSelfUser().getAvatarUrl());

		if(track == null){
			embedBuilder.setColor(Color.RED);
			embedBuilder.setDescription("There's nothing to play! Use `" + prefix + "play` to queue some songs.");
			return embedBuilder.build();
		}
		var info = track.getInfo();
		var paused = player.isPaused();
		var member = ctx.getGuild().getMemberById(getRequesterId());
		embedBuilder.setThumbnail("https://i.ytimg.com/vi/" + info.identifier + "/maxresdefault.jpg");
		//noinspection ConstantConditions shut :)
		embedBuilder.setFooter("Requested by " + member.getEffectiveName() + " | View the queue with " + prefix + "queue", member.getUser().getEffectiveAvatarUrl());
		embedBuilder.setColor(paused ? Color.ORANGE : Color.GREEN);
		embedBuilder.addField("Now playing" + (paused ? " (paused at " + formatDuration(track.getPosition()) + ")" : ""), (paused ? "\u23F8\uFE0F" : "\u25B6\uFE0F") + " " + formatTrackTitle(track, true), false);

		var next = queue.peek();
		if(next != null){
			embedBuilder.addField("Next up", "\u23ED\uFE0F " + formatTrackTitle(next, true), false);
		}
		embedBuilder.addField("Volume", player.getVolume() + "%", false);
		embedBuilder.addField("Song duration", formatDuration(info.length), true);
		if(next != null){
			var totalDuration = 0;
			for(final var queued : queue){
				totalDuration += queued.getDuration();
			}
			embedBuilder.addField("Total duration", formatDuration(totalDuration), true);
			embedBuilder.addField("Songs remaining", "" + queue.size(), true);
		}
		return embedBuilder.build();
	}

	public void updateMusicControlMessage(){
		if(controllerMessageId == null){
			sendMusicController(command, ctx);
			return;
		}
		getControllerChannel().editMessageById(controllerMessageId, buildMusicControlMessage()).queue();
	}

	public TextChannel getControllerChannel(){
		return ctx.getJDA().getTextChannelById(channelId);
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
