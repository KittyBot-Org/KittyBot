package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.objects.module.Module;
import de.kittybot.kittybot.objects.music.MusicPlayer;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.MessageUtils;
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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MusicModule extends Module implements Serializable{

	private Map<Long, MusicPlayer> musicPlayers;

	@Override
	public void onEnable(){
		this.musicPlayers = new HashMap<>();
	}

	@Override
	public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event){
		var player = this.musicPlayers.get(event.getGuild().getIdLong());
		if(player != null){
			player.setLastMessageId(event.getMessageIdLong());
		}
	}

	@Override
	public void onGuildMessageReactionAdd(@NotNull GuildMessageReactionAddEvent event){
		if(event.getUserIdLong() == Config.BOT_ID){
			return;
		}
		var player = this.musicPlayers.get(event.getGuild().getIdLong());
		if(player == null){
			return;
		}
		var member = event.getMember();

		var voiceState = member.getVoiceState();
		if(voiceState == null || voiceState.getChannel() == null || player.getLink().getChannelId() != voiceState.getChannel().getIdLong()){
			return;
		}
		var messageId = event.getMessageIdLong();
		var currentTrack = player.getPlayingTrack();
		var userId = event.getUserIdLong();
		var requesterId = currentTrack == null ? -1L : currentTrack.getUserData(Long.class);
		var settings = this.modules.get(SettingsModule.class).getSettings(event.getGuild().getIdLong());

		if(messageId != player.getControllerMessageId()){
			return;
		}
		switch(event.getReactionEmote().getAsReactionCode()){
			case "\u2B05\uFE0F":// ⬅
				if(requesterId == userId || member.hasPermission(Permission.ADMINISTRATOR) || settings.hasDJRole(member)){
					player.previous();
					player.setPaused(false);
				}
				break;
			case "\u27A1\uFE0F":// ➡
				if(requesterId == userId || member.hasPermission(Permission.ADMINISTRATOR) || settings.hasDJRole(member)){
					player.next();
					player.setPaused(false);
				}
				break;
			case "<:PlayPause:744945002416963634>"://play pause
				if(requesterId == userId || member.hasPermission(Permission.ADMINISTRATOR) || settings.hasDJRole(member)){
					player.pause();
				}
				break;
			case "\uD83D\uDD00":// 🔀
				if(member.hasPermission(Permission.ADMINISTRATOR) || settings.hasDJRole(member)){
					player.shuffle();
				}
				break;
			case "\uD83D\uDD09":// 🔉
				player.increaseVolume(-10);
				break;
			case "\uD83D\uDD0A":// 🔊
				player.increaseVolume(10);
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
		destroy(event.getGuild().getIdLong(), -1L);
	}

	@Override
	public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event){
		if(event instanceof GuildVoiceLeaveEvent || event instanceof GuildVoiceMoveEvent){
			var player = get(event.getEntity().getGuild().getIdLong());
			if(player == null){
				return;
			}
			if(event.getEntity().getIdLong() == Config.BOT_ID){
				this.modules.get(MusicModule.class).destroy(event.getEntity().getGuild().getIdLong(), -1L);
				return;
			}
			var channel = event.getChannelLeft();
			var currentChannel = player.getLink().getChannelId();
			if(channel == null || channel.getIdLong() != currentChannel){
				return;
			}
			if(channel.getMembers().stream().anyMatch(member -> !member.getUser().isBot())){
				return;
			}
			player.planDestroy();
		}
		else if(event instanceof GuildVoiceJoinEvent){
			var player = get(event.getEntity().getGuild().getIdLong());
			if(player == null){
				return;
			}
			player.cancelDestroy();
		}
	}

	public MusicPlayer get(long guildId){
		return this.musicPlayers.get(guildId);
	}

	public void destroy(long guildId, long userId){
		var link = this.modules.get(LavalinkModule.class).getExistingLink(guildId);
		if(link != null){
			link.destroy();
		}
		var player = this.musicPlayers.remove(guildId);
		if(player != null){
			player.updateMusicController();
			var channel = player.getTextChannel();
			if(channel == null || !channel.canTalk()){
				return;
			}
			var message = userId == -1 ? "Disconnected due to inactivity" : MessageUtils.getUserMention(userId) + " disconnected me bye bye";
			channel.sendMessage(new EmbedBuilder().setColor(Color.RED).setDescription(message).setTimestamp(Instant.now()).build()).queue();
		}
	}

	public Collection<MusicPlayer> getPlayers(){
		return this.musicPlayers.values();
	}

	public int getActivePlayers(){
		return this.musicPlayers.size();
	}

	public MusicPlayer create(CommandContext ctx){
		var guildId = ctx.getGuildId();
		var link = this.modules.get(LavalinkModule.class).getLink(guildId);
		var player = new MusicPlayer(this.modules, link, guildId, ctx.getChannelId());
		this.musicPlayers.put(guildId, player);
		return player;
	}

}
