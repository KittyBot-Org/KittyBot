package de.kittybot.kittybot.commands.admin;

import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;

public class TestCommand extends ACommand{

	public static final String COMMAND = "test";
	public static final String USAGE = "test";
	public static final String DESCRIPTION = "Only for testing weird stuff";
	protected static final String[] ALIASES = {};
	protected static final Category CATEGORY = Category.ADMIN;

	public TestCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		sendSuccess(ctx, "Test command working!");
	}

}
