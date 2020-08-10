package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.commands.ACommand;
import de.anteiku.kittybot.commands.CommandContext;
import de.anteiku.kittybot.objects.Emotes;

public class DogCommand extends ACommand{

	public static final String COMMAND = "dog";
	public static final String USAGE = "dog";
	public static final String DESCRIPTION = "Sends a random dog";
	protected static final String[] ALIAS = {"hund", "doggo"};

	public DogCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIAS);
	}

	@Override
	public void run(CommandContext ctx){
		image(ctx, getNeko("woof")).queue(message -> message.addReaction(Emotes.DOG.get()).queue());
	}

}
