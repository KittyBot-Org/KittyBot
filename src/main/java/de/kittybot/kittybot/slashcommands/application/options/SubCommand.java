package de.kittybot.kittybot.slashcommands.application.options;

import de.kittybot.kittybot.slashcommands.application.CommandOptionType;
import de.kittybot.kittybot.slashcommands.application.RunnableCommand;

public abstract class SubCommand extends CommandOption implements RunnableCommand{

	public SubCommand(String name, String description){
		super(CommandOptionType.SUB_COMMAND, name, description);
	}

}
