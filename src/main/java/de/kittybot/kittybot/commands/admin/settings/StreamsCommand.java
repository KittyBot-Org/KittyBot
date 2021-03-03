package de.kittybot.kittybot.commands.admin.settings;

import de.kittybot.kittybot.commands.admin.settings.streams.*;
import de.kittybot.kittybot.commands.admin.settings.streams.ListCommand;
import de.kittybot.kittybot.slashcommands.application.options.SubCommandGroup;

public class StreamsCommand extends SubCommandGroup{

	public StreamsCommand(){
		super("streams", "Used to configure stream announcements");
		addOptions(
			new AddCommand(),
			new RemoveCommand(),
			new ListCommand(),
			new MessageCommand(),
			new ChannelCommand()
		);
	}

}
