package de.anteiku.kittybot.events;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.utils.Emotes;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.awt.*;

public class OnGuildMessageReceivedEvent extends ListenerAdapter{
	
	private KittyBot main;
	
	public OnGuildMessageReceivedEvent(KittyBot main){
		this.main = main;
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event){
		if(event.getAuthor().isBot() || event.getAuthor().isFake()){
			return;
		}
		if(event.getMessage().getContentRaw().startsWith(main.database.getCommandPrefix(event.getGuild().getId()))){
			main.commandManager.checkCommands(event);
		}
		else if(event.getMessage().getMentionedUsers().size() == 1){
			if(event.getMessage().getMentionedUsers().get(0).getId().equals(main.jda.getSelfUser().getId())){
				event.getMessage().addReaction(Emotes.QUESTION.get()).queue();
				EmbedBuilder eb = new EmbedBuilder();
				eb.setColor(Color.ORANGE);
				eb.setTitle("Do you need help?");
				eb.setDescription("If you want to know my prefix it's `" + main.database.getCommandPrefix(event.getGuild().getId()) + "`\nIf you need more help run `.help` or `.commands`" + Emotes.TEAM_KITTY.get());
				event.getChannel().sendMessage(eb.build()).complete();
			}
		}
	}
	
}
