package de.kittybot.kittybot.main.commands.roles.roles;

import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.old.Args;
import de.kittybot.kittybot.slashcommands.old.Command;
import de.kittybot.kittybot.slashcommands.old.CommandContext;
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
