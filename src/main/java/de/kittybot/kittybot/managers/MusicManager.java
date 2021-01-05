package de.kittybot.kittybot.managers;

import de.kittybot.kittybot.command.CommandContext;
import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.objects.MusicPlayer;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MusicManager extends ListenerAdapter{

	private final KittyBot main;
	private final Map<Long, MusicPlayer> musicPlayers;

	public MusicManager(KittyBot main){
		this.main = main;
		this.musicPlayers = new HashMap<>();
	}

	@Override
	public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event){
		if(event instanceof GuildVoiceLeaveEvent || event instanceof GuildVoiceMoveEvent){
			var player = get(event.getEntity().getGuild().getIdLong());
			if(player == null){
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

	@Override
	public void onGuildLeave(@NotNull GuildLeaveEvent event){
		destroy(event.getGuild().getIdLong());
	}

	public MusicPlayer create(CommandContext ctx){
		var guildId = ctx.getGuildId();
		var link = this.main.getLavalinkManager().getLink(guildId);
		var player = new MusicPlayer(this.main, link, guildId, ctx.getChannelId());
		this.musicPlayers.put(guildId, player);
		return player;
	}

	public void destroy(long guildId){
		var link = this.main.getLavalinkManager().getLavalink().getExistingLink(guildId);
		if(link != null){
			link.destroy();
		}
		var player = this.musicPlayers.remove(guildId);
		player.updateMusicController();
	}

	public MusicPlayer get(long guildId){
		return this.musicPlayers.get(guildId);
	}

}
