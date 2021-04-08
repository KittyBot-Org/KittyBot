package de.kittybot.kittybot.commands.admin;

import de.kittybot.kittybot.commands.admin.ignore.ChannelDisableCommand;
import de.kittybot.kittybot.commands.admin.ignore.UserDisableCommand;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class DisableCommand extends Command{

	public DisableCommand(){
		super("disable", "Used to disable users or channels", Category.ADMIN);
		addOptions(
			new UserDisableCommand(),
			new ChannelDisableCommand()
		);
		addPermissions(Permission.ADMINISTRATOR);
	}

}
