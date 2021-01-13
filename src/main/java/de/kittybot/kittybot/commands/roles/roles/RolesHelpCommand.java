package de.kittybot.kittybot.commands.roles.roles;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.context.CommandContext;
import net.dv8tion.jda.api.Permission;

public class RolesHelpCommand extends Command{

	public RolesHelpCommand(Command parent){
		super(parent, "help", "Used to see help to self assignable roles", Category.ROLES);
		setUsage("help");
		addAliases("?", "hilfe");
		addPermissions(Permission.ADMINISTRATOR);
	}

	@Override
	protected void run(Args args, CommandContext ctx){
		ctx.sendUsage(this);
	}

}
