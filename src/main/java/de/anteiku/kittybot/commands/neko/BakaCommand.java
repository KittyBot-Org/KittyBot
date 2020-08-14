package de.anteiku.kittybot.commands.neko;

import de.anteiku.kittybot.objects.command.ACommand;
import de.anteiku.kittybot.objects.command.CommandContext;

public class BakaCommand extends ACommand{

	public static final String COMMAND = "baka";
	public static final String USAGE = "baka <@user, ...>";
	public static final String DESCRIPTION = "Says baka to a user";
	protected static final String[] ALIAS = {"dummy", "dummi"};

	public BakaCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIAS);
	}

	@Override
	public void run(CommandContext ctx){
		if(ctx.getArgs().length == 0){
			sendUsage(ctx);
			return;
		}
		sendReactionImage(ctx, "baka", "said baka to");
	}

}
