package de.kittybot.kittybot.commands.streams;

import de.kittybot.kittybot.commands.streams.streams.*;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;

@SuppressWarnings("unused")
public class StreamsCommand extends Command{

	public StreamsCommand(){
		super("streams", "Used to configure stream announcements", Category.ANNOUNCEMENT);
		addOptions(
			new AddCommand(),
			new RemoveCommand(),
			new ListCommand(),
			new MessageCommand(),
			new ChannelCommand()
		);
	}

}
