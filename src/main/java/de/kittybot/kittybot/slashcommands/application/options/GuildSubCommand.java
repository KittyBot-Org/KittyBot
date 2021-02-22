package de.kittybot.kittybot.slashcommands.application.options;

import de.kittybot.kittybot.slashcommands.application.RunnableGuildCommand;

public abstract class GuildSubCommand extends SubBaseCommand implements RunnableGuildCommand{

	public GuildSubCommand(String name, String description){
		super(name, description);
	}

}
