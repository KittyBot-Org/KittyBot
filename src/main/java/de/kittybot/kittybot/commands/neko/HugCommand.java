package de.kittybot.kittybot.commands.neko;

import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;

public class HugCommand extends ACommand{

	public static final String COMMAND = "hug";
	public static final String USAGE = "hug <@user, ...>";
	public static final String DESCRIPTION = "Sends a hug to a user";
	protected static final String[] ALIASES = {"umarme"};
	protected static final Category CATEGORY = Category.NEKO;

	public HugCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		if(ctx.getArgs().length == 0){
			sendUsage(ctx);
			return;
		}
		sendReactionImage(ctx, false, "hug", "gif", "hugs");
	}

}
