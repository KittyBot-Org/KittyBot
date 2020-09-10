package de.anteiku.kittybot.utils.audio;

import de.anteiku.kittybot.objects.Config;
import lavalink.client.io.jda.JdaLavalink;
import lavalink.client.io.jda.JdaLink;
import lavalink.client.player.LavalinkPlayer;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.hooks.VoiceDispatchInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

import static de.anteiku.kittybot.utils.TextUtils.pluralize;

public class LinkUtils
{
    private static JdaLavalink lavalink;

    private static final Logger LOGGER = LoggerFactory.getLogger(LinkUtils.class);

    private LinkUtils()
    {
        super();
    }

    public static boolean loadNodes()
    {
        lavalink = new JdaLavalink(Config.getBotId(), 0, null);
        final var nodes = Config.getLavalinkNodes();
        if (nodes.isEmpty())
        {
            LOGGER.error("There are no nodes available");
            return false;
        }
        LOGGER.info("Trying to load {} {}", nodes.size(), pluralize(nodes, "node"));
        for (final var node : nodes)
        {
            final var host = node.getHost();
            final var port = node.getPort();
            final var password = node.getPassword();
            try
            {
                lavalink.addNode(new URI("ws://" + host + ":" + port), password);
            }
            catch (final Exception ex)
            {
                LOGGER.error("Couldn't load lavalink node with host {}, port {} and password {}", host, port, password, ex);
                return false;
            }
        }
        final var loadedNodes = lavalink.getNodes();
        LOGGER.info("Successfully loaded {} {}", loadedNodes.size(), pluralize(loadedNodes, "node"));
        return true;
    }

    public static JdaLink getLink(final Guild guild)
    {
        return getLink(guild, true);
    }

    public static JdaLink getLink(final Guild guild, final boolean createIfAbsent)
    {
        return createIfAbsent ? lavalink.getLink(guild) : lavalink.getExistingLink(guild);
    }

    public static LavalinkPlayer getLavalinkPlayer(final Guild guild)
    {
        return getLink(guild).getPlayer();
    }

    public static VoiceDispatchInterceptor getVoiceDispatchInterceptor()
    {
        return lavalink.getVoiceInterceptor();
    }

    public static JdaLavalink getLavalink()
    {
        return lavalink;
    }
}