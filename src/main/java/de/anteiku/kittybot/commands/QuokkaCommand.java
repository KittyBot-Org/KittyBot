package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.utils.Emotes;
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
		sendUnsplashImage(event.getMessage(), "quokka").addReaction(Emotes.QUOKKA.get()).queue();
	}
	
}
