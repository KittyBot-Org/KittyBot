package de.kittybot.kittybot.commands.dev;

import de.kittybot.kittybot.commands.dev.dev.*;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;

@SuppressWarnings("unused")
public class DevCommand extends Command{

	public DevCommand(){
		super("dev", "Collection of some dev commands", Category.DEV);
		addOptions(
			new DeployCommand(),
			new RemoveCommand(),
			new TestCommand(),
			new PresenceCommand()
		);
	}

}
