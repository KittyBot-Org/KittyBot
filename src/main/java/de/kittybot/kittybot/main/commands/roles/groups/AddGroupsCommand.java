package de.kittybot.kittybot.main.commands.roles.groups;

import de.kittybot.kittybot.command.old.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.old.Command;
import de.kittybot.kittybot.command.old.CommandContext;
import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.objects.SelfAssignableRoleGroup;

import java.util.Collections;

@SuppressWarnings("unused")
public class AddGroupsCommand extends Command{

	public AddGroupsCommand(Command parent){
		super(parent, "add", "Used to manage your self assignable role groups", Category.ROLES);
		setUsage("<name> <max roles>");
		addAliases("add", "create", "new");
	}

	@Override
	public void run(Args args, CommandContext ctx){
		if(args.isEmpty()){
			ctx.sendUsage(this);
			return;
		}
		var maxRoles = -1;
		try{
			if(args.size() < 1){
				maxRoles = Integer.parseInt(args.get(1));
			}
		}
		catch(NumberFormatException e){
			ctx.sendError("Please provide a number as your second argument");
			return;
		}
		ctx.get(SettingsModule.class).addSelfAssignableRoleGroups(ctx.getGuildId(), Collections.singleton(new SelfAssignableRoleGroup(ctx.getGuildId(), -1L, args.get(0), maxRoles)));
		ctx.sendSuccess("Group added!");
	}

}
