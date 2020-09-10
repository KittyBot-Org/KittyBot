package de.anteiku.kittybot.utils.concurrent;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.utils.SentryHelper;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class ThreadFactoryBuilder
{
    private String name = "KittyBot Thread";
    private Class<?> clazz = KittyBot.class;

    public ThreadFactoryBuilder withName(final String name)
    {
        this.name = name;
        return this;
    }

    public ThreadFactoryBuilder withClass(final Class<?> clazz)
    {
        this.clazz = clazz;
        return this;
    }

    public ThreadFactory build()
    {
        final var defaultThreadFactory = Executors.defaultThreadFactory();
        return runnable ->
        {
            final var thread = defaultThreadFactory.newThread(runnable);
            thread.setName(name);
            thread.setUncaughtExceptionHandler((t, ex) ->
            {
                final var threadName = t.getName();
                SentryHelper.captureException("There was an exception in thread " + threadName, ex, clazz);
                LoggerFactory.getLogger(clazz).error("There was an exception in thread {}", threadName, ex);
            });
            return thread;
        };
    }
}