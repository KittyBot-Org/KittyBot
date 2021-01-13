package de.kittybot.kittybot.commands.roles;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.context.CommandContext;
import de.kittybot.kittybot.commands.roles.groups.AddGroupsCommand;
import de.kittybot.kittybot.commands.roles.groups.ListGroupsCommand;
import de.kittybot.kittybot.commands.roles.groups.RemoveGroupsCommand;
import de.kittybot.kittybot.objects.SelfAssignableRoleGroup;
import net.dv8tion.jda.api.Permission;

import java.util.List;
import java.util.Set;

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
