package de.anteiku.kittybot.objects.cache;

import de.anteiku.kittybot.objects.Config;
import de.anteiku.kittybot.objects.DatabaseManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PrefixCache
{
    private static final Map<Long, String> PREFIX_CACHE = new HashMap<>();

    private PrefixCache()
    {
        super();
    }

    public static String getPrefix(final long guildId)
    {
        return Objects.requireNonNullElseGet(PREFIX_CACHE.get(guildId), () ->
        {
            final var retrieved = DatabaseManager.retrievePrefix(guildId);
            final var prefix = retrieved == null ? Config.getDefaultPrefix() : retrieved;
            PREFIX_CACHE.put(guildId, prefix);
            return prefix;
        });
    }

    public static void setPrefix(final long guildId, final String prefix)
    {
        PREFIX_CACHE.put(guildId, prefix);
        DatabaseManager.setPrefix(guildId, prefix);
    }
}