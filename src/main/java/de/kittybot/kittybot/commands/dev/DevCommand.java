package de.kittybot.kittybot.commands.dev;

import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;

@SuppressWarnings("unused")
public class DevCommand extends Command{

	public DevCommand(){
		super("dev", "Collection of dev commands", Category.DEV);
		addOptions(
			new EvalCommand(),
			new SlashCommandsCommand(),
			new StatsCommand(),
			new TestCommand()
		);
	}

}
