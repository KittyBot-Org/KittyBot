package de.kittybot.kittybot.slashcommands.application.options;

import de.kittybot.kittybot.slashcommands.application.RunnableCommand;

public abstract class SubCommand extends SubBaseCommand implements RunnableCommand{

	public SubCommand(String name, String description){
		super(name, description, false);
	}

}
