package de.kittybot.kittybot.command.options;

import de.kittybot.kittybot.command.application.CommandOptionType;

public class SubCommandGroup extends CommandOption{

	public SubCommandGroup(String name, String description){
		super(CommandOptionType.SUB_COMMAND_GROUP, name, description);
	}

}
