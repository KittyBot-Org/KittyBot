package de.anteiku.kittybot.handlers.command;

import de.anteiku.kittybot.objects.command.Command;

import java.util.HashMap;
import java.util.Map;

public class CooldownHandler
{
    private static final Map<Command, Map<Long, Long>> COOLDOWN_MAP = new HashMap<>(); // K = Command, V = Map<userId, Timestamp>

    private CooldownHandler(){}

    public static void cooldown(final long userId, final Command command)
    {
        final var cooldown = command.getCooldown();
        if (cooldown == 0)
            return;
        COOLDOWN_MAP.computeIfAbsent(command, k -> new HashMap<>()).put(userId, System.currentTimeMillis() + cooldown * 1000);
    }

    public static boolean isOnCooldown(final long userId, final Command command)
    {
        final var entry = COOLDOWN_MAP.get(command);
        if (entry == null)
            return false;
        final var lastUsed = entry.get(userId);
        if (lastUsed == null)
            return false;
        if (System.currentTimeMillis() > lastUsed)
        {
            entry.remove(userId);
            return false;
        }
        return true;
    }
}