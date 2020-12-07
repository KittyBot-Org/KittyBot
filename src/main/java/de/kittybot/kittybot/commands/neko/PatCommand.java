package de.kittybot.kittybot.commands.neko;

import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;

public class PatCommand extends ACommand{

	public static final String COMMAND = "pat";
	public static final String USAGE = "pat <@user, ...>";
	public static final String DESCRIPTION = "Pats to a user";
	protected static final String[] ALIASES = {"tätschel"};
	protected static final Category CATEGORY = Category.NEKO;

	public PatCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		sendReactionImage(ctx, false, "pat", "gif", "pats");
	}

}
