package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.API;
import de.anteiku.kittybot.Emotes;
import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

public class TurtleCommand extends Command{
	
	public static String COMMAND = "turtle";
	public static String USAGE = "turtle";
	public static String DESCRIPTION = "Sends a random turtle";
	public static String[] ALIAS = {"schildkröte"};
	
	public TurtleCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
	}
	
	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		sendUnsplashImage(event.getMessage(), "turtle");
	}
	
}
