package de.kittybot.kittybot.commands.neko;

import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;
import de.kittybot.kittybot.objects.requests.Requester;

public class BlushCommand extends ACommand{

	public static final String COMMAND = "blush";
	public static final String USAGE = "blush";
	public static final String DESCRIPTION = "Blushes";
	protected static final String[] ALIASES = {"beiß"};
	protected static final Category CATEGORY = Category.NEKO;

	public BlushCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		queue(ctx, image(ctx, Requester.getNeko(false, "blush", "gif")));
	}

}
