package de.kittybot.kittybot.commands.info;

import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;
import de.kittybot.kittybot.utils.MessageUtils;

public class CommandsCommand extends ACommand{

	public static final String COMMAND = "commands";
	public static final String USAGE = "commands <page>";
	public static final String DESCRIPTION = "Lists all available commands";
	protected static final String[] ALIASES = {"cmds"};
	protected static final Category CATEGORY = Category.INFORMATIVE;

	public CommandsCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		sendAnswer(ctx, "You can see all available commands " + MessageUtils.maskLink("here", "https://kittybot.de/commands"));
	}

}
