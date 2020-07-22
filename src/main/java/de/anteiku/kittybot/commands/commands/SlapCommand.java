package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import de.anteiku.kittybot.commands.CommandContext;

public class SlapCommand extends ACommand{

	public static String COMMAND = "slap";
	public static String USAGE = "slap <@user, ...>";
	public static String DESCRIPTION = "Slaps a user";
	protected static String[] ALIAS = {"schlag"};

	public SlapCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
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
