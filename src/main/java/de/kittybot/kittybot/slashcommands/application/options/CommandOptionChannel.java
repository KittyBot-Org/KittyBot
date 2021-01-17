package de.kittybot.kittybot.slashcommands.application.options;

import de.kittybot.kittybot.slashcommands.application.CommandOptionType;

public class CommandOptionChannel extends CommandOption{

	public CommandOptionChannel(String name, String description){
		super(CommandOptionType.CHANNEL, name, description);
	}

}
