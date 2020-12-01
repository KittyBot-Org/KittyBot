package de.kittybot.kittybot.commands.neko;

import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;

public class BiteCommand extends ACommand{

	public static final String COMMAND = "bite";
	public static final String USAGE = "bite <@user, ...>";
	public static final String DESCRIPTION = "Bites a user";
	protected static final String[] ALIASES = {"beiß"};
	protected static final Category CATEGORY = Category.NEKO;

	public BiteCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		if(ctx.getArgs().length == 0){
			sendUsage(ctx);
			return;
		}
		sendReactionImage(ctx, false, "bite", "gif", "bites");
	}

}
