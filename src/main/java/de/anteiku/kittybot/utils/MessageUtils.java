package de.anteiku.kittybot.utils;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

public class MessageUtils
{
    private MessageUtils(){}

    public static void sendMessage(final String text, final TextChannel channel)
    {
        if (channel == null || !channel.canTalk())
            return;
        channel.sendMessage(text).queue();
    }

    public static void sendMessage(final MessageEmbed embed, final TextChannel channel)
    {
        if (channel == null || !channel.canTalk())
            return;
        channel.sendMessage(embed).queue();
    }

    public static void sendError(final String cause, final TextChannel channel)
    {
        sendMessage(EmbedUtils.getErrorEmbed(cause), channel);
    }

    public static void sendSuccess(final String text, final TextChannel channel)
    {
        sendMessage(EmbedUtils.getSuccessEmbed(text), channel);
    }
}