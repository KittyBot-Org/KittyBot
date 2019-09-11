package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.API;
import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.Logger;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

public class KurapikaCommand extends Command{
	
	public static String COMMAND = "kurapika";
	public static String USAGE = "kurapika";
	public static String DESCRIPTION = "Sends a random Kurapika image";
	public static String[] ALIAS = {"kura"};
	
	public KurapikaCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
	}
	
	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		try{
			String url = API.getRandomAnteikuImage("kurapika");
			EmbedBuilder eb = new EmbedBuilder();
			eb.setColor(Color.ORANGE);
			eb.setImage(url);
			event.getChannel().sendMessage(eb.build()).queue();
		}
		catch(NullPointerException e){
			sendError(event.getChannel(), "No Kurapika found!");
			Logger.error(e);
		}
	}
	
}
