package de.anteiku.kittybot.handlers.command;

import de.anteiku.kittybot.objects.command.Command;
import de.anteiku.kittybot.objects.command.CommandContext;
import de.anteiku.kittybot.utils.SentryHelper;
import de.anteiku.kittybot.utils.TextUtils;
import io.github.classgraph.ClassGraph;
import net.dv8tion.jda.api.entities.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static de.anteiku.kittybot.utils.MessageUtils.sendError;

public class CommandHandler
{
    private static final Map<String, Command> COMMAND_MAP = new HashMap<>();
    private static final List<Command> DISTINCT_COMMANDS = new ArrayList<>();

    private static final ClassGraph CLASS_GRAPH = new ClassGraph().acceptPackages("de.anteiku.kittybot.commands");

    private static final Logger LOGGER = LoggerFactory.getLogger("CommandLoader");

    private CommandHandler()
    {
        super();
    }

    public static void handle(final Message msg, final String prefix)
    {
        final var content = msg.getContentRaw().substring(prefix.length());
        final var channel = msg.getTextChannel();
        if (content.isEmpty())
        {
            sendError("Please specify a command.", channel);
            return;
        }
        final var command = content.contains(" ") ? content.substring(0, content.indexOf(' ')) : content;
        final var cmd = COMMAND_MAP.get(command.toLowerCase());
        if (cmd == null)
        {
            sendError("**" + command + "** is not a valid command. Type `" + prefix + "cmds` for the list of available commands.", channel);
            return;
        }
        if (!msg.getMember().hasPermission(cmd.getRequiredPermission()))
        {
            sendError("You don't have permissions to execute this command.", channel);
            return;
        }
        final var guildId = msg.getGuild().getIdLong();
        if (CooldownHandler.isOnCooldown(guildId, cmd))
        {
            sendError("This command is on cooldown", channel);
            return;
        }

        final var maxArgs = cmd.getMaxArgs();
        final var tmp = content.split("\\s+", maxArgs > 0 ? maxArgs + 1 : 0);
        final var args = Arrays.copyOfRange(tmp, 1, tmp.length);
        cmd.execute(new CommandContext(msg, args));
        CooldownHandler.cooldown(guildId, cmd);
    }

    public static boolean registerCommands()
    {
        LOGGER.info("Attempting to load the commands");
        try (final var result = CLASS_GRAPH.scan())
        {
            for (final var cls : result.getAllClasses())
            {
                final var cmd = (Command) cls.loadClass().getDeclaredConstructor().newInstance();
                final var invoke = cmd.getInvoke();
                COMMAND_MAP.put(invoke, cmd);
                DISTINCT_COMMANDS.add(cmd);
                for (final var alias : cmd.getAliases())
                    COMMAND_MAP.put(alias, cmd);
            }
        }
        catch (final Exception ex)
        {
            SentryHelper.captureException("There was an error while registering commands", ex, "CommandLoader");
            LOGGER.error("There was an error while registering commands", ex);
            return false;
        }
        LOGGER.info("A total of {} {} has been loaded", DISTINCT_COMMANDS.size(), TextUtils.pluralize(DISTINCT_COMMANDS, "command"));
        return true;
    }
}