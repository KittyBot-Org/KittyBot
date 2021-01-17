package de.kittybot.kittybot.slashcommands.application.options;

import de.kittybot.kittybot.slashcommands.application.CommandOptionType;

public class SubCommandGroup extends CommandOption{

	public SubCommandGroup(String name, String description){
		super(CommandOptionType.SUB_COMMAND_GROUP, name, description);
	}

}
