package de.kittybot.kittybot.commands.neko;

import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;

public class PokeCommand extends ACommand{

	public static final String COMMAND = "poke";
	public static final String USAGE = "poke <@user, ...>";
	public static final String DESCRIPTION = "Pokes a user";
	protected static final String[] ALIASES = {"stups"};
	protected static final Category CATEGORY = Category.NEKO;

	public PokeCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		if(ctx.getArgs().length == 0){
			sendUsage(ctx);
			return;
		}
		sendReactionImage(ctx, "poke", "pokes");
	}

}
