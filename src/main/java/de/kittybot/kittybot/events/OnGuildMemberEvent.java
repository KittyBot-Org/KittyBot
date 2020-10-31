package de.kittybot.kittybot.events;

import de.kittybot.kittybot.cache.GuildCache;
import de.kittybot.kittybot.objects.data.GuildData;
import de.kittybot.kittybot.utils.MessageUtils;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberUpdateEvent;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class OnGuildMemberEvent extends ListenerAdapter{

	@Override
	public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event){
		MessageUtils.sendAnnouncementMessage(event.getGuild(), event);
		GuildCache.uncacheGuildForUser(event.getUser().getId(), event.getGuild().getId());
	}

	@Override
	public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event){
		MessageUtils.sendAnnouncementMessage(event.getGuild(), event);
	}

	@Override
	public void onGuildMemberUpdateBoostTime(@NotNull GuildMemberUpdateBoostTimeEvent event){
		MessageUtils.sendAnnouncementMessage(event.getGuild(), event);
	}

	@Override
	public void onGuildMemberUpdate(@NotNull GuildMemberUpdateEvent event){
		final var guild = event.getGuild();
		final var guildId = guild.getId();
		final var userId = event.getUser().getId();

		guild.retrieveMemberById(userId).queue(member -> {
			if(member.hasPermission(Permission.ADMINISTRATOR)){
				GuildCache.cacheGuild(guildId, new GuildData(guildId, guild.getName(), guild.getIconUrl()));
				GuildCache.cacheGuildForUser(userId, guildId);
			}
			else{
				GuildCache.uncacheGuildForUser(userId, guildId);
			}
		});
	}

}
