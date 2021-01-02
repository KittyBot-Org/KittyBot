package de.kittybot.kittybot.managers;

import de.kittybot.kittybot.command.ctx.CommandContext;
import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.objects.MusicPlayer;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
			var guild = event.getEntity().getGuild();
			var player = get(guild.getIdLong());
			if(player == null){
				return;
			}
			var channel = event.getChannelLeft();
			var currentChannel = player.getLink().long
			if(channel == null || !channel.getId().equals(currentChannel)){
				return;
			}
			if(channel.getMembers().stream().anyMatch(member -> !member.getUser().isBot())){
				return;
			}
			planDestroy(player, currentChannel);
		}
	}

	@Override
	public void onGuildLeave(@NotNull GuildLeaveEvent event){
		destroy(event.getGuild().getIdLong());
	}

	public MusicPlayer create(CommandContext ctx){
		var guild = ctx.getGuild();
		var guildId = ctx.getGuild().getIdLong();
		var link = this.main.getLavalinkManager().getLink(guild);
		var player = new MusicPlayer(link, guildId, ctx.getChannelId());
		musicPlayers.put(guildId, player);
		return player;
	}

	public void planDestroy(MusicPlayer player, long currentChannel){
		if(player != null){
			this.main.getEventWaiter().waitForEvent(GuildVoiceJoinEvent.class,
					event -> event.getChannelJoined().getIdLong() == currentChannel && !event.getEntity().getUser().isBot(),
					event -> {},
					3,
					TimeUnit.MINUTES,
					player::destroy
			);
		}
	}

	public void destroy(long guildId){
		var musicPlayer = get(guildId);
		if(musicPlayer == null){
			return;
		}
		this.main.getLavalinkManager().getLavalink().getExistingLink(String.valueOf(guildId)).destroy();
		musicPlayers.remove(guildId);
	}

	public MusicPlayer get(long guildId){
		return musicPlayers.get(guildId);
	}

}
