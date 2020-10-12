package de.kittybot.kittybot.commands.neko;

import de.kittybot.kittybot.objects.Emojis;
import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;

public class CatCommand extends ACommand{

	public static final String COMMAND = "cat";
	public static final String USAGE = "cat";
	public static final String DESCRIPTION = "Sends a random cat";
	protected static final String[] ALIASES = {"kitty", "katze"};
	protected static final Category CATEGORY = Category.NEKO;

	public CatCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		ACommand.image(ctx, ACommand.getNeko("meow")).queue(message -> message.addReaction(Emojis.CAT).queue());
	}

}
