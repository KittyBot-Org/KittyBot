package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import de.anteiku.kittybot.commands.CommandContext;

public class CuddleCommand extends ACommand{

	public static String COMMAND = "cuddle";
	public static String USAGE = "cuddle <@user, ...>";
	public static String DESCRIPTION = "Cuddles a user";
	protected static String[] ALIAS = {"knuddel"};

	public CuddleCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
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
