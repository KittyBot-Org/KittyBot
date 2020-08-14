package de.anteiku.kittybot.commands.neko;

import de.anteiku.kittybot.objects.command.ACommand;
import de.anteiku.kittybot.objects.command.CommandContext;

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
