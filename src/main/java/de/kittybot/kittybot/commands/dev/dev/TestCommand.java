package de.kittybot.kittybot.commands.dev.dev;

import de.kittybot.kittybot.commands.dev.dev.test.ResponseCommand;
import de.kittybot.kittybot.slashcommands.application.options.SubCommandGroup;

public class TestCommand extends SubCommandGroup{

	public TestCommand(){
		super("test", "Some test commands");
		addOptions(
			new ResponseCommand()
		);
	}

}