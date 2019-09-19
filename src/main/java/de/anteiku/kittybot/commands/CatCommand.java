package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.API;
import de.anteiku.kittybot.Emotes;
import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.Logger;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

public class CatCommand extends Command{

	public static String COMMAND = "cat";
	public static String USAGE = "cat";
	public static String DESCRIPTION = "Sends a random cat";
	public static String[] ALIAS = {"kitty", "katze"};

	public CatCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
		this.main = main;
	}

	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		try{
			String url = getNeko("meow");
			EmbedBuilder eb = new EmbedBuilder();
			eb.setColor(Color.ORANGE);
			eb.setImage(url);
			Message message = sendAnswer(event.getMessage(), eb.build());
			message.addReaction(Emotes.CAT.get()).queue();
		}
		catch(Exception e){
			sendError(event.getMessage(), "No cat found!");
			Logger.error(e);
		}
	}

}
