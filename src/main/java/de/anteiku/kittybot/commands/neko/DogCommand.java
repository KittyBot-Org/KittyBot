package de.anteiku.kittybot.commands.neko;

import de.anteiku.kittybot.command.ACommand;
import de.anteiku.kittybot.command.Category;
import de.anteiku.kittybot.command.CommandContext;
import de.anteiku.kittybot.objects.Emojis;

public class DogCommand extends ACommand{

	public static final String COMMAND = "dog";
	public static final String USAGE = "dog";
	public static final String DESCRIPTION = "Sends a random dog";
	protected static final String[] ALIASES = {"hund", "doggo"};
	protected static final Category CATEGORY = Category.NEKO;

	public DogCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		image(ctx, getNeko("woof")).queue(message -> message.addReaction(Emojis.DOG).queue());
	}

}
