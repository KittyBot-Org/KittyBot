package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.commands.ACommand;
import de.anteiku.kittybot.commands.CommandContext;
import de.anteiku.kittybot.objects.Emotes;

public class CatCommand extends ACommand{

	public static final String COMMAND = "cat";
	public static final String USAGE = "cat";
	public static final String DESCRIPTION = "Sends a random cat";
	protected static final String[] ALIAS = {"kitty", "katze"};

	public CatCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIAS);
	}

	@Override
	public void run(CommandContext ctx){
		image(ctx, getNeko("meow")).queue(message -> message.addReaction(Emotes.CAT.get()).queue());
	}

}
