package de.kittybot.kittybot.commands.admin.ignore;

import de.kittybot.kittybot.commands.admin.ignore.channel.AddCommand;
import de.kittybot.kittybot.commands.admin.ignore.channel.ListCommand;
import de.kittybot.kittybot.commands.admin.ignore.channel.RemoveCommand;
import de.kittybot.kittybot.slashcommands.application.options.SubCommandGroup;

public class ChannelDisableCommand extends SubCommandGroup{

	public ChannelDisableCommand(){
		super("channels", "Used to list/ignore/unignore a channel");
		addOptions(
			new AddCommand(),
			new RemoveCommand(),
			new ListCommand()
		);
	}

}