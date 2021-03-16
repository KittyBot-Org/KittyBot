package de.kittybot.kittybot.commands.admin;

import de.kittybot.kittybot.commands.admin.ban.AddCommand;
import de.kittybot.kittybot.commands.admin.ban.ListCommand;
import de.kittybot.kittybot.commands.admin.ban.RemoveCommand;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class BanCommand extends Command{

	public BanCommand(){
		super("ban", "Bans a member", Category.ADMIN);
		addOptions(
			new AddCommand(),
			new RemoveCommand(),
			new ListCommand()
		);
		addPermissions(Permission.BAN_MEMBERS);
	}

}
