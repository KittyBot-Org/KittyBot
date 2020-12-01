package de.kittybot.kittybot.commands.neko;

import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;

public class LickCommand extends ACommand{

	public static final String COMMAND = "lick";
	public static final String USAGE = "lick <@user, ...>";
	public static final String DESCRIPTION = "Licks a user";
	protected static final String[] ALIASES = {"leck"};
	protected static final Category CATEGORY = Category.NEKO;

	public LickCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		if(ctx.getArgs().length == 0){
			sendUsage(ctx);
			return;
		}
		sendReactionImage(ctx, false, "lick", "gif", "licks");
	}

}
