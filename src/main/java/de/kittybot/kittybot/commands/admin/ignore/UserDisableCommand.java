package de.kittybot.kittybot.commands.admin.ignore;

import de.kittybot.kittybot.commands.admin.ignore.user.AddCommand;
import de.kittybot.kittybot.commands.admin.ignore.user.ListCommand;
import de.kittybot.kittybot.commands.admin.ignore.user.RemoveCommand;
import de.kittybot.kittybot.slashcommands.application.options.SubCommandGroup;

public class UserDisableCommand extends SubCommandGroup{

	public UserDisableCommand(){
		super("users", "Used to list/ignore/unignore a user");
		addOptions(
			new AddCommand(),
			new RemoveCommand(),
			new ListCommand()
		);
	}

}
