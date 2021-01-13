package de.kittybot.kittybot.commands.roles;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.context.CommandContext;
import de.kittybot.kittybot.commands.roles.groups.AddGroupsCommand;
import de.kittybot.kittybot.commands.roles.groups.ListGroupsCommand;
import de.kittybot.kittybot.commands.roles.groups.RemoveGroupsCommand;
import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.objects.SelfAssignableRoleGroup;
import de.kittybot.kittybot.utils.MessageUtils;
import de.kittybot.kittybot.utils.TableBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

import java.util.Collections;
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

	public Set<SelfAssignableRoleGroup> removeSelfAssignableRoleGroupsByName(CommandContext ctx, List<String> set){
		return null;
	}

}
