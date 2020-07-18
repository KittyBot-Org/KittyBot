package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import de.anteiku.kittybot.commands.CommandContext;

public class TestCommand extends ACommand{

	public static String COMMAND = "test";
	public static String USAGE = "test";
	public static String DESCRIPTION = "Only for testing weird stuff";
	protected static String[] ALIAS = {};

	public TestCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
	}

	@Override
	public void run(CommandContext ctx){
		sendAnswer(ctx, "Test command working!");
	}

}
