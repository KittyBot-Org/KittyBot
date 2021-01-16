package de.kittybot.kittybot.command.options;

import de.kittybot.kittybot.command.application.CommandOptionType;

public class CommandOptionChannel extends CommandOption{

	public CommandOptionChannel(String name, String description){
		super(CommandOptionType.CHANNEL, name, description);
	}

}
