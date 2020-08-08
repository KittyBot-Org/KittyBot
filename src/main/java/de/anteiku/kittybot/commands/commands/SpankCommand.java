package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import de.anteiku.kittybot.commands.CommandContext;

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
