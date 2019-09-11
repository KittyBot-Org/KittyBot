package de.anteiku.kittybot.events;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.database.Database;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

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
	}
	
}
