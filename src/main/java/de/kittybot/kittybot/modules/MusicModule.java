package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.command.context.CommandContext;
import de.kittybot.kittybot.module.Module;
import de.kittybot.kittybot.objects.MusicPlayer;
import de.kittybot.kittybot.utils.Config;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class MusicModule extends Module{

	private Map<Long, MusicPlayer> musicPlayers;

	@Override
	public void onEnable(){
		this.musicPlayers = new HashMap<>();
	}

	@Override
	public void onGuildLeave(@NotNull GuildLeaveEvent event){
		destroy(event.getGuild().getIdLong());
	}

	@Override
	public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event){
		if(event instanceof GuildVoiceLeaveEvent || event instanceof GuildVoiceMoveEvent){
			var player = get(event.getEntity().getGuild().getIdLong());
			if(player == null){
				return;
			}
			if(event.getEntity().getIdLong() == Config.BOT_ID){
				this.modules.get(MusicModule.class).destroy(event.getEntity().getGuild().getIdLong());
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

	public void destroy(long guildId){
		var link = this.modules.get(LavalinkModule.class).getExistingLink(guildId);
		if(link != null){
			link.destroy();
		}
		var player = this.musicPlayers.remove(guildId);
		player.updateMusicController();
	}

	public MusicPlayer create(CommandContext ctx){
		var guildId = ctx.getGuildId();
		var link = this.modules.get(LavalinkModule.class).getLink(guildId);
		var player = new MusicPlayer(this.modules, link, guildId, ctx.getChannelId());
		this.musicPlayers.put(guildId, player);
		return player;
	}

}
