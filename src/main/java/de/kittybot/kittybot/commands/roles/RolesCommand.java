package de.kittybot.kittybot.commands.roles;

import de.kittybot.kittybot.commands.roles.roles.AddCommand;
import de.kittybot.kittybot.commands.roles.roles.GroupsCommand;
import de.kittybot.kittybot.commands.roles.roles.ListCommand;
import de.kittybot.kittybot.commands.roles.roles.RemoveCommand;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.GenericHelpCommand;

@SuppressWarnings("unused")
public class RolesCommand extends Command{

	public RolesCommand(){
		super("roles", "Used to configure self assignable roles", Category.ROLES);
		addOptions(
			new AddCommand(),
			new RemoveCommand(),
			new ListCommand(),
			new GroupsCommand(),
			new GenericHelpCommand("To configure self assignable roles you need first need to create a group with `/roles groups add <group name> <max-roles-from-group>`.\n" +
				"Then you can add a role to this group by doing `/roles add <@role> <custom-emote> <group-name>`.\n" +
				"You can add as much as you like but only 20 of them are assignable by reactions via the specific emote.\n" +
				"Use `/roles list` to view all roles with groups in a nice message"
			)
		);
	}

}
