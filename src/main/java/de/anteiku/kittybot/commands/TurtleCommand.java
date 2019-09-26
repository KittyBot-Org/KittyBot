package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.utils.Emotes;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class TurtleCommand extends ACommand{
	
	public static String COMMAND = "turtle";
	public static String USAGE = "turtle";
	public static String DESCRIPTION = "Sends a random turtle";
	protected static String[] ALIAS = {"schildkröte"};
	
	public TurtleCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
	}
	
	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		sendUnsplashImage(event.getMessage(), "turtle").addReaction(Emotes.TURTLE.get()).queue();
	}
	
}
