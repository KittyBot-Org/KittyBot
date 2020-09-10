package de.anteiku.kittybot;

import de.anteiku.kittybot.objects.Config;
import de.anteiku.kittybot.utils.TextUtils;
import de.anteiku.kittybot.utils.audio.LinkUtils;
import lavalink.client.io.jda.JdaLink;
import net.dv8tion.jda.api.GatewayEncoding;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;

import static net.dv8tion.jda.api.requests.GatewayIntent.*;

public class KittyBot
{
    private static final Logger LOGGER = LoggerFactory.getLogger(KittyBot.class);

    public static void main(String[] args)
    {
        if (!Config.loadConfig()){
            LOGGER.error("The config couldn't be loaded. Exiting");
            System.exit(1);
        }
        if (!LinkUtils.loadNodes())
        {
            LOGGER.error("Some lavalink nodes couldn't be loaded. Exiting");
            System.exit(1);
        }
        try
        {
            JDABuilder.create(Config.getToken(),
                    EnumSet.of(
                            GUILD_MEMBERS,
                            GUILD_EMOJIS,
                            GUILD_INVITES,
                            GUILD_VOICE_STATES,
                            GUILD_MESSAGES,
                            GUILD_MESSAGE_REACTIONS
                    ))
                    .disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS) // JDA already disables these by default but we explicitly disable it to get rid of warnings on startup
                    .addEventListeners(LinkUtils.getLavalink())
                    .setStatus(OnlineStatus.DO_NOT_DISTURB)
                    .setActivity(Activity.watching("myself load"))
                    .setGatewayEncoding(GatewayEncoding.ETF)
                    .setVoiceDispatchInterceptor(LinkUtils.getVoiceDispatchInterceptor())
                    .build()
                    .awaitReady();
        }
        catch (final Exception ex)
        {
            LOGGER.error("There was an error while building JDA. Exiting");
            System.exit(1);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(null, () ->
        {
            LOGGER.info("Running shutdown hook");
            final var links = LinkUtils.getLavalink().getLinks();
            if (!links.isEmpty())
            {
                links.forEach(JdaLink::destroy);
                LOGGER.info("Destroyed {} {}", links.size(), TextUtils.pluralize(links, "link"));
            }
        }, "KittyBot Shutdown Hooks Thread"));
    }
}