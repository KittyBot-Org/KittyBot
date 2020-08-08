package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import de.anteiku.kittybot.commands.CommandContext;

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
