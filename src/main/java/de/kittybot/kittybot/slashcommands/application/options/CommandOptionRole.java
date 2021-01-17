package de.kittybot.kittybot.slashcommands.application.options;

import de.kittybot.kittybot.slashcommands.application.CommandOptionType;

public class CommandOptionRole extends CommandOption{

	public CommandOptionRole(String name, String description){
		super(CommandOptionType.ROLE, name, description);
	}

}
