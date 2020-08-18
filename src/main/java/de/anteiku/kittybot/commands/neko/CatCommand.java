package de.anteiku.kittybot.commands.neko;

import de.anteiku.kittybot.objects.Emotes;
import de.anteiku.kittybot.objects.command.ACommand;
import de.anteiku.kittybot.objects.command.Category;
import de.anteiku.kittybot.objects.command.CommandContext;

public class CatCommand extends ACommand{

	public static final String COMMAND = "cat";
	public static final String USAGE = "cat";
	public static final String DESCRIPTION = "Sends a random cat";
	protected static final String[] ALIAS = {"kitty", "katze"};
	protected static final Category CATEGORY = Category.NEKO;

	public CatCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIAS, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		image(ctx, getNeko("meow")).queue(message -> message.addReaction(Emotes.CAT.get()).queue());
	}

}
