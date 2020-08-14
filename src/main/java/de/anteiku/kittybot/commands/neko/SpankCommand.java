package de.anteiku.kittybot.commands.neko;

import de.anteiku.kittybot.objects.command.ACommand;
import de.anteiku.kittybot.objects.command.CommandContext;

public class SpankCommand extends ACommand{

	public static final String COMMAND = "spank";
	public static final String USAGE = "spank <@user, ...>";
	public static final String DESCRIPTION = "Spanks a user";
	protected static final String[] ALIAS = {};

	public SpankCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIAS);
	}

	@Override
	public void run(CommandContext ctx){
		if(!ctx.getChannel().isNSFW()){
			sendError(ctx, "Sorry but this command can only be used in nsfw channels");
			return;
		}
		if(ctx.getArgs().length == 0){
			sendUsage(ctx);
			return;
		}
		sendReactionImage(ctx, "spank", "spanks");
	}

}
