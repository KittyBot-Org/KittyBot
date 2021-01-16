package de.kittybot.kittybot.main.commands.roles.roles;

import de.kittybot.kittybot.command.old.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.old.Command;
import de.kittybot.kittybot.command.old.CommandContext;
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
		// TODO
		ctx.sendSuccess("Roles removed!");
	}

}
