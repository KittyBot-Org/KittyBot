package de.anteiku.kittybot.commands.info;

import de.anteiku.kittybot.objects.command.ACommand;
import de.anteiku.kittybot.objects.command.CommandContext;

public class TestCommand extends ACommand{

	public static final String COMMAND = "test";
	public static final String USAGE = "test";
	public static final String DESCRIPTION = "Only for testing weird stuff";
	protected static final String[] ALIAS = {};

	public TestCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIAS);
	}

	@Override
	public void run(CommandContext ctx){
		sendAnswer(ctx, "Test command working!");
	}

}
