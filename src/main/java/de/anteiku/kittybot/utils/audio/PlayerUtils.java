package de.anteiku.kittybot.utils.audio;

import de.anteiku.kittybot.objects.audio.MusicPlayer;
import de.anteiku.kittybot.objects.cache.MusicPlayerCache;
import net.dv8tion.jda.api.entities.Guild;

public class PlayerUtils
{
    private PlayerUtils()
    {
        super();
    }

    public static MusicPlayer getMusicPlayer(final Guild guild)
    {
        return MusicPlayerCache.getMusicPlayer(guild, false);
    }

    public static MusicPlayer getMusicPlayer(final Guild guild, final boolean createIfAbsent)
    {
        return MusicPlayerCache.getMusicPlayer(guild, createIfAbsent);
    }

    public static void destroyMusicPlayer(final Guild guild)
    {
        MusicPlayerCache.destroyMusicPlayer(guild);
    }
}