package de.kittybot.kittybot.slashcommands.application.options;

import de.kittybot.kittybot.slashcommands.application.CommandOption;
import de.kittybot.kittybot.slashcommands.application.CommandOptionType;

public class CommandOptionString extends CommandOption{

	public CommandOptionString(String name, String description){
		super(CommandOptionType.STRING, name, description);
	}

}
