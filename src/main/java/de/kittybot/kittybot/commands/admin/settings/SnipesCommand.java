package de.kittybot.kittybot.commands.admin.settings;

import de.kittybot.kittybot.commands.admin.settings.snipes.ChannelCommand;
import de.kittybot.kittybot.commands.admin.settings.snipes.EnableCommand;
import de.kittybot.kittybot.slashcommands.application.options.SubCommandGroup;

public class SnipesCommand extends SubCommandGroup{

	public SnipesCommand(){
		super("snipes", "Used to disable snipes");
		addOptions(
			new ChannelCommand(),
			new EnableCommand()
		);
	}

}
