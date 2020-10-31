package de.kittybot.kittybot.events;

import de.kittybot.kittybot.cache.CommandResponseCache;
import de.kittybot.kittybot.cache.GuildSettingsCache;
import de.kittybot.kittybot.cache.MessageCache;
import de.kittybot.kittybot.cache.ReactiveMessageCache;
import de.kittybot.kittybot.database.Database;
import de.kittybot.kittybot.objects.Emojis;
import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.CommandManager;
import de.kittybot.kittybot.objects.data.MessageData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class OnGuildMessageEvent extends ListenerAdapter{

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event){
		var content = event.getMessage().getContentRaw();
		if(!content.isEmpty()){
			MessageCache.cacheMessage(event.getMessageId(), new MessageData(event.getMessage()));
		}
		if(event.getAuthor().isBot() || CommandManager.checkCommands(event) || event.getMessage().getMentionedUsers().size() != 1 || !event.getMessage().getMentionedUsers().get(0).getId().equals(event.getJDA().getSelfUser().getId())){
			return;
		}
		event.getMessage().addReaction(Emojis.QUESTION).queue();
		if(!event.getChannel().canTalk()){
			return;
		}
		var prefix = GuildSettingsCache.getCommandPrefix(event.getGuild().getId());
		ACommand.sendAnswer(event.getChannel(), event.getMember(), new EmbedBuilder().setColor(Color.ORANGE)
				.setTitle("Do you need help?")
				.setDescription("My current prefix for this guild is `" + prefix + "`\n"
						+ "If you don't like my prefix you can ping me directly!\n" + "To have a look at all my commands use `" + prefix
						+ "cmds`\n" + "To get help use `" + prefix + "help`")
				.setThumbnail(event.getJDA().getSelfUser().getAvatarUrl())
		);
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
		var reactiveMessage = ReactiveMessageCache.getReactiveMessage(event.getGuild(), event.getMessageId());
		if(reactiveMessage == null){
			return;
		}
		if(reactiveMessage.allowed.equals("-1") || reactiveMessage.allowed.equals(event.getUserId())){
			CommandManager.getCommands().get(reactiveMessage.command).reactionAdd(reactiveMessage, event);
			return;
		}
		event.getReaction().removeReaction(event.getUser()).queue();
	}

}
