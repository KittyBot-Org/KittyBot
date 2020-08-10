package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.commands.ACommand;
import de.anteiku.kittybot.commands.CommandContext;

public class PatCommand extends ACommand{

	public static final String COMMAND = "pat";
	public static final String USAGE = "pat <@user, ...>";
	public static final String DESCRIPTION = "Pats to a user";
	protected static final String[] ALIAS = {"tätschel"};

	public PatCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIAS);
	}

	@Override
	public void run(CommandContext ctx){
		if(ctx.getArgs().length == 0){
			sendUsage(ctx);
			return;
		}
		sendReactionImage(ctx, "pat", "pats");
	}

}
