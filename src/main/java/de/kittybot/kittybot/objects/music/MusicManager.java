package de.kittybot.kittybot.objects.music;

import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.kittybot.kittybot.modules.LavalinkModule;
import de.kittybot.kittybot.modules.MusicModule;
import de.kittybot.kittybot.objects.enums.Emoji;
import de.kittybot.kittybot.objects.module.Modules;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.utils.MessageUtils;
import de.kittybot.kittybot.utils.MusicUtils;
import de.kittybot.kittybot.utils.TimeUtils;
import lavalink.client.io.Link;
import lavalink.client.io.jda.JdaLink;
import lavalink.client.player.event.PlayerEventListenerAdapter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.awt.Color;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MusicManager extends PlayerEventListenerAdapter{

	private final Modules modules;
	private final TrackScheduler scheduler;
	private ScheduledFuture<?> future;

	public MusicManager(Modules modules, long guildId, long channelId){
		this.modules = modules;
		var link = modules.get(LavalinkModule.class).getLink(guildId);
		this.scheduler = new TrackScheduler(this, modules, link, guildId, channelId);
		link.getPlayer().addListener(this.scheduler);
		this.future = null;
	}

	public void planDestroy(){
		this.scheduler.setPaused(true);
		if(this.future != null){
			return;
		}
		this.future = this.modules.schedule(() -> this.modules.get(MusicModule.class).destroy(this, -1L), 4, TimeUnit.MINUTES);
	}

	public void cancelDestroy(){
		this.scheduler.setPaused(false);
		if(this.future == null){
			return;
		}
		this.future.cancel(true);
		this.future = null;
	}

	public void connectToChannel(CommandContext ctx){
		var voiceState = ctx.getMember().getVoiceState();
		if(voiceState != null && voiceState.getChannel() != null && this.scheduler.getLink().getChannelId() != voiceState.getChannel().getIdLong()){
			((JdaLink) this.scheduler.getLink()).connect(voiceState.getChannel());
		}
	}

	public EmbedBuilder buildMusicController(){
		var embed = new EmbedBuilder();
		var track = this.scheduler.getPlayingTrack();
		if(this.scheduler.getLink().getState() == Link.State.DESTROYED){
			embed.setColor(Color.RED)
				.addField("Disconnected", "", false)
				.addField("Author", "-", true)
				.addField("Length", "-", true)
				.addField("Requested by", "-", true);
		}
		else if(track == null){
			embed.setColor(Color.RED)
				.addField("Waiting", "Nothing to play", false)
				.addField("Author", "-", true)
				.addField("Length", "-", true)
				.addField("Requested by", "-", true);
		}
		else{
			var info = track.getInfo();
			if(this.scheduler.isPaused()){
				embed.setColor(Color.ORANGE)
					.addField("Pausing", Emoji.FORWARD.get() + " " + MusicUtils.formatTrack(track), false);
			}
			else{
				embed.setColor(Color.GREEN)
					.addField("Playing", Emoji.FORWARD.get() + " " + MusicUtils.formatTrack(track), false);
			}
			embed.setThumbnail(getThumbnail(track.getIdentifier(), track.getSourceManager()))
				.addField("Author", info.author, true)
				.addField("Length", TimeUtils.formatDuration(track.getDuration()), true)
				.addField("Requested by", MessageUtils.getUserMention(track.getUserData(Long.class)), true);
		}
		embed
			.addField("Volume", (int) (this.scheduler.getFilters().getVolume() * 100) + "%", true)
			.addField("Repeat Mode", this.scheduler.getRepeatMode().getName(), true)
			.setTimestamp(Instant.now());
		return embed;
	}

	public void sendMusicController(){
		if(this.scheduler.getLastMessageId() == this.scheduler.getControllerMessageId()){
			updateMusicController();
			return;
		}
		var channel = this.scheduler.getTextChannel();
		if(channel == null || !channel.canTalk()){
			return;
		}
		channel.deleteMessageById(this.scheduler.getControllerMessageId()).queue();
		var embed = buildMusicController();
		if(!channel.canTalk()){
			return;
		}
		channel.sendMessage(embed.build()).queue(message -> {
			this.scheduler.setControllerId(message.getIdLong());
			if(!channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_ADD_REACTION)){
				return;
			}
			message.addReaction(Emoji.VOLUME_DOWN.getStripped()).queue();
			message.addReaction(Emoji.VOLUME_UP.getStripped()).queue();
			message.addReaction(Emoji.ARROW_LEFT.getStripped()).queue();
			message.addReaction(Emoji.PLAY_PAUSE.getStripped()).queue();
			message.addReaction(Emoji.ARROW_RIGHT.getStripped()).queue();
			message.addReaction(Emoji.SHUFFLE.getStripped()).queue();
			message.addReaction(Emoji.X.getStripped()).queue();
		});
	}

	public void updateMusicController(){
		var channel = this.scheduler.getTextChannel();
		if(channel == null){
			return;
		}
		if(!channel.getGuild().getSelfMember().hasPermission(channel, Permission.MESSAGE_HISTORY)){
			return;
		}
		channel.editMessageById(this.scheduler.getControllerMessageId(), new MessageBuilder().setEmbed(buildMusicController().build()).build()).override(true).queue();
	}

	public String getThumbnail(String identifier, AudioSourceManager source){
		if(source == null){
			return null;
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
				thumbnail = null;
				break;
		}
		return thumbnail;
	}

	public DataObject toJSON(){
		return DataObject.empty()
			.put("guild_id", Long.toString(this.scheduler.getGuildId()))
			.put("channel_id", Long.toString(this.scheduler.getChannelId()))
			.put("queue", tracksToJSON(this.scheduler.getQueue()))
			.put("history", tracksToJSON(this.scheduler.getHistory()));
	}

	private DataArray tracksToJSON(List<AudioTrack> tracks){
		return DataArray.fromCollection(tracks.stream().map(this::trackToJSON).collect(Collectors.toList()));
	}

	private DataObject trackToJSON(AudioTrack track){
		var info = track.getInfo();
		return DataObject.empty()
			.put("identifier", info.identifier)
			.put("uri", info.uri)
			.put("title", info.title)
			.put("author", info.author)
			.put("length", info.length)
			.put("source_name", track.getSourceManager().getSourceName())
			.put("is_stream", info.isStream);
	}

	public TrackScheduler getScheduler(){
		return this.scheduler;
	}

}
