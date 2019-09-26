package de.anteiku.kittybot.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.utils.Emotes;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class CatCommand extends ACommand{

	public static String COMMAND = "cat";
	public static String USAGE = "cat";
	public static String DESCRIPTION = "Sends a random cat";
	protected static String[] ALIAS = {"kitty", "katze"};

	public CatCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
		this.main = main;
	}

	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		sendImage(event.getMessage(), getNeko("meow")).addReaction(Emotes.CAT.get()).queue();
	}

}
