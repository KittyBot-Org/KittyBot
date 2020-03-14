package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.utils.Emotes;
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
		sendImage(event.getMessage(), getNeko("woof")).addReaction(Emotes.DOG.get()).queue();
	}

}
