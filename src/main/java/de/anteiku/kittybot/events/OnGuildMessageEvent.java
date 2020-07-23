package de.anteiku.kittybot.events;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.database.Database;
import de.anteiku.kittybot.objects.Cache;
import de.anteiku.kittybot.objects.Emotes;
import de.anteiku.kittybot.objects.ReactiveMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.Instant;

public class OnGuildMessageEvent extends ListenerAdapter{

	private final KittyBot main;

	public OnGuildMessageEvent(KittyBot main){
		this.main = main;
	}

	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event){
		if(event.getAuthor().isBot()){
			return;
		}
		if(!KittyBot.commandManager.checkCommands(event)){
			if(event.getMessage().getMentionedUsers().size() == 1 && event.getMessage().getMentionedUsers().get(0).getId().equals(event.getJDA().getSelfUser().getId())){
				event.getMessage().addReaction(Emotes.QUESTION.get()).queue();
				event.getChannel().sendMessage(new EmbedBuilder().setColor(Color.ORANGE).setTitle("Do you need help?").setDescription("My current prefix for this guild is `" + Database.getCommandPrefix(event.getGuild().getId()) + "`\n" + "If you don't like my prefix you can ping me directly!\n" + "To have a look at all my commands use `" + Database.getCommandPrefix(event.getGuild().getId()) + "cmds`\n" + "To get help use`" + Database.getCommandPrefix(event.getGuild().getId()) + "help`").setThumbnail(event.getJDA().getSelfUser().getAvatarUrl()).setFooter(event.getMember().getEffectiveName(), event.getAuthor().getEffectiveAvatarUrl()).setTimestamp(Instant.now()).build()).queue();
			}
		}
	}

	@Override
	public void onGuildMessageDelete(GuildMessageDeleteEvent event){
		Cache.deleteCommandResponse(event.getChannel(), event.getMessageId());
	}

	@Override
	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event){
		if(event.getMember().getUser().isBot()){
			return;
		}

		ReactiveMessage reactiveMessage = Cache.getReactiveMessage(event.getGuild(), event.getMessageId());
		if(reactiveMessage != null){
			if(reactiveMessage.allowed.equals("-1") || reactiveMessage.allowed.equals(event.getUserId())){
				KittyBot.commandManager.commands.get(reactiveMessage.command).reactionAdd(reactiveMessage, event);
			}
			else{
				event.getReaction().removeReaction(event.getUser()).queue();
			}
		}
	}

}
