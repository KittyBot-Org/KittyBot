package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import de.anteiku.kittybot.commands.CommandContext;

public class SlapCommand extends ACommand{

	public static final String COMMAND = "slap";
	public static final String USAGE = "slap <@user, ...>";
	public static final String DESCRIPTION = "Slaps a user";
	protected static final String[] ALIAS = {"schlag"};

	public SlapCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIAS);
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
