package de.kittybot.kittybot.objects.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.objects.module.Modules;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.utils.Colors;
import de.kittybot.kittybot.utils.MessageUtils;
import de.kittybot.kittybot.utils.MusicUtils;
import lavalink.client.io.Link;
import lavalink.client.io.filters.Filters;
import lavalink.client.player.IPlayer;
import lavalink.client.player.LavalinkPlayer;
import lavalink.client.player.event.PlayerEventListenerAdapter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TrackScheduler extends PlayerEventListenerAdapter{

	private final MusicManager manager;
	private final Modules modules;
	private final Link link;
	private final LavalinkPlayer player;
	private final LinkedList<AudioTrack> queue;
	private final LinkedList<AudioTrack> history;
	private final long guildId;
	private final long channelId;
	private long controllerMessageId;
	private long lastMessageId;
	private RepeatMode repeatMode;

	public TrackScheduler(MusicManager manager, Modules modules, Link link, long guildId, long channelId){
		this.manager = manager;
		this.modules = modules;
		this.link = link;
		this.player = link.getPlayer();
		this.guildId = guildId;
		this.channelId = channelId;
		this.queue = new LinkedList<>();
		this.history = new LinkedList<>();
		this.controllerMessageId = -1;
		this.lastMessageId = -2;
		this.repeatMode = RepeatMode.OFF;
	}

	@Override
	public void onPlayerPause(IPlayer player){
		this.manager.updateMusicController();
	}

	@Override
	public void onPlayerResume(IPlayer player){
		this.manager.updateMusicController();
	}

	@Override
	public void onTrackStart(IPlayer player, AudioTrack track){
		this.manager.sendMusicController();
		this.manager.cancelDestroy();
	}

	@Override
	public void onTrackEnd(IPlayer player, AudioTrack track, AudioTrackEndReason endReason){
		this.history.push(track.makeClone());
		if(!endReason.mayStartNext){
			this.manager.updateMusicController();
			return;
		}
		next(false, track);
	}

	public void next(boolean force, AudioTrack track){
		if(this.repeatMode == RepeatMode.SONG && !force){
			if(track != null){
				this.player.playTrack(track.makeClone());
			}
			return;
		}
		var next = this.queue.poll();
		if(next == null){
			this.player.stopTrack();
			this.manager.planDestroy();
			return;
		}
		this.player.playTrack(next);
		if(this.repeatMode == RepeatMode.QUEUE && track != null){
			this.queue.offer(track.makeClone());
		}
	}

	public void queue(GuildInteraction ia, AudioTrack toPlay, List<AudioTrack> tracks){
		var embed = queue(toPlay, tracks);
		var action = ia.acknowledge(true);
		if(embed != null){
			action.embeds(embed);
		}
		action.queue(success -> tryPlay(toPlay), error -> tryPlay(toPlay));
	}

	public MessageEmbed queue(AudioTrack toPlay, List<AudioTrack> tracks){
		var wasEmpty = this.queue.isEmpty();
		var shouldPlay = this.player.getPlayingTrack() == null;
		if(!shouldPlay){
			this.queue.offer(toPlay);
		}
		for(var track : tracks){
			this.queue.offer(track);
		}
		if(!wasEmpty || this.queue.size() > 0){
			return new EmbedBuilder()
				.setColor(Colors.KITTYBOT_BLUE)
				.setDescription("**Queued " + tracks.size() + " " + MessageUtils.pluralize("track", tracks.size()) + "**\n\n" +
					(tracks.size() == 0 ? MusicUtils.formatTrackWithInfo(toPlay) : "") +
					"\nUse `/queue` to view the queue"
				)
				.setTimestamp(Instant.now())
				.build();
		}
		return null;
	}

	public void tryPlay(AudioTrack toPlay){
		if(this.player.getPlayingTrack() == null){
			this.player.playTrack(toPlay);
			this.player.setPaused(false);
		}
	}

	public void pause(){
		player.setPaused(!player.isPaused());
	}

	public int removeQueue(int from, int to, Member member){
		var settings = this.modules.get(SettingsModule.class);
		var userId = member.getIdLong();
		var iterator = this.queue.iterator();
		var i = 1;
		var removed = 0;
		while(iterator.hasNext()){
			var track = iterator.next();
			if(i >= from && i <= to && (track.getUserData(Long.class) == userId || settings.hasDJRole(member))){
				iterator.remove();
				removed++;
			}
			i++;
		}
		return removed;
	}

	public TextChannel getTextChannel(){
		var guild = this.modules.getJDA(this.guildId).getGuildById(this.guildId);
		if(guild == null){
			return null;
		}
		return guild.getTextChannelById(this.channelId);
	}

	public void next(boolean force){
		next(force, this.player.getPlayingTrack());
	}

	public void previous(){
		var previous = this.history.pollLast();
		if(previous == null){
			return;
		}
		this.player.playTrack(previous);
	}

	public boolean shuffle(){
		if(queue.isEmpty()){
			return false;
		}
		Collections.shuffle(this.queue);
		return true;
	}

	public void increaseVolume(int volumeStep){
		var newVol = (int) (this.player.getFilters().getVolume() * 100) + volumeStep;
		if(newVol <= 0){
			newVol = 10;
		}
		if(newVol > 150){
			newVol = 150;
		}
		setVolume(newVol);
		this.manager.updateMusicController();
	}

	public void setVolume(int volume){
		this.player.getFilters().setVolume((float) volume / 100.0f).commit();
		this.manager.updateMusicController();
	}

	public long getControllerMessageId(){
		return this.controllerMessageId;
	}

	public LinkedList<AudioTrack> getQueue(){
		return this.queue;
	}

	public LinkedList<AudioTrack> getHistory(){
		return this.history;
	}

	public AudioTrack getPlayingTrack(){
		return this.player.getPlayingTrack();
	}

	public LavalinkPlayer getPlayer(){
		return this.player;
	}

	public Link getLink(){
		return this.link;
	}

	public long getGuildId(){
		return this.guildId;
	}

	public long getChannelId(){
		return this.channelId;
	}

	public boolean isPaused(){
		return this.player.isPaused();
	}

	public void setPaused(boolean paused){
		player.setPaused(paused);
	}

	public Filters getFilters(){
		return this.player.getFilters();
	}

	public long getLastMessageId(){
		return this.lastMessageId;
	}

	public void setLastMessageId(long lastMessageId){
		this.lastMessageId = lastMessageId;
	}

	public void setControllerId(long messageId){
		this.controllerMessageId = messageId;
	}

	public RepeatMode getRepeatMode(){
		return this.repeatMode;
	}

	public void setRepeatMode(RepeatMode repeatMode){
		this.repeatMode = repeatMode;
		this.manager.updateMusicController();
	}

}
