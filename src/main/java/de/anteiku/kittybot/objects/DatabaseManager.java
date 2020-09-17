package de.anteiku.kittybot.objects;

import de.anteiku.kittybot.utils.SentryHelper;
import io.sentry.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseManager
{
    private static int failedConnections = 0;

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseManager.class);

    private DatabaseManager()
    {
        super();
    }

    public static Connection getConnection()
    {
        try
        {
            final var connection = DriverManager.getConnection("jdbc:postgresql://" + Config.getDbHost() + ":" + Config.getDbPort() + "/" + Config.getDatabase(), Config.getDbUser(), Config.getDbPassword());
            failedConnections = 0;
            return connection;
        }
        catch (final Exception ex)
        {
            if (failedConnections == 0)
            {
                SentryHelper.captureException("There was an error while establishing a DB connection", ex, DatabaseManager.class);
                LOGGER.error("There was an error while establishing a DB connection", ex);
                failedConnections++;
                return null;
            }
            if (failedConnections < 3)
            {
                SentryHelper.captureException("There was an error while establishing a DB connection", ex, DatabaseManager.class, Event.Level.WARNING);
                LOGGER.warn("There was an error while establishing a DB connection", ex);
                failedConnections++;
                return null;
            }
            SentryHelper.captureException("There was an error while establishing a DB connection", ex, DatabaseManager.class);
            LOGGER.error("There was an error while establishing a DB connection. Hits: {}", failedConnections, ex);
            failedConnections++;
            return null;
        }
    }

    public static boolean testConnection() // just a convenience name method to check on startup
    {
        LOGGER.info("Attempting to connect to the DB");
        try (final var connection = getConnection())
        {
            if (connection == null)
                return false;
            LOGGER.info("Successfully connected to the DB");
            return true;
        }
        catch (final Exception ex)
        {
            SentryHelper.captureException("There was an error while testing the DB connection? what the fuck?", ex, DatabaseManager.class, Event.Level.WARNING);
            LOGGER.warn("There was an error while testing the DB connection? what the fuck?", ex);
            return false;
        }
    }

    // this is where the actual fun begins

    private static String executeGetQuery(final String property, final long guildId, final boolean asString)
    {
        final var defaultReturn = asString ? null : "-1";
        try (final var con = getConnection())
        {
            if (con == null)
                return defaultReturn;

            final var ps = con.prepareStatement("SELECT " + property + " FROM guilds WHERE guild_id='" + guildId + "'");
            try (ps; final var rs = ps.executeQuery())
            {
                if (!rs.next())
                    return defaultReturn;
                return rs.getString(property) == null && asString ? null : "-1";
            }
        }
        catch (final Exception ex)
        {
            SentryHelper.captureException("There was an error while getting the " + property + " property for guild " + guildId, ex, DatabaseManager.class);
            LOGGER.error("There was an error while getting the {} property for guild {}", property, guildId, ex);
            return defaultReturn;
        }
    }

    private static <T> void executeSetQuery(final String property, final long guildId, final T value)
    {
        final var query = "INSERT INTO guilds (guild_id, " + property + ") VALUES (?, ?) ON CONFLICT (guild_id) DO UPDATE SET " + property + "='" + value + "'";
        try (final var con = getConnection())
        {
            if (con == null)
                return;

            try (final var ps = con.prepareStatement(query))
            {
                ps.setLong(1, guildId);
                ps.setObject(2, value);
                ps.executeUpdate();
            }
        }
        catch (final Exception ex)
        {
            SentryHelper.captureException("There was an error while setting the " + property + " property for guild " + guildId, ex, DatabaseManager.class);
            LOGGER.error("There was an error while setting the {} property for guild {}", property, guildId, ex);
        }
    }

    // helper getters

    private static String getPropertyAsString(final String property, final long guildId)
    {
        return executeGetQuery(property, guildId, true);
    }

    private static long getPropertyAsLong(final String property, final long guildId)
    {
        //noinspection ConstantConditions
        return Long.parseLong(executeGetQuery(property, guildId, false));
    }

    // getters

    public static long retrieveLogChannel(final long guildId)
    {
        return getPropertyAsLong("log_channel_id", guildId);
    }

    public static String retrievePrefix(final long guildId)
    {
        return getPropertyAsString("prefix", guildId);
    }

    // setters

    public static void setPrefix(final long guildId, final String prefix)
    {
        executeSetQuery("prefix", guildId, prefix);
    }
}