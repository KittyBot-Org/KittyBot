package de.anteiku.kittybot.utils;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public class EmbedUtils
{
    public static MessageEmbed getSuccessEmbed(final String text)
    {
        return getEmbed(text, Color.GREEN);
    }

    public static MessageEmbed getErrorEmbed(final String text)
    {
        return getEmbed(text, Color.RED);
    }

    public static MessageEmbed getEmbed(final String text, final Color color)
    {
        final var eb = new EmbedBuilder();
        eb.setDescription(text);
        eb.setColor(color);
        return eb.build();
    }
}