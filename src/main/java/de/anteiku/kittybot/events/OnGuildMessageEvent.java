package de.anteiku.kittybot.events;

import de.anteiku.kittybot.database.Database;
import de.anteiku.kittybot.objects.Cache;
import de.anteiku.kittybot.objects.Emojis;
import de.anteiku.kittybot.objects.ReactiveMessage;
import de.anteiku.kittybot.objects.command.CommandManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.Instant;

public class OnGuildMessageEvent extends ListenerAdapter{

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event){
		if(event.getAuthor().isBot()){
			return;
		}
		if(!CommandManager.checkCommands(event)){
			if(event.getMessage().getMentionedUsers().size() == 1 && event.getMessage().getMentionedUsers().get(0).getId().equals(event.getJDA().getSelfUser().getId())){
				event.getMessage().addReaction(Emojis.QUESTION).queue();
				event.getChannel().sendMessage(new EmbedBuilder().setColor(Color.ORANGE).setTitle("Do you need help?").setDescription("My current prefix for this guild is `" + Database.getCommandPrefix(event.getGuild().getId()) + "`\n" + "If you don't like my prefix you can ping me directly!\n" + "To have a look at all my commands use `" + Database.getCommandPrefix(event.getGuild().getId()) + "cmds`\n" + "To get help use`" + Database.getCommandPrefix(event.getGuild().getId()) + "help`").setThumbnail(event.getJDA().getSelfUser().getAvatarUrl()).setFooter(event.getMember().getEffectiveName(), event.getAuthor().getEffectiveAvatarUrl()).setTimestamp(Instant.now()).build()).queue();
			}
		}
	}

	@Override
	public void onGuildMessageDelete(GuildMessageDeleteEvent event){
		Cache.deleteCommandResponse(event.getChannel(), event.getMessageId());
		Database.removeReactiveMessage(event.getGuild().getId(), event.getMessageId());
	}

	@Override
	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event){
		if(event.getMember().getUser().isBot()){
			return;
		}

		ReactiveMessage reactiveMessage = Cache.getReactiveMessage(event.getGuild(), event.getMessageId());
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
