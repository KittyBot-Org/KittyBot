package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class QuokkaCommand extends ACommand{
	
	public static String COMMAND = "quokka";
	public static String USAGE = "quokka";
	public static String DESCRIPTION = "Sends a random quokka";
	protected static String[] ALIAS = {};
	
	public QuokkaCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
	}
	
	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		try{
			sendUnsplashImage(event.getMessage(), "quokka");
		}
		catch(NullPointerException e){
			sendError(event.getChannel(), "No quokka found!");
		}
	}
	
}
