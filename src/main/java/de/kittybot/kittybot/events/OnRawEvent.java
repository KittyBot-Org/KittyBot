package de.kittybot.kittybot.events;

import de.kittybot.kittybot.cache.GuildCache;
import de.kittybot.kittybot.objects.guilds.GuildData;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.RawGatewayEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class OnRawEvent extends ListenerAdapter{

	@Override
	public void onRawGateway(@NotNull final RawGatewayEvent event){ // literally how to bypass jda - this is topi's fucking idea don't blame me ok - cane
		if(!event.getType().equals("GUILD_MEMBER_UPDATE")){
			return;
		}
		final var json = event.getPayload();
		final var guildId = json.getString("guild_id");
		final var userId = json.getObject("user").getString("id");
		final var guild = event.getJDA().getGuildById(guildId);
		//noinspection ConstantConditions shut :)
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