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
		try{
			String url = API.getRandomImage("turtle");
			EmbedBuilder eb = new EmbedBuilder();
			eb.setColor(Color.GREEN);
			eb.setImage(url);
			Message message = event.getChannel().sendMessage(eb.build()).complete();
			message.addReaction(Emotes.TURTLE).queue();
		}
		catch(NullPointerException e){
			sendError(event.getChannel(), "No turtle found!");
		}
	}
	
}
