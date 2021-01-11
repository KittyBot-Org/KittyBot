package de.kittybot.kittybot.objects;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import de.kittybot.kittybot.command.CommandContext;
import de.kittybot.kittybot.module.Modules;
import de.kittybot.kittybot.modules.CommandResponseModule;
import de.kittybot.kittybot.modules.MusicModule;
import de.kittybot.kittybot.modules.PaginatorModule;
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
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.RestAction;

import java.awt.Color;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class MusicPlayer extends PlayerEventListenerAdapter{

	public static final Pattern URL_PATTERN = Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]?");

	private final Modules modules;
	private final JdaLink link;
	private final LavalinkPlayer player;
	private final Queue<AudioTrack> queue;
	private final Deque<AudioTrack> history;
	private final long guildId;
	private final long channelId;
	private long controllerMessageId;
	private ScheduledFuture<?> future;

	public MusicPlayer(Modules modules, JdaLink link, long guildId, long channelId){
		this.modules = modules;
		this.link = link;
		this.player = link.getPlayer();
		this.player.addListener(this);
		this.guildId = guildId;
		this.channelId = channelId;
		this.queue = new LinkedList<>();
		this.history = new LinkedList<>();
		this.controllerMessageId = -1;
		this.future = null;
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

	public void queue(List<AudioTrack> tracks){
		for(var track : tracks){
			queue.offer(track);
		}
		if(player.getPlayingTrack() == null){
			player.playTrack(this.queue.poll());
		}

		var channel = getTextChannel();
		if(channel == null){
			return;
		}
		this.modules.get(PaginatorModule.class).create(
				channel,
				1,
				(page, embedBuilder) -> {
					return
				}
		);

		sendMessageToChannel(new EmbedBuilder()
				.setColor(Colors.KITTYBOT_BLUE)
				.setDescription(MusicUtils.formatTracks("Queued " + tracks.size() + " " + MessageUtils.pluralize("track", tracks) + ":\n", tracks))
		);
	}

	public void sendMessageToChannel(EmbedBuilder embed){
		messageToChannel(embed).queue();
	}

	public RestAction<Message> messageToChannel(EmbedBuilder embed){
		var guild = this.modules.getJDA(this.guildId).getGuildById(this.guildId);
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

	public TextChannel getTextChannel(){
		var guild = this.modules.getJDA(this.guildId).getGuildById(this.guildId);
		if(guild == null){
			return null;
		}
		return guild.getTextChannelById(this.channelId);
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

	@Override
	public void onTrackEnd(IPlayer player, AudioTrack track, AudioTrackEndReason endReason){
		this.history.push(track);
		if(!endReason.mayStartNext){
			updateMusicController();
			return;
		}
		next();
	}

	/*public void planDestroy(long currentChannel){
		player.setPaused(true);
		this.main.getEventWaiter().waitForEvent(
			GuildVoiceJoinEvent.class,
			event -> event.getChannelJoined().getIdLong() == currentChannel && !event.getEntity().getUser().isBot(),
			event -> this.player.setPaused(false),
			3,
			TimeUnit.MINUTES,
			this.main.get(MusicModule.class).destroy(this.guildId)
		);
	}*/

	public void sendMusicController(){
		messageToChannel(buildMusicController()).queue(message -> controllerMessageId = message.getIdLong());
	}

	public void updateMusicController(){
		if(this.controllerMessageId == -1){
			return;
		}
		var guild = this.modules.getJDA(this.guildId).getGuildById(this.guildId);
		if(guild == null){
			return;
		}
		var channel = guild.getTextChannelById(this.channelId);
		if(channel == null){
			return;
		}
		channel.editMessageById(this.controllerMessageId, new MessageBuilder().setEmbed(buildMusicController().build()).build()).override(true).queue();
	}

	public EmbedBuilder buildMusicController(){
		var embed = new EmbedBuilder();
		var track = this.player.getPlayingTrack();
		if(track == null){
			embed.setColor(Color.RED)
					.addField("Waiting", "The queue is empty", true)
					.addField("Author", "", true)
					.addField("Length", "", true)
					.addField("Requested by", "", true);
		}
		else{
			var info = track.getInfo();
			if(this.player.isPaused()){
				embed.setColor(Color.ORANGE)
						.addField("Pausing", Emoji.FORWARD.getAsMention() + " " + MusicUtils.formatTrack(track), false);
			}
			else{
				embed.setColor(Color.GREEN)
						.addField("Playing", Emoji.FORWARD.getAsMention() + " " + MusicUtils.formatTrack(track), false);
			}
			embed.setThumbnail(getThumbnail(track.getIdentifier(), track.getSourceManager()))
					.addField("Author", info.author, true)
					.addField("Length", TimeUtils.formatDuration(track.getDuration()), true)
					.addField("Requested by", MessageUtils.getUserMention(getRequesterId(track)), true);
		}
		return embed.addField("Volume", (int) (this.player.getFilters().getVolume() * 100) + "%", true);
	}

	public String getThumbnail(String identifier, AudioSourceManager source){
		if(source == null){
			return "";
		}
		var sourceName = source.getSourceName();
		String thumbnail;
		switch(sourceName){
			case "youtube":
				thumbnail = "https://i.ytimg.com/vi/" + identifier + "/hqdefault.jpg";
				break;
			case "twitch":
				thumbnail = "https://static-cdn.jtvnw.net/previews-ttv/live_user_" + identifier + "-440x248.jpg";
				break;
			default:
				thumbnail = "";
				break;
		}
		return thumbnail;
	}

	public long getRequesterId(AudioTrack track){
		return track.getUserData(Long.class);
	}

	public void next(){
		var next = this.queue.poll();
		if(next == null){
			updateMusicController();
			planDestroy();
			return;
		}
		this.player.playTrack(next);
	}

	public void planDestroy(){
		if(this.future != null){
			return;
		}
		this.future = this.modules.getScheduler().schedule(() -> {
			this.modules.get(MusicModule.class).destroy(this.guildId);
		}, 3, TimeUnit.MINUTES);
	}

	public void cancelDestroy(){
		if(this.future == null){
			return;
		}
		this.future.cancel(true);
		this.future = null;
	}

	public void sendMessageToChannel(EmbedBuilder embed, long commandId){
		messageToChannel(embed).queue(message -> this.modules.get(CommandResponseModule.class).add(commandId, message.getIdLong()));
	}

	public void pause(){
		player.setPaused(!player.isPaused());
	}

	public boolean shuffle(){
		if(queue.isEmpty()){
			return false;
		}
		Collections.shuffle((List<?>) this.queue);
		return true;
	}

	public void setPaused(boolean paused){
		player.setPaused(paused);
	}

	public void setVolume(int volume){
		player.getFilters().setVolume(volume).commit();
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
