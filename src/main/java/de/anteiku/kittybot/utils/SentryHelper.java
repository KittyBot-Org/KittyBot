package de.anteiku.kittybot.utils;

import io.sentry.Sentry;
import io.sentry.event.Event;
import io.sentry.event.EventBuilder;
import io.sentry.event.interfaces.ExceptionInterface;

public class SentryHelper
{
    private SentryHelper(){}

    public static void captureException(final String message, final Throwable throwable, final Class<?> clazz)
    {
        captureException(message, throwable, clazz.getName(), Event.Level.ERROR);
    }

    public static void captureException(final String message, final Throwable throwable, final Class<?> clazz, final Event.Level level)
    {
        captureException(message, throwable, clazz.getName(), level);
    }

    public static void captureException(final String message, final Throwable throwable, final String clazz)
    {
        captureException(message, throwable, clazz, Event.Level.ERROR);
    }

    public static void captureException(final String message, final Throwable throwable, final String clazz, final Event.Level level)
    {
        Sentry.capture(
                new EventBuilder()
                        .withMessage(message)
                        .withSentryInterface(new ExceptionInterface(throwable))
                        .withLogger(clazz)
                        .withLevel(level)
        );
    }
}