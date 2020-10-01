package de.kittybot.kittybot.objects.cache;

import de.kittybot.kittybot.KittyBot;
import de.kittybot.kittybot.WebService;
import de.kittybot.kittybot.objects.guilds.GuildData;
import de.kittybot.kittybot.objects.session.DashboardSession;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GuildCache{

	private static final Map<String, GuildData> GUILD_CACHE = new HashMap<>();
	private static final Map<String, List<String>> USER_GUILD_CACHE = new HashMap<>();

	private GuildCache(){}

	public static List<GuildData> getGuilds(final DashboardSession dashboardSession) throws IOException{
		var guilds = USER_GUILD_CACHE.get(dashboardSession.getUserId());
		if(guilds != null){
			return guilds.stream().map(GUILD_CACHE::get).collect(Collectors.toList());
		}
		var guildsId = KittyBot.getJda().getGuildCache().applyStream(guildStream -> guildStream.map(Guild::getId).collect(Collectors.toList()));
		//noinspection ConstantConditions shut the fuck up IJ
		var retrievedGuilds = WebService.getOAuth2Client().getGuilds(dashboardSession)
				.complete()
				.stream()
				.filter(guild -> guildsId.contains(guild.getId()))
				.filter(guild -> guild.getPermissions().contains(Permission.ADMINISTRATOR))
				.map(guild -> new GuildData(guild.getId(), guild.getName(), guild.getIconUrl()))
				.collect(Collectors.toList());

		retrievedGuilds.forEach(guildData -> {
			var guildId = guildData.getId();
			cacheGuild(guildId, guildData);
			cacheGuildForUser(dashboardSession.getUserId(), guildId);
		});
		return retrievedGuilds;
	}

	public static void cacheGuild(final String guildId, final GuildData guildData){
		GUILD_CACHE.put(guildId, guildData);
	}

	public static void cacheGuildForUser(final String userId, final String guildId){
		USER_GUILD_CACHE.computeIfAbsent(userId, k -> new ArrayList<>()).add(guildId);
	}

	public static void uncacheGuildForUser(final String userId, final String guildId){
		var userGuilds = USER_GUILD_CACHE.get(userId);
		if(userGuilds == null){
			return;
		}
		userGuilds.remove(guildId);
	}

	public static void uncacheGuild(final Guild guild){
		var guildId = guild.getId();
		GUILD_CACHE.remove(guildId);
		USER_GUILD_CACHE.forEach((userId, guildIds) -> guildIds.remove(guildId));
	}

}
