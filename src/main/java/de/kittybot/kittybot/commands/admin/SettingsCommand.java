package de.kittybot.kittybot.commands.admin;

import de.kittybot.kittybot.commands.admin.settings.*;
import de.kittybot.kittybot.commands.streams.StreamAnnouncementsCommand;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class SettingsCommand extends Command{

	public SettingsCommand(){
		super("settings", "Let's you see/change settings", Category.ADMIN);
		addOptions(
			new ListCommand(),
			new DJRoleCommand(),
			new AnnouncementChannelCommand(),
			new JoinMessageCommand(),
			new LeaveMessageCommand(),
			new NsfwCommand(),
			new LogMessagesCommand(),
			new SnipesCommand(),
			new StreamAnnouncementsCommand(),
			new RoleSaverCommand()
		);
		addPermissions(Permission.ADMINISTRATOR);
	}

}
