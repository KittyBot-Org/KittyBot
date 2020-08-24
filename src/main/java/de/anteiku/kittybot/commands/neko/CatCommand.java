package de.anteiku.kittybot.commands.neko;

import de.anteiku.kittybot.objects.Emojis;
import de.anteiku.kittybot.command.ACommand;
import de.anteiku.kittybot.command.Category;
import de.anteiku.kittybot.command.CommandContext;

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
		image(ctx, getNeko("meow")).queue(message -> message.addReaction(Emojis.CAT).queue());
	}

}
