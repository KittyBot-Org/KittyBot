package de.anteiku.kittybot.events;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.utils.Emotes;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class OnGuildMessageReceivedEvent extends ListenerAdapter{
	
	private final KittyBot main;
	
	public OnGuildMessageReceivedEvent(KittyBot main){
		this.main = main;
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event){
		if(event.getAuthor().isBot() || event.getAuthor().isFake()){
			return;
		}
		if(!main.commandManager.checkCommands(event)){
			if(event.getMessage().getMentionedUsers().size() == 1 && event.getMessage().getMentionedUsers().get(0).getId().equals(main.jda.getSelfUser().getId())) {
				event.getMessage().addReaction(Emotes.QUESTION.get()).queue();
				event.getChannel().sendMessage(
					new EmbedBuilder()
						.setColor(Color.ORANGE)
						.setTitle("Do you need help?")
						.setDescription("Do you want to know what my prefix is?\n" +
							"It is `" + main.database.getCommandPrefix(event.getGuild().getId()) + "`\n" +
							"If you need more help run `" + main.database.getCommandPrefix(event.getGuild().getId()) + "help` or `" + main.database.getCommandPrefix(event.getGuild().getId()) + "commands`" + Emotes.KITTY_BLINK.get())
						.build()
				).queue();
			}
		}
	}
	
}
