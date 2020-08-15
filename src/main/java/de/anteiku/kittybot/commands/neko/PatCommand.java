package de.anteiku.kittybot.commands.neko;

import de.anteiku.kittybot.objects.command.ACommand;
import de.anteiku.kittybot.objects.command.CommandContext;

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