package de.kittybot.kittybot.commands.roles.roles;

import de.kittybot.kittybot.commands.roles.roles.groups.AddCommand;
import de.kittybot.kittybot.commands.roles.roles.groups.ListCommand;
import de.kittybot.kittybot.commands.roles.roles.groups.RemoveCommand;
import de.kittybot.kittybot.slashcommands.application.options.SubCommandGroup;

@SuppressWarnings("unused")
public class GroupsCommand extends SubCommandGroup{

	public GroupsCommand(){
		super("groups", "Used to configure self assignable role groups");
		addOptions(
			new AddCommand(),
			new RemoveCommand(),
			new ListCommand()
		);
	}

}
