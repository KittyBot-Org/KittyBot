package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.commands.ACommand;
import de.anteiku.kittybot.commands.CommandContext;

public class HugCommand extends ACommand{

	public static final String COMMAND = "hug";
	public static final String USAGE = "hug <@user, ...>";
	public static final String DESCRIPTION = "Sends a hug to a user";
	protected static final String[] ALIAS = {"umarme"};

	public HugCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIAS);
	}

	@Override
	public void run(CommandContext ctx){
		if(ctx.getArgs().length == 0){
			sendUsage(ctx);
			return;
		}
		sendReactionImage(ctx, "hug", "hugs");
	}

}
