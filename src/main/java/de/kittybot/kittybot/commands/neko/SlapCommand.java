package de.kittybot.kittybot.commands.neko;

import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;

public class SlapCommand extends ACommand{

	public static final String COMMAND = "slap";
	public static final String USAGE = "slap <@user, ...>";
	public static final String DESCRIPTION = "Slaps a user";
	protected static final String[] ALIASES = {"schlag"};
	protected static final Category CATEGORY = Category.NEKO;

	public SlapCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		if(ctx.getArgs().length == 0){
			sendUsage(ctx);
			return;
		}
		sendReactionImage(ctx, "slap", "slaps");
	}

}
