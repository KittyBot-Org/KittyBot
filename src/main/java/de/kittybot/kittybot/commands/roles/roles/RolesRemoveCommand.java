package de.kittybot.kittybot.commands.roles.roles;

import de.kittybot.kittybot.command.*;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.context.CommandContext;
import net.dv8tion.jda.api.Permission;

public class RolesRemoveCommand extends Command{

	public RolesRemoveCommand(Command parent){
		super(parent,"remove", "Used to remove self-assignable roles", Category.ROLES);
		setUsage("<@Role>");
		addPermissions(Permission.ADMINISTRATOR);
	}

	@Override
	protected void run(Args args, CommandContext ctx){
		var roles = ctx.getMessage().getMentionedRoles();
		if(roles.isEmpty()){
			ctx.sendUsage(this);
			return;
		}
		//SelfAssignableRoleCache.removeSelfAssignableRoles(ctx.getGuild().getId(), Utils.toSet(roles));
		ctx.sendSuccess("Roles removed!");
	}

}
