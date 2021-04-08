package de.kittybot.kittybot.commands.dev;

import de.kittybot.kittybot.commands.dev.dev.DeployCommand;
import de.kittybot.kittybot.commands.dev.dev.PresenceCommand;
import de.kittybot.kittybot.commands.dev.dev.RemoveCommand;
import de.kittybot.kittybot.commands.dev.dev.TestCommand;
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
