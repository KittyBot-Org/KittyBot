package de.kittybot.kittybot.commands.neko;

import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;
import de.kittybot.kittybot.objects.requests.Requester;

public class KitsuneCommand extends ACommand{

	public static final String COMMAND = "kitsune";
	public static final String USAGE = "kitsune";
	public static final String DESCRIPTION = "Sends a Kitsune";
	protected static final String[] ALIASES = {};
	protected static final Category CATEGORY = Category.NEKO;

	public KitsuneCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		queue(ctx, image(ctx, Requester.getNeko(false, "kitsune", "img")));
	}

}
