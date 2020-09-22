package de.anteiku.kittybot.events;

import de.anteiku.kittybot.objects.Config;
import de.anteiku.kittybot.utils.audio.LinkUtils;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class MiscEvents extends ListenerAdapter
{
    private static final Pattern BOT_PATTERN = Pattern.compile("<@!?(" + Config.getBotId() + ")>");

    @Override
    public void onGuildLeave(@NotNull final GuildLeaveEvent event)
    {
        final var link = LinkUtils.getLink(event.getGuild(), false);
        if (link != null)
            link.destroy();
    }

    @Override
    public void onGuildMessageReceived(@NotNull final GuildMessageReceivedEvent event)
    {
        if (event.getAuthor().isBot())
            return;

    }
}