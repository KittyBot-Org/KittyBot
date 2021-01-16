package de.kittybot.kittybot.command.options;

import de.kittybot.kittybot.command.application.CommandOptionType;

public class CommandOptionUser extends CommandOption{

	public CommandOptionUser(String name, String description){
		super(CommandOptionType.USER, name, description);
	}

}
