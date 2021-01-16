package de.kittybot.kittybot.command.options;

import de.kittybot.kittybot.command.application.CommandOptionType;

public class CommandOptionRole extends CommandOption{

	public CommandOptionRole(String name, String description){
		super(CommandOptionType.ROLE, name, description);
	}

}
