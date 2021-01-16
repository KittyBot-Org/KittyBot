package de.kittybot.kittybot.command.options;

import de.kittybot.kittybot.command.application.CommandOptionType;
import de.kittybot.kittybot.command.application.RunnableCommand;

public abstract class SubCommand extends CommandOption implements RunnableCommand{

	public SubCommand(String name, String description){
		super(CommandOptionType.SUB_COMMAND, name, description);
	}

}
