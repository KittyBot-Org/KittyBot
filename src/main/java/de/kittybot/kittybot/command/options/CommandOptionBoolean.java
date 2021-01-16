package de.kittybot.kittybot.command.options;

import de.kittybot.kittybot.command.application.CommandOptionType;

public class CommandOptionBoolean extends CommandOption{

	public CommandOptionBoolean(String name, String description){
		super(CommandOptionType.BOOLEAN, name, description);
	}

}
