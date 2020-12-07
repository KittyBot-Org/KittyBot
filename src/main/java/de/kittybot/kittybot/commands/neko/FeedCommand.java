package de.kittybot.kittybot.commands.neko;

import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;

public class FeedCommand extends ACommand{

	public static final String COMMAND = "feed";
	public static final String USAGE = "feed <@user, ...>";
	public static final String DESCRIPTION = "Feeds a user";
	protected static final String[] ALIASES = {"füttern"};
	protected static final Category CATEGORY = Category.NEKO;

	public FeedCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		sendReactionImage(ctx, false, "feed", "gif", "feeds");
	}

}
