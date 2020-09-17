package de.anteiku.kittybot.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public class EmbedUtils
{
    private EmbedUtils()
    {
        super();
    }

    public static MessageEmbed getSuccessEmbed(final String text)
    {
        return getEmbed(text, Color.GREEN, "");
    }

    public static MessageEmbed getErrorEmbed(final String cause)
    {
        return getEmbed(cause, Color.RED, "Error");
    }

    private static MessageEmbed getEmbed(final String text, final Color color, final String author)
    {
        final var eb = new EmbedBuilder();
        eb.setAuthor(author);
        eb.setDescription(text);
        eb.setColor(color);
        return eb.build();
    }
}