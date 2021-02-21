package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.objects.module.Module;
import de.kittybot.kittybot.objects.music.AudioLoader;
import de.kittybot.kittybot.objects.music.MusicManager;
import de.kittybot.kittybot.objects.music.SearchProvider;
import de.kittybot.kittybot.objects.music.TrackScheduler;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.MessageUtils;
import lavalink.client.io.Link;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class MusicModule extends Module implements Serializable{

	public static final Pattern URL_PATTERN = Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]?");
	public static final Pattern SPOTIFY_URL_PATTERN = Pattern.compile("^(https?://)?(www\\.)?open\\.spotify\\.com/(track|album|playlist)/([a-zA-Z0-9-_]+)(\\?si=[a-zA-Z0-9-_]+)?");

	private Map<Long, MusicManager> musicPlayers;

	@Override
	public void onEnable(){
		this.musicPlayers = new HashMap<>();
	}

	@Override
	public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event){
		var manager = this.musicPlayers.get(event.getGuild().getIdLong());
		if(manager != null){
			manager.getScheduler().setLastMessageId(event.getMessageIdLong());
		}
	}

	@Override
	public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event){
		if(event.getUserIdLong() == Config.BOT_ID){
			return;
		}
		var manager = this.musicPlayers.get(event.getGuild().getIdLong());
		if(manager == null){
			return;
		}
		var scheduler = manager.getScheduler();
		var member = event.getMember();

		var voiceState = member.getVoiceState();
		if(voiceState == null || voiceState.getChannel() == null || scheduler.getLink().getChannelId() != voiceState.getChannel().getIdLong()){
			return;
		}
		var messageId = event.getMessageIdLong();
		var currentTrack = scheduler.getPlayingTrack();
		var userId = event.getUserIdLong();
		var requesterId = currentTrack == null ? -1L : currentTrack.getUserData(Long.class);
		var settings = this.modules.get(SettingsModule.class).getSettings(event.getGuild().getIdLong());

		if(messageId != scheduler.getControllerMessageId()){
			return;
		}
		switch(event.getReactionEmote().getAsReactionCode()){
			case "\u2B05\uFE0F":// ⬅
				if(requesterId == userId || member.hasPermission(Permission.ADMINISTRATOR) || settings.hasDJRole(member)){
					scheduler.previous();
					scheduler.setPaused(false);
				}
				break;
			case "\u27A1\uFE0F":// ➡
				if(requesterId == userId || member.hasPermission(Permission.ADMINISTRATOR) || settings.hasDJRole(member)){
					scheduler.next(true);
					scheduler.setPaused(false);
				}
				break;
			case "PlayPause:744945002416963634"://play pause
				if(requesterId == userId || member.hasPermission(Permission.ADMINISTRATOR) || settings.hasDJRole(member)){
					scheduler.pause();
				}
				break;
			case "\uD83D\uDD00":// 🔀
				if(member.hasPermission(Permission.ADMINISTRATOR) || settings.hasDJRole(member)){
					scheduler.shuffle();
				}
				break;
			case "\uD83D\uDD09":// 🔉
				scheduler.increaseVolume(-10);
				break;
			case "\uD83D\uDD0A":// 🔊
				scheduler.increaseVolume(10);
				break;
			case "\u274C":// ❌
				if(requesterId == userId || member.hasPermission(Permission.ADMINISTRATOR) || settings.hasDJRole(member)){
					destroy(event.getGuild().getIdLong(), event.getUserIdLong());
				}
				break;
		}
		if(event.getGuild().getSelfMember().hasPermission(event.getChannel(), Permission.MESSAGE_MANAGE)){
			event.getReaction().removeReaction(event.getUser()).queue();
		}
	}

	@Override
	public void onGuildLeave(@NotNull GuildLeaveEvent event){
		this.musicPlayers.remove(event.getGuild().getIdLong());
	}

	@Override
	public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event){
		if(event instanceof GuildVoiceLeaveEvent || event instanceof GuildVoiceMoveEvent){
			var manager = get(event.getEntity().getGuild().getIdLong());
			if(manager == null){
				return;
			}
			if(event.getEntity().getIdLong() == Config.BOT_ID){
				this.modules.get(MusicModule.class).destroy(manager, -1L);
				return;
			}
			var channel = event.getChannelLeft();
			var currentChannel = manager.getScheduler().getLink().getChannelId();
			if(channel == null || channel.getIdLong() != currentChannel){
				return;
			}
			if(channel.getMembers().stream().anyMatch(member -> !member.getUser().isBot())){
				return;
			}
			manager.planDestroy();
		}
		else if(event instanceof GuildVoiceJoinEvent){
			var player = get(event.getEntity().getGuild().getIdLong());
			if(player == null){
				return;
			}
			player.cancelDestroy();
		}
	}

	public MusicManager get(long guildId){
		return this.musicPlayers.get(guildId);
	}

	public void destroy(long guildId, long userId){
		destroy(this.musicPlayers.get(guildId), userId);
	}

	public void destroy(MusicManager musicManager, long userId){
		var scheduler = musicManager.getScheduler();
		var link = scheduler.getLink();
		if(link != null && link.getState() != Link.State.DESTROYING && link.getState() != Link.State.DESTROYED){
			link.destroy();
		}
		var player = this.musicPlayers.remove(musicManager.getScheduler().getGuildId());
		if(player != null){
			player.updateMusicController();
			var channel = scheduler.getTextChannel();
			if(channel == null || !channel.canTalk()){
				return;
			}
			var message = userId == -1 ? "Disconnected due to inactivity" : MessageUtils.getUserMention(userId) + " disconnected me bye bye";
			channel.sendMessage(new EmbedBuilder().setColor(Color.RED).setDescription(message).setTimestamp(Instant.now()).build()).queue();
		}
	}

	public void play(GuildInteraction ia, String query, SearchProvider searchProvider){
		var manager = this.musicPlayers.computeIfAbsent(ia.getGuildId(), guildId -> new MusicManager(this.modules, guildId, ia.getChannelId()));

		var matcher = SPOTIFY_URL_PATTERN.matcher(query);
		if(matcher.matches()){
			this.modules.get(SpotifyModule.class).load(ia, manager, matcher);
			return;
		}

		if(!URL_PATTERN.matcher(query).matches()){
			switch(searchProvider){
				case YOUTUBE:
					query = "ytsearch:" + query;
					break;
				case SOUNDCLOUD:
					query = "scsearch:" + query;
					break;
			}
		}
		manager.getScheduler().getLink().getRestClient().loadItem(query, new AudioLoader(ia, manager));
	}

	public TrackScheduler getScheduler(long guildId){
		var manager = get(guildId);
		if(manager == null){
			return null;
		}
		return manager.getScheduler();
	}

	public Map<Long, MusicManager> getPlayers(){
		return this.musicPlayers;
	}

	public int getActivePlayers(){
		return this.musicPlayers.size();
	}

}
