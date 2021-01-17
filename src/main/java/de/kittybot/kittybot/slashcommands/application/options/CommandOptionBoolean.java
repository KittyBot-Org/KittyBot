package de.kittybot.kittybot.slashcommands.application.options;

import de.kittybot.kittybot.slashcommands.application.CommandOptionType;

public class CommandOptionBoolean extends CommandOption{

	public CommandOptionBoolean(String name, String description){
		super(CommandOptionType.BOOLEAN, name, description);
	}

}
