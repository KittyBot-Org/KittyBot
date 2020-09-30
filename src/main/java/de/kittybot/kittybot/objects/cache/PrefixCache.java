package de.kittybot.kittybot.objects.cache;

import de.kittybot.kittybot.database.Database;
import net.dv8tion.jda.api.entities.Guild;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PrefixCache{

	private static final Map<String, String> GUILD_PREFIXES = new ConcurrentHashMap<>();

	public static String getCommandPrefix(String guildId){
		return GUILD_PREFIXES.computeIfAbsent(guildId, k -> Database.getCommandPrefix(guildId));
	}

	public static void setCommandPrefix(String guildId, String prefix){
		Database.setCommandPrefix(guildId, prefix);
		GUILD_PREFIXES.put(guildId, prefix);
	}

	public static void pruneCache(Guild guild){
		GUILD_PREFIXES.remove(guild.getId());
	}

}