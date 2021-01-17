package de.kittybot.kittybot.slashcommands.application.options;

import de.kittybot.kittybot.slashcommands.application.CommandOptionType;

public class CommandOptionUser extends CommandOption{

	public CommandOptionUser(String name, String description){
		super(CommandOptionType.USER, name, description);
	}

}
