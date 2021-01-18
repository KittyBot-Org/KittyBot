package de.kittybot.kittybot.commands.admin;

import de.kittybot.kittybot.commands.admin.ignore.ChannelIgnoreCommand;
import de.kittybot.kittybot.commands.admin.ignore.UserIgnoreCommand;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class IgnoreCommand extends Command{

	public IgnoreCommand(){
		super("ignore", "Used to ignore users or channels", Category.ADMIN);
		addOptions(
			new UserIgnoreCommand(),
			new ChannelIgnoreCommand()
		);
		addPermissions(Permission.ADMINISTRATOR);
	}

}
