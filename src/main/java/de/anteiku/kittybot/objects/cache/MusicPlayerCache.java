package de.anteiku.kittybot.objects.cache;

import de.anteiku.kittybot.objects.audio.MusicPlayer;
import de.anteiku.kittybot.utils.audio.LinkUtils;
import net.dv8tion.jda.api.entities.Guild;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MusicPlayerCache
{
    private static final Map<Long, MusicPlayer> MUSIC_PLAYER_CACHE = new ConcurrentHashMap<>();

    private MusicPlayerCache()
    {
        super();
    }

    public static MusicPlayer createMusicPlayer(final Guild guild)
    {
        final var lavalinkPlayer = LinkUtils.getLavalinkPlayer(guild);
        final var musicPlayer = new MusicPlayer(lavalinkPlayer);
        lavalinkPlayer.addListener(musicPlayer);
        MUSIC_PLAYER_CACHE.put(guild.getIdLong(), musicPlayer);
        return musicPlayer;
    }

    public static MusicPlayer getMusicPlayer(final Guild guild, final boolean createIfAbsent)
    {
        var musicPlayer = MUSIC_PLAYER_CACHE.get(guild.getIdLong());
        if (musicPlayer == null && createIfAbsent)
            musicPlayer = createMusicPlayer(guild);
        return musicPlayer;
    }

    public static void destroyMusicPlayer(final Guild guild)
    {
        LinkUtils.getLink(guild).destroy();
        MUSIC_PLAYER_CACHE.remove(guild.getIdLong());
    }
}