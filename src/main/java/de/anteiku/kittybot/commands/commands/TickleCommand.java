package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import de.anteiku.kittybot.commands.CommandContext;

public class TickleCommand extends ACommand{

	public static final String COMMAND = "tickle";
	public static final String USAGE = "tickle <@user, ...>";
	public static final String DESCRIPTION = "Tickles a user";
	protected static final String[] ALIAS = {"kitzel"};

	public TickleCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIAS);
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
