package de.anteiku.kittybot.objects;

import de.anteiku.kittybot.objects.audio.LavalinkNode;
import de.anteiku.kittybot.utils.SentryHelper;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class Config
{
    private static String token;
    private static String secret;
    private static long botId;
    private static long supportGuildId;
    private static long logChannelId;
    private static String inviteUrl;
    private static String redirectUrl;
    private static String originUrl;
    private static String hastebinUrl;

    private static String discordBotsToken;
    private static String dblToken;

    private static String dbHost;
    private static int dbPort;
    private static String dbDatabase;
    private static String dbUser;
    private static String dbPassword;

    private static final List<Long> ADMIN_IDS = new ArrayList<>();

    private static final List<LavalinkNode> LAVALINK_NODES = new ArrayList<>();

    private static final String DEFAULT_PREFIX = ".";

    private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);

    private Config(){}

    public static boolean loadConfig()
    {
        final var configFile = new File("config.json");
        if (!configFile.exists())
            return false;
        LOGGER.info("Attempting to load the config");
        try
        {
            final var json = DataObject.fromJson(new FileInputStream(configFile));
            token = json.getString("bot_token");
            secret = json.getString("bot_secret");
            botId = json.getLong("bot_id");
            supportGuildId = json.getLong("support_guild_id");
            logChannelId = json.getLong("log_channel_id");
            inviteUrl = json.getString("invite_url");
            redirectUrl = json.getString("redirect_url");
            originUrl = json.getString("origin_url");
            hastebinUrl = json.getString("hastebin_url");

            discordBotsToken = json.getString("discord_bots_token");
            dblToken = json.getString("discord_bot_list_token");

            final var database = json.getObject("db");
            dbHost = database.getString("host");
            dbPort = database.getInt("port");
            dbDatabase = database.getString("db");
            dbUser = database.getString("user");
            dbPassword = database.getString("password");

            final var adminIds = json.getArray("admin_ids");
            for (int i = 0; i < adminIds.length(); i++)
                ADMIN_IDS.add(adminIds.getLong(i));

            final var lavalinkNodes = json.getArray("lavalink_nodes");
            for (var i = 0; i < lavalinkNodes.length(); i++) {
                final var node = lavalinkNodes.getObject(i);
                LAVALINK_NODES.add(new LavalinkNode(node.getString("host"), node.getInt("port"), node.getString("password")));
            }
        }
        catch (final Exception ex)
        {
            SentryHelper.captureException("Config couldn't be loaded", ex, Config.class);
            LOGGER.error("Config couldn't be loaded", ex);
            return false;
        }
        LOGGER.info("Config has been successfully loaded");
        return true;
    }

    public static String getToken()
    {
        return token;
    }

    public static String getSecret()
    {
        return secret;
    }

    public static long getBotId()
    {
        return botId;
    }

    public static long getSupportGuildId()
    {
        return supportGuildId;
    }

    public static long getLogChannelId()
    {
        return logChannelId;
    }

    public static String getInviteUrl()
    {
        return inviteUrl;
    }

    public static String getRedirectUrl()
    {
        return redirectUrl;
    }

    public static String getOriginUrl()
    {
        return originUrl;
    }

    public static String getHastebinUrl()
    {
        return hastebinUrl;
    }

    public static String getDiscordBotsToken()
    {
        return discordBotsToken;
    }

    public static String getDblToken()
    {
        return dblToken;
    }

    public static String getDbHost()
    {
        return dbHost;
    }

    public static int getDbPort()
    {
        return dbPort;
    }

    public static String getDatabase()
    {
        return dbDatabase;
    }

    public static String getDbUser()
    {
        return dbUser;
    }

    public static String getDbPassword()
    {
        return dbPassword;
    }

    public static List<Long> getAdminIds()
    {
        return ADMIN_IDS;
    }

    public static List<LavalinkNode> getLavalinkNodes()
    {
        return LAVALINK_NODES;
    }

    public static String getDefaultPrefix()
    {
        return DEFAULT_PREFIX;
    }
}