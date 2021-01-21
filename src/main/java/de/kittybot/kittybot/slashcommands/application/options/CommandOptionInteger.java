package de.kittybot.kittybot.slashcommands.application.options;

import de.kittybot.kittybot.slashcommands.application.CommandOption;
import de.kittybot.kittybot.slashcommands.application.CommandOptionType;

public class CommandOptionInteger extends CommandOption{

	public CommandOptionInteger(String name, String description){
		super(CommandOptionType.INTEGER, name, description);
	}

}
