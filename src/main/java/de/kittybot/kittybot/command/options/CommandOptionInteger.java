package de.kittybot.kittybot.command.options;

import de.kittybot.kittybot.command.application.CommandOptionType;

public class CommandOptionInteger extends CommandOption{

	public CommandOptionInteger(String name, String description){
		super(CommandOptionType.INTEGER, name, description);
	}

}
