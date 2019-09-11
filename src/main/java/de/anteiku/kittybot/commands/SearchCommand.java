package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.API;
import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.Logger;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

public class SearchCommand extends Command{
	
	public static String COMMAND = "search";
	public static String USAGE = "search <search>";
	public static String DESCRIPTION = "Searches for some images";
	public static String[] ALIAS = {"s", "suche"};
	
	public SearchCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
	}
	
	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		if(args.length < 1){
			sendUsage(event.getChannel());
			return;
		}
		try{
			String search = String.join("%20", args);
			String url = API.getRandomImage(search);
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle(String.join(" ", args));
			eb.setColor(Color.orange);
			eb.setImage(url);
			event.getChannel().sendMessage(eb.build()).queue();
		}
		catch(NullPointerException | IllegalArgumentException e){
			sendError(event.getChannel(), "I found nothing for '" + String.join(" ", args) + "'!");
			Logger.error(e);
		}
	}
	
}
