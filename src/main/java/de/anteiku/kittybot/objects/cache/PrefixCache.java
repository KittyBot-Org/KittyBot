package de.anteiku.kittybot.objects.cache;

import de.anteiku.kittybot.database.Database;
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;
import java.util.Map;

public class PrefixCache {
    private static final Map<String, String> GUILD_PREFIXES = new HashMap<>();

    public static String getCommandPrefix(String guildId) {
        return GUILD_PREFIXES.computeIfAbsent(guildId, k -> Database.getCommandPrefix(guildId));
    }

    public static void setCommandPrefix(String guildId, String prefix) {
        Database.setCommandPrefix(guildId, prefix);
        GUILD_PREFIXES.put(guildId, prefix);
    }

    public static void pruneCache(Guild guild) {
        GUILD_PREFIXES.remove(guild.getId());
    }
}