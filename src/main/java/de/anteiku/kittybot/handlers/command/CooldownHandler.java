package de.anteiku.kittybot.handlers.command;

import de.anteiku.kittybot.objects.command.Command;
import de.anteiku.kittybot.utils.concurrent.ConcurrentUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CooldownHandler
{
    private static final Map<Long, List<Command>> COOLDOWN_MAP = new HashMap<>();

    private static final ScheduledExecutorService COOLDOWN_SCHEDULER = ConcurrentUtils.createScheduledThread("KittyBot Cooldown Handler", CooldownHandler.class);

    private CooldownHandler()
    {
        super();
    }

    public static void cooldown(final long guildId, final Command command)
    {
        final var cooldown = command.getCooldown();
        if (cooldown == 0)
            return;
        COOLDOWN_MAP.computeIfAbsent(guildId, k -> new ArrayList<>()).add(command);
        COOLDOWN_SCHEDULER.schedule(() -> COOLDOWN_MAP.get(guildId).remove(command), cooldown, TimeUnit.SECONDS);
    }

    public static boolean isOnCooldown(final long guildId, final Command command)
    {
        final var commands = COOLDOWN_MAP.get(guildId);
        return commands != null && commands.contains(command);
    }
}