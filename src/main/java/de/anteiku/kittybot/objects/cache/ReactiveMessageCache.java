package de.anteiku.kittybot.objects.cache;

import de.anteiku.kittybot.command.ACommand;
import de.anteiku.kittybot.command.CommandContext;
import de.anteiku.kittybot.database.Database;
import de.anteiku.kittybot.objects.ReactiveMessage;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

import java.util.HashMap;
import java.util.Map;

public class ReactiveMessageCache {
    private static final Map<String, ReactiveMessage> REACTIVE_MESSAGES = new HashMap<>();

    public static void removeReactiveMessage(Guild guild, String messageId){
        var textChannel = guild.getTextChannelById(REACTIVE_MESSAGES.get(messageId).channelId);
        if(textChannel != null){
            textChannel.deleteMessageById(messageId).queue();
        }
        REACTIVE_MESSAGES.remove(messageId);
        Database.removeReactiveMessage(guild.getId(), messageId);
    }

    public static void addReactiveMessage(CommandContext ctx, Message message, ACommand cmd, String allowed){
        REACTIVE_MESSAGES.put(message.getId(), new ReactiveMessage(ctx.getChannel().getId(), ctx.getMessage().getId(), ctx.getUser().getId(), message.getId(), cmd.command, allowed));
        Database.addReactiveMessage(ctx.getGuild().getId(), ctx.getUser().getId(), ctx.getChannel().getId(), message.getId(), ctx.getMessage().getId(), cmd.command, allowed);
    }

    public static ReactiveMessage getReactiveMessage(Guild guild, String messageId){
        var reactiveMessage = REACTIVE_MESSAGES.get(messageId);
        if(reactiveMessage != null){
            return reactiveMessage;
        }
        reactiveMessage = Database.isReactiveMessage(guild.getId(), messageId);
        REACTIVE_MESSAGES.put(messageId, reactiveMessage);
        return reactiveMessage;
    }

    public static void pruneCache(Guild guild){
        REACTIVE_MESSAGES.remove(guild.getId());
    }
}