package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import de.anteiku.kittybot.commands.CommandContext;

public class FeedCommand extends ACommand{

	public static final String COMMAND = "feed";
	public static final String USAGE = "feed <@user, ...>";
	public static final String DESCRIPTION = "Feeds a user";
	protected static final String[] ALIAS = {"füttern"};

	public FeedCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIAS);
	}

	@Override
	public void run(CommandContext ctx){
		if(ctx.getArgs().length == 0){
			sendUsage(ctx);
			return;
		}
		sendReactionImage(ctx, "feed", "feeds");
	}

}
