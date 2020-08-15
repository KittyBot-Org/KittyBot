package de.anteiku.kittybot.events;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.objects.Cache;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class OnGuildVoiceEvent extends ListenerAdapter{

	private static final EventWaiter WAITER = new EventWaiter();

	@Override
	public void onGuildVoiceUpdate(@NotNull final GuildVoiceUpdateEvent event){
		if (event instanceof GuildVoiceMoveEvent || event instanceof GuildVoiceLeaveEvent){
			var guild = event.getEntity().getGuild();
			var musicPlayer = Cache.getMusicPlayer(guild);
			if (musicPlayer == null)
				return;
			var channel = event.getChannelLeft();
			var currentChannel = musicPlayer.getPlayer().getLink().getChannel();
			if (!channel.getId().equals(currentChannel))
				return;
			if (channel.getMembers().size() != 1)
				return;
			WAITER.waitForEvent(GuildVoiceJoinEvent.class,
					ev -> ev.getChannelJoined().getId().equals(currentChannel),
					ev -> {}, 5, TimeUnit.MINUTES, () -> KittyBot.getLavalink().getLink(guild).destroy());
		}
	}
}
