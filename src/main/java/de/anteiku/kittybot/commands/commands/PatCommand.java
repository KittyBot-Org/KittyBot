package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import de.anteiku.kittybot.commands.CommandContext;

public class PatCommand extends ACommand{

	public static String COMMAND = "pat";
	public static String USAGE = "pat <@user, ...>";
	public static String DESCRIPTION = "Pats to a user";
	protected static String[] ALIAS = {"tätschel"};

	public PatCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
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
