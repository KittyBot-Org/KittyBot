package de.kittybot.kittybot.commands.admin;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.application.Command;
import de.kittybot.kittybot.command.context.CommandContext;
import de.kittybot.kittybot.command.interaction.Options;
import de.kittybot.kittybot.command.options.CommandOptionChannel;
import de.kittybot.kittybot.command.options.CommandOptionUser;
import de.kittybot.kittybot.command.options.SubCommand;
import de.kittybot.kittybot.command.options.SubCommandGroup;
import de.kittybot.kittybot.commands.admin.ignore.ChannelIgnoreCommand;
import de.kittybot.kittybot.commands.admin.ignore.UserIgnoreCommand;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class IgnoreCommand extends Command{

	public IgnoreCommand(){
		super("ignore", "Used to ignore users or channels", Category.ADMIN);
		addOptions(
				new UserIgnoreCommand(),
				new ChannelIgnoreCommand()
		);
		addPermissions(Permission.MANAGE_SERVER);
	}

}