package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.utils.Colors;
import de.anteiku.kittybot.utils.Emotes;
import de.anteiku.kittybot.utils.Logger;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class DogCommand extends ACommand{

	public static String COMMAND = "dog";
	public static String USAGE = "dog";
	public static String DESCRIPTION = "Sends a random dog";
	protected static String[] ALIAS = {"hund", "doggo"};

	public DogCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
	}

	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		try{
			String url = getNeko("woof");
			EmbedBuilder eb = new EmbedBuilder();
			eb.setColor(Colors.BROWN);
			eb.setImage(url);
			Message message = event.getChannel().sendMessage(eb.build()).complete();
			message.addReaction(Emotes.DOG.get()).queue();
		}
		catch(Exception e){
			sendError(event.getChannel(), "No dog found!");
			Logger.error(e);
		}
	}

}
