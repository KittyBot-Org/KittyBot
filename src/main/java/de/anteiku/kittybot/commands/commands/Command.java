package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import de.anteiku.kittybot.commands.CommandContext;

public class Command extends ACommand{

	public static String COMMAND = "";
	public static String USAGE = "";
	public static String DESCRIPTION = "";
	protected static String[] ALIAS = {};

	public Command(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
		this.main = main;
	}

	@Override
	public void run(CommandContext ctx){
		sendAnswer(ctx, "this is my command template uwu");
	}


}
