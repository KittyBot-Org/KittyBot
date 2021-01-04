package de.kittybot.kittybot.objects;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.kittybot.kittybot.command.CommandContext;
import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.MessageUtils;
import de.kittybot.kittybot.utils.MusicUtils;
import de.kittybot.kittybot.utils.TimeUtils;
import lavalink.client.io.jda.JdaLink;
import lavalink.client.player.IPlayer;
import lavalink.client.player.LavalinkPlayer;
import lavalink.client.player.event.PlayerEventListenerAdapter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.RestAction;

import java.awt.*;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

public class MusicPlayer extends PlayerEventListenerAdapter{

	public static final Pattern URL_PATTERN = Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]?");

	private final KittyBot main;
	private final JdaLink link;
	private final LavalinkPlayer player;
	private final Queue<AudioTrack> queue;
	private final Deque<AudioTrack> history;
	private final long guildId;
	private final long channelId;
	private long controllerMessageId;

	public MusicPlayer(KittyBot main, JdaLink link, long guildId, long channelId){
		this.main = main;
		this.link = link;
		this.player = link.getPlayer();
		this.player.addListener(this);
		this.guildId = guildId;
		this.channelId = channelId;
		this.queue = new LinkedList<>();
		this.history = new LinkedList<>();
		this.controllerMessageId = -1;
	}

	public void loadItem(CommandContext ctx){
		var raw = ctx.getRawMessage();
		var query = URL_PATTERN.matcher(raw).matches() ? raw : "ytsearch:" + raw;
		this.link.getRestClient().loadItem(query, new AudioLoadResultHandler(){

			@Override
			public void trackLoaded(AudioTrack track){
				connectToChannel(ctx);
				track.setUserData(ctx.getUser().getIdLong());
				queue(Collections.singletonList(track));
			}

			@Override
			public void playlistLoaded(AudioPlaylist playlist){
				connectToChannel(ctx);
				if(playlist.isSearchResult()){
					var track = playlist.getSelectedTrack();
					if(track != null){
						track.setUserData(ctx.getUser().getIdLong());
						queue(Collections.singletonList(track));
					}
					return;
				}
				for(var track : playlist.getTracks()){
					track.setUserData(ctx.getUser().getIdLong());
				}
				queue(playlist.getTracks());
			}

			@Override
			public void noMatches(){
				ctx.sendError("No track found for:\n" + raw);
			}

			@Override
			public void loadFailed(FriendlyException e){
				ctx.sendError("Failed to load track:\n" + e.getMessage());
			}
		});
	}

	public void connectToChannel(CommandContext ctx){
		var voiceState = ctx.getMember().getVoiceState();
		if(voiceState != null && voiceState.getChannel() != null && this.link.getChannelId() != voiceState.getChannel().getIdLong()){
			this.link.connect(voiceState.getChannel());
		}
	}


	@Override
	public void onPlayerPause(IPlayer player){
		updateMusicController();
	}

	@Override
	public void onPlayerResume(IPlayer player){
		updateMusicController();
	}

	@Override
	public void onTrackStart(IPlayer player, AudioTrack track){
		sendMusicController();
	}

	public void destroy(){
		this.link.destroy();
	}

	public void sendMusicController(){
		messageToChannel(buildMusicController()).queue(message -> controllerMessageId = message.getIdLong());
	}

	public EmbedBuilder buildMusicController(){
		var track = this.player.getPlayingTrack();
		var embed = new EmbedBuilder();
		if(track == null){
			embed.addField("Now playing", "Queue is empty", true)
					.setColor(Color.RED);
		}
		else{
			var info = track.getInfo();
			if(this.player.isPaused()){
				embed.addField("Pausing", Emoji.FORWARD.getAsMention() + " " + MusicUtils.formatTrack(track), false);
			}
			else{
				embed.addField("Playing", Emoji.FORWARD.getAsMention() + " " + MusicUtils.formatTrack(track), false);
			}
			embed.setColor(Colors.KITTYBOT_BLUE)
					.setThumbnail(getThumbnail(track.getIdentifier(), track.getSourceManager()))

					.addField("Author", info.author, true)
					.addField("Length", TimeUtils.formatDuration(track.getDuration()), true)
					.addField("Volume", (int)(this.player.getFilters().getVolume() * 100) + "%", true)
					.addField("Requested by", MessageUtils.getUserMention(track.getUserData(Long.class)), true);
		}
		return embed;
	}

	public void updateMusicController(){
		if(this.controllerMessageId == -1){
			return;
		}
		var guild = this.main.getJDA().getGuildById(this.guildId);
		if(guild == null){
			return;
		}
		var channel = guild.getTextChannelById(this.channelId);
		if(channel == null){
			return;
		}
		channel.editMessageById(this.controllerMessageId, new MessageBuilder().setEmbed(buildMusicController().build()).build()).override(true).queue();
	}

	public String getThumbnail(String identifier, AudioSourceManager source){
		if(source == null){
			return "";
		}
		var sourceName = source.getSourceName();
		if(sourceName.equals("youtube")){
			return "https://i.ytimg.com/vi/" + identifier + "/hqdefault.jpg";
		}
		return "";
	}

	public void queue(List<AudioTrack> tracks){
		for(var track : tracks){
			queue.offer(track);
		}
		if(player.getPlayingTrack() == null){
			player.playTrack(this.queue.poll());
		}
		sendMessageToChannel(new EmbedBuilder()
				.setColor(Colors.KITTYBOT_BLUE)
				.setDescription(MusicUtils.formatTracks("Queued " + tracks.size() + " " + MessageUtils.pluralize("track", tracks) + ":\n", tracks))
		);
	}

	public RestAction<Message> messageToChannel(EmbedBuilder embed){
		var guild = this.main.getJDA().getGuildById(this.guildId);
		if(guild == null){
			return null;
		}
		var channel = guild.getTextChannelById(this.channelId);
		if(channel == null){
			return null;
		}
		return channel.sendMessage(
				embed.setTimestamp(Instant.now()).build()
		);
	}

	public void sendMessageToChannel(EmbedBuilder embed){
		messageToChannel(embed).queue();
	}

	public void sendMessageToChannel(EmbedBuilder embed, long commandId){
		messageToChannel(embed).queue(message -> this.main.getCommandResponseManager().add(commandId, message.getIdLong()));
	}

	public void pause(){
		player.setPaused(!player.isPaused());
	}

	public void setPaused(boolean paused){
		player.setPaused(paused);
	}

	public void setVolume(int volume){
		player.getFilters().setVolume(volume).commit();
	}

	public long getRequesterId(AudioTrack track){
		return track.getUserData(Long.class);
	}

	public JdaLink getLink(){
		return this.link;
	}

	public Queue<AudioTrack> getQueue(){
		return queue;
	}

	public Deque<AudioTrack> getHistory(){
		return history;
	}

}
