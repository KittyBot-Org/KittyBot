package de.kittybot.kittybot.commands.roles.roles;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.context.CommandContext;
import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.utils.MessageUtils;
import net.dv8tion.jda.api.Permission;

import java.util.stream.Collectors;

public class RolesListCommand extends Command{

	public RolesListCommand(Command parent){
		super(parent, "list", "Used to list self-assignable roles", Category.ROLES);
		setUsage("list");
		addPermissions(Permission.ADMINISTRATOR);
	}

	@Override
	protected void run(Args args, CommandContext ctx){
		var sRoles = ctx.get(SettingsModule.class).getSelfAssignableRoles(ctx.getGuildId());
		if(sRoles == null || sRoles.isEmpty()){
			ctx.sendSuccess("There are no roles added");
			return;
		}
		ctx.sendSuccess("Roles: \n" + sRoles.stream().map(sarg -> MessageUtils.getRoleMention(sarg.getRoleId())).collect(Collectors.joining("\n")));
	}

}
