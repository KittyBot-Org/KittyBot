package de.kittybot.kittybot.commands.neko;

import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;
import de.kittybot.kittybot.objects.requests.Requester;

public class FluffCommand extends ACommand{

	public static final String COMMAND = "fluff";
	public static final String USAGE = "fluff";
	public static final String DESCRIPTION = "Fluffs";
	protected static final String[] ALIASES = {};
	protected static final Category CATEGORY = Category.NEKO;

	public FluffCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		queue(ctx, image(ctx, Requester.getNeko(false, "fluff", "gif")));
	}

}
