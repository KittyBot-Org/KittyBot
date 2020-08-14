package de.anteiku.kittybot.commands.neko;

import de.anteiku.kittybot.objects.command.ACommand;
import de.anteiku.kittybot.objects.command.CommandContext;

public class KissCommand extends ACommand{

	public static final String COMMAND = "kiss";
	public static final String USAGE = "kiss <@user, ...>";
	public static final String DESCRIPTION = "Sends a kiss to a user";
	protected static final String[] ALIAS = {"küss"};

	public KissCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIAS);
	}

	@Override
	public void run(CommandContext ctx){
		if(ctx.getArgs().length == 0){
			sendUsage(ctx);
			return;
		}
		sendReactionImage(ctx, "kiss", "kisses");
	}

}
