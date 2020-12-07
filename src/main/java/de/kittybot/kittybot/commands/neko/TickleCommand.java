package de.kittybot.kittybot.commands.neko;

import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;

public class TickleCommand extends ACommand{

	public static final String COMMAND = "tickle";
	public static final String USAGE = "tickle <@user, ...>";
	public static final String DESCRIPTION = "Tickles a user";
	protected static final String[] ALIASES = {"kitzel"};
	protected static final Category CATEGORY = Category.NEKO;

	public TickleCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		sendReactionImage(ctx, false, "tickle", "gif", "tickles");
	}

}
