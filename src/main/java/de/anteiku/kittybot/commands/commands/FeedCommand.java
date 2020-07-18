package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import de.anteiku.kittybot.commands.CommandContext;

public class FeedCommand extends ACommand{

	public static String COMMAND = "feed";
	public static String USAGE = "feed <@user, ...>";
	public static String DESCRIPTION = "Feeds a user";
	protected static String[] ALIAS = {"füttern"};

	public FeedCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
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
