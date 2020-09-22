package de.anteiku.kittybot.utils;

import java.util.Collection;

public class TextUtils
{
    private TextUtils(){}

    public static String pluralize(final Collection<?> collection, final String text)
    {
        return collection.size() == 1 ? text : text + "s";
    }
}