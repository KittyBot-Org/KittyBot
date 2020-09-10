package de.anteiku.kittybot.utils.concurrent;

import de.anteiku.kittybot.KittyBot;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ConcurrentUtils
{
    private static final ThreadFactoryBuilder THREAD_FACTORY_BUILDER = new ThreadFactoryBuilder();
    private static final ScheduledExecutorService SCHEDULER = createScheduledThread("KittyBot Scheduler");

    private ConcurrentUtils()
    {
        super();
    }

    public static ScheduledExecutorService createScheduledThread(final String name)
    {
        return Executors.newSingleThreadScheduledExecutor(THREAD_FACTORY_BUILDER.withName(name).withClass(KittyBot.class).build());
    }

    public static ScheduledExecutorService createScheduledThread(final String name, final Class<?> clazz)
    {
        return Executors.newSingleThreadScheduledExecutor(THREAD_FACTORY_BUILDER.withName(name).withClass(clazz).build());
    }

    public static ScheduledExecutorService getScheduler()
    {
        return SCHEDULER;
    }
}