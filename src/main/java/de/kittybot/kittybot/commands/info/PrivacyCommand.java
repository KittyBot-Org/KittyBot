package de.kittybot.kittybot.commands.info;

import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;

public class PrivacyCommand extends ACommand{

	public static final String COMMAND = "privacy";
	public static final String USAGE = "privacy";
	public static final String DESCRIPTION = "Gives you a link to our privacy policy";
	protected static final String[] ALIASES = {};
	protected static final Category CATEGORY = Category.INFORMATIVE;

	public PrivacyCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		sendAnswer(ctx, "You can find our privacy policy here: https://kittybot.de/privacy");
	}

}
