package de.anteiku.kittybot.commands.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import de.anteiku.kittybot.trackmanger.TrackScheduler;
import de.anteiku.kittybot.trackmanger.trackevents.TrackBackEvent;
import de.anteiku.kittybot.trackmanger.trackevents.TrackForwardEvent;
import de.anteiku.kittybot.trackmanger.trackevents.TrackShuffleEvent;
import de.anteiku.kittybot.utils.Emotes;
import de.anteiku.kittybot.utils.ReactiveMessage;
import lavalink.client.io.jda.JdaLink;
import lavalink.client.player.LavalinkPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

import java.awt.*;
import java.time.Duration;

public class PlayCommand extends ACommand{

	private static final int volumeStep = 20;
	private static final int volumeMax = 200;
	public static String COMMAND = "play";
	public static String USAGE = "play <playlist/song/video>";
	public static String DESCRIPTION = "Plays what you want him to play";
	protected static String[] ALIAS = {"p", "spiele"};

	public PlayCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
		this.main = main;
	}

	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		GuildVoiceState voiceState = event.getMember().getVoiceState();
		if(voiceState != null && voiceState.inVoiceChannel()){
			JdaLink link = main.lavalink.getLink(event.getGuild());
			link.connect(voiceState.getChannel());

			LavalinkPlayer player = link.getPlayer();

			TrackScheduler trackScheduler = new TrackScheduler(player);
			player.addListener(trackScheduler);

			main.playerManager.loadItem(String.join(" ", args), new AudioLoadResultHandler(){

				@Override
				public void trackLoaded(AudioTrack track){
					player.playTrack(track);
					sendMusicController(event, player);
				}

				@Override
				public void playlistLoaded(AudioPlaylist playlist){
					for(AudioTrack track : playlist.getTracks()){
						trackScheduler.queue(track);
					}
					sendMusicController(event, player);
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
		else{
			sendError(event, "Please connect to a voice channel to play some stuff");
		}
	}

	public void sendMusicController(GuildMessageReceivedEvent event, LavalinkPlayer player){
		answer(event, buildMusicControlMessage(player)).queue(
				message -> {
					main.commandManager.addReactiveMessage(event, message, this, "-1");
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

	public static EmbedBuilder buildMusicControlMessage(LavalinkPlayer player){
		if(player.getPlayingTrack() == null){
			return new EmbedBuilder()
					.setAuthor("Nothing to play...")
					.setColor(Color.RED)
					.addField("Author", "", true)
					.addField("Length", "", true)
					.addField("Volume", player.getVolume() + "%", true);
		}
		AudioTrackInfo info = player.getPlayingTrack().getInfo();
		Duration duration = Duration.ofMillis(info.length);
		return new EmbedBuilder()
				.setAuthor("Now playing...")
				.setColor(Color.GREEN)
				.setTitle(info.title, info.uri)
				.addField("Author", info.author, true)
				.addField("Length", duration.toMinutes() + ":" + duration.toSecondsPart(), true)
				.addField("Volume", player.getVolume() + "%", true);
	}

	@Override
	public void reactionAdd(ReactiveMessage reactiveMessage, GuildMessageReactionAddEvent event){
		if(event.getReactionEmote().isEmoji()){
			LavalinkPlayer player = main.lavalink.getLink(event.getGuild()).getPlayer();
			String emoji = event.getReactionEmote().getEmoji();
			if(emoji.equals(Emotes.FORWARD.get())){
				event.getChannel().retrieveMessageById(event.getMessageId()).queue(
						message -> player.emitEvent(new TrackForwardEvent(player, message))
				);
			}
			else if(emoji.equals(Emotes.BACK.get())){
				event.getChannel().retrieveMessageById(event.getMessageId()).queue(
						message -> player.emitEvent(new TrackBackEvent(player, message))
				);
			}
			else if(emoji.equals(Emotes.SHUFFLE.get())){
				event.getChannel().retrieveMessageById(event.getMessageId()).queue(
						message -> player.emitEvent(new TrackShuffleEvent(player, message))
				);
			}
			else if(emoji.equals(Emotes.PLAY_PAUSE.get())){
				player.setPaused(!player.isPaused());
			}
			else if(emoji.equals(Emotes.VOLUME_DOWN.get())){
				int volume = player.getVolume();
				if(volume - volumeStep > 0){
					volume -= volumeStep;
				}
				else if(volume > 0){
					volume = 0;
				}
				player.setVolume(volume);
				event.getChannel().editMessageById(event.getMessageId(), PlayCommand.buildMusicControlMessage(player).build()).queue();
			}
			else if(emoji.equals(Emotes.VOLUME_UP.get())){
				int volume = player.getVolume();
				if(volume + volumeStep < volumeMax){
					volume += volumeStep;
				}
				else if(volume < volumeMax){
					volume = volumeMax;
				}
				player.setVolume(volume);
				event.getChannel().editMessageById(event.getMessageId(), PlayCommand.buildMusicControlMessage(player).build()).queue();
			}
			else if(emoji.equals(Emotes.X.get())){
				main.lavalink.getLink(event.getGuild()).destroy();
				main.commandManager.removeReactiveMessage(event.getGuild(), event.getMessageId());
			}
			event.getReaction().removeReaction(event.getUser()).queue();
		}
		super.reactionAdd(reactiveMessage, event);
	}

}
