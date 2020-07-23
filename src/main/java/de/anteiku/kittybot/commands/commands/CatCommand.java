package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import de.anteiku.kittybot.commands.CommandContext;
import de.anteiku.kittybot.objects.Emotes;

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
	public void run(CommandContext ctx){
		image(ctx, getNeko("meow")).queue(message -> message.addReaction(Emotes.CAT.get()).queue());
	}

}
