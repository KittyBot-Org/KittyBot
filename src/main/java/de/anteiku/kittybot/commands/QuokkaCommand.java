package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.API;
import de.anteiku.kittybot.Colors;
import de.anteiku.kittybot.KittyBot;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class QuokkaCommand extends Command{
	
	public static String COMMAND = "quokka";
	public static String USAGE = "quokka";
	public static String DESCRIPTION = "Sends a random quokka";
	public static String[] ALIAS = {};
	
	public QuokkaCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
	}
	
	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		try{
			String url = API.getMeme("quokka");
			EmbedBuilder eb = new EmbedBuilder();
			eb.setColor(Colors.BROWN);
			eb.setImage(url);
			event.getChannel().sendMessage(eb.build()).queue();
		}
		catch(NullPointerException e){
			sendError(event.getChannel(), "No quokka found!");
		}
	}
	
}
