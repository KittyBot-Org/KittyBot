package de.anteiku.kittybot.utils.audio;

import de.anteiku.kittybot.objects.Config;
import de.anteiku.kittybot.utils.SentryHelper;
import io.sentry.event.Event;
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
    private static final JdaLavalink LAVALINK = new JdaLavalink(Config.getBotId(), 0, null);

    private static final Logger LOGGER = LoggerFactory.getLogger("LinkLoader");

    private LinkUtils(){}

    public static boolean loadNodes()
    {
        final var nodes = Config.getLavalinkNodes();
        if (nodes.isEmpty())
        {
            LOGGER.error("There are no nodes available");
            return false;
        }
        var failedNodes = 0;
        final var nodeCount = nodes.size();
        final var totalNodes = "total of " +  nodeCount + " "  + pluralize(nodes, "node");
        LOGGER.info("Attempting to load a {}", totalNodes);
        for (var i = 0; i < nodeCount; i++)
        {
            final var node = nodes.get(i);
            final var host = node.getHost();
            final var port = node.getPort();
            final var password = node.getPassword();
            try
            {
                LOGGER.info("Attempting to load node {}", i + 1);
                LAVALINK.addNode("Lavalink node " + (i + 1), new URI("ws://" + host + ":" + port), password);
                LOGGER.info("Node {} has been successfully loaded", i + 1);
            }
            catch (final Exception ex)
            {
                failedNodes++;
                SentryHelper.captureException("Couldn't load node " + (i + 1), ex, "LinkLoader", Event.Level.WARNING);
                LOGGER.warn("Couldn't load node {}", i + 1, ex);
            }
        }
        if (failedNodes != nodeCount)
        {
            LOGGER.info("A {} has been successfully loaded", totalNodes);
            return true;
        }
        return false;
    }

    public static JdaLink getLink(final Guild guild)
    {
        return getLink(guild, true);
    }

    public static JdaLink getLink(final Guild guild, final boolean createIfAbsent)
    {
        return createIfAbsent ? LAVALINK.getLink(guild) : LAVALINK.getExistingLink(guild);
    }

    public static LavalinkPlayer getLavalinkPlayer(final Guild guild)
    {
        return getLink(guild).getPlayer();
    }

    public static VoiceDispatchInterceptor getVoiceDispatchInterceptor()
    {
        return LAVALINK.getVoiceInterceptor();
    }

    public static JdaLavalink getLavalink()
    {
        return LAVALINK;
    }
}