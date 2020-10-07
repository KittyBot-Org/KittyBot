package de.kittybot.kittybot.events;

import de.kittybot.kittybot.KittyBot;
import de.kittybot.kittybot.cache.MusicPlayerCache;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class OnGuildVoiceEvent extends ListenerAdapter{

	@Override
	public void onGuildVoiceUpdate(@NotNull final GuildVoiceUpdateEvent event){
		if(event instanceof GuildVoiceMoveEvent || event instanceof GuildVoiceLeaveEvent){
			var guild = event.getEntity().getGuild();
			var musicPlayer = MusicPlayerCache.getMusicManager(guild);
			if(musicPlayer == null){
				return;
			}
			var channel = event.getChannelLeft();
			var currentChannel = musicPlayer.getChannelId();
			if(channel == null || !channel.getId().equals(currentChannel)){
				return;
			}
			if(channel.getMembers().stream().anyMatch(member -> !member.getUser().isBot())){
				return;
			}
			KittyBot.getWaiter().waitForEvent(GuildVoiceJoinEvent.class,
					ev -> ev.getChannelJoined().getId().equals(currentChannel) && !ev.getEntity().getUser().isBot(),
					ev -> {
					}, 3, TimeUnit.MINUTES, () -> MusicPlayerCache.destroyMusicPlayer(guild));
		}
	}

}
