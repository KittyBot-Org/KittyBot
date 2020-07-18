package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import de.anteiku.kittybot.commands.CommandContext;

public class TickleCommand extends ACommand{

	public static String COMMAND = "tickle";
	public static String USAGE = "tickle <@user, ...>";
	public static String DESCRIPTION = "Tickles a user";
	protected static String[] ALIAS = {"kitzel"};

	public TickleCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
	}

	@Override
	public void run(CommandContext ctx){
		if(ctx.getArgs().length == 0){
			sendUsage(ctx);
			return;
		}
		sendReactionImage(ctx, "tickle", "tickles");
	}

}
