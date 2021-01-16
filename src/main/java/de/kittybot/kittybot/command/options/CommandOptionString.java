package de.kittybot.kittybot.command.options;

import de.kittybot.kittybot.command.application.CommandOptionType;

public class CommandOptionString extends CommandOption{

	public CommandOptionString(String name, String description){
		super(CommandOptionType.STRING, name, description);
	}

}
