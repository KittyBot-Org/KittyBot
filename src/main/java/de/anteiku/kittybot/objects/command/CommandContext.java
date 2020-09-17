package de.anteiku.kittybot.objects.command;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import static de.anteiku.kittybot.utils.MessageUtils.*;

public class CommandContext
{
    private final long messageId;
    private final long guildId;
    private final long authorId;
    private final long channelId;
    private final JDA jda;
    private final String[] args;

    public CommandContext(final Message message, final String[] args)
    {
        this.messageId = message.getIdLong();
        this.guildId = message.getGuild().getIdLong();
        this.authorId = message.getAuthor().getIdLong();
        this.channelId = message.getTextChannel().getIdLong();
        this.jda = message.getJDA();
        this.args = args;
    }

    public void reply(final String text)
    {
        success(text);
    }

    public void reply(final MessageEmbed embed)
    {
        sendMessage(embed, jda.getTextChannelById(channelId));
    }

    public void success(final String text)
    {
        sendSuccess(text, jda.getTextChannelById(channelId));
    }

    public void replyError(final String cause)
    {
        sendError(cause, jda.getTextChannelById(channelId));
    }

    public long getMessageId()
    {
        return messageId;
    }

    public long getGuildId()
    {
        return guildId;
    }

    public long getAuthorId()
    {
        return authorId;
    }

    public long getChannelId()
    {
        return channelId;
    }

    public String[] getArgs()
    {
        return args;
    }
}