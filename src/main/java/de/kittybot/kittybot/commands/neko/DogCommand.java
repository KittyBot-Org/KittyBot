package de.kittybot.kittybot.commands.neko;

import de.kittybot.kittybot.objects.Emojis;
import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;

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
		ACommand.image(ctx, ACommand.getNeko("woof")).queue(message -> message.addReaction(Emojis.DOG).queue());
	}

}
