package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.commands.ACommand;
import de.anteiku.kittybot.commands.CommandContext;

public class CuddleCommand extends ACommand{

	public static final String COMMAND = "cuddle";
	public static final String USAGE = "cuddle <@user, ...>";
	public static final String DESCRIPTION = "Cuddles a user";
	protected static final String[] ALIAS = {"knuddel"};

	public CuddleCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIAS);
	}

	@Override
	public void run(CommandContext ctx){
		if(ctx.getArgs().length == 0){
			sendUsage(ctx);
			return;
		}
		sendReactionImage(ctx, "cuddle", "cuddles");
	}

}
