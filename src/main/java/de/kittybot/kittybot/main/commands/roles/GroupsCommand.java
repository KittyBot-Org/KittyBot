package de.kittybot.kittybot.main.commands.roles;

import de.kittybot.kittybot.command.old.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.old.Command;
import de.kittybot.kittybot.command.old.CommandContext;
import de.kittybot.kittybot.main.commands.roles.groups.AddGroupsCommand;
import de.kittybot.kittybot.main.commands.roles.groups.ListGroupsCommand;
import de.kittybot.kittybot.main.commands.roles.groups.RemoveGroupsCommand;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class GroupsCommand extends Command{

	public GroupsCommand(){
		super("groups", "Used to manage your self assignable role groups", Category.ROLES);
		setUsage("<add/remove/list>");
		addAliases("g", "gruppen");
		addPermissions(Permission.ADMINISTRATOR);
		addChildren(
				new AddGroupsCommand(this),
				new RemoveGroupsCommand(this),
				new ListGroupsCommand(this)
		);
	}

	@Override
	public void run(Args args, CommandContext ctx){
		ctx.sendUsage(this);
	}

}
