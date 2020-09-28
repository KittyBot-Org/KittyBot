package de.kittybot.kittybot.events;

import de.kittybot.kittybot.database.Database;
import de.kittybot.kittybot.objects.Emojis;
import de.kittybot.kittybot.objects.ReactiveMessage;
import de.kittybot.kittybot.objects.cache.CommandResponseCache;
import de.kittybot.kittybot.objects.cache.MessageCache;
import de.kittybot.kittybot.objects.cache.ReactiveMessageCache;
import de.kittybot.kittybot.objects.command.CommandManager;
import de.kittybot.kittybot.objects.messages.MessageData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.Instant;

public class OnGuildMessageEvent extends ListenerAdapter{

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event){
		var content = event.getMessage().getContentRaw();
		if(!content.isEmpty()){
			MessageCache.cacheMessage(event.getMessageId(), new MessageData(event.getMessage()));
		}
		if(event.getAuthor().isBot()){
			return;
		}
		if(!CommandManager.checkCommands(event)){
			if(event.getMessage().getMentionedUsers().size() == 1 && event.getMessage().getMentionedUsers().get(0).getId().equals(event.getJDA().getSelfUser().getId())){
				event.getMessage().addReaction(Emojis.QUESTION).queue();
				event.getChannel()
						.sendMessage(new EmbedBuilder().setColor(Color.ORANGE)
								.setTitle("Do you need help?")
								.setDescription("My current prefix for this guild is `" + Database.getCommandPrefix(event.getGuild()
										.getId()) + "`\n" + "If you don't like my prefix you can ping me directly!\n" + "To have a look at all my commands use `" + Database
										.getCommandPrefix(event.getGuild()
												.getId()) + "cmds`\n" + "To get help use `" + Database.getCommandPrefix(event.getGuild()
										.getId()) + "help`")
								.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl())
								.setFooter(event.getMember() == null ? event.getAuthor().getName() : event.getMember().getEffectiveName(), event.getAuthor().getEffectiveAvatarUrl())
								.setTimestamp(Instant.now())
								.build())
						.queue();
			}
		}
	}

	@Override
	public void onGuildMessageUpdate(@NotNull final GuildMessageUpdateEvent event){
		if(!MessageCache.isCached(event.getMessageId())){
			return;
		}
		final var messageId = event.getMessageId();
		MessageCache.cacheMessage(messageId, new MessageData(event.getMessage()));
		MessageCache.setLastEditedMessage(event.getChannel().getId(), messageId);
	}

	@Override
	public void onGuildMessageDelete(GuildMessageDeleteEvent event){
		CommandResponseCache.deleteCommandResponse(event.getChannel(), event.getMessageId());
		Database.removeReactiveMessage(event.getGuild().getId(), event.getMessageId());
		if(!MessageCache.isCached(event.getMessageId())){
			return;
		}
		MessageCache.setLastDeletedMessage(event.getChannel().getId(), event.getMessageId());
		MessageCache.uncacheEditedMessage(event.getChannel().getId(), event.getMessageId());
	}

	@Override
	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event){
		if(event.getMember().getUser().isBot()){
			return;
		}

		ReactiveMessage reactiveMessage = ReactiveMessageCache.getReactiveMessage(event.getGuild(), event.getMessageId());
		if(reactiveMessage != null){
			if(reactiveMessage.allowed.equals("-1") || reactiveMessage.allowed.equals(event.getUserId())){
				CommandManager.getCommands().get(reactiveMessage.command).reactionAdd(reactiveMessage, event);
			}
			else{
				event.getReaction().removeReaction(event.getUser()).queue();
			}
		}
	}

}
