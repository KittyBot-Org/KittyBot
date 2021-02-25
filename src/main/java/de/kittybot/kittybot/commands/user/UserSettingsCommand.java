package de.kittybot.kittybot.commands.user;

import de.kittybot.kittybot.commands.user.user.settings.LevelCardCommand;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;

@SuppressWarnings("unused")
public class UserSettingsCommand extends Command{


	public UserSettingsCommand(){
		super("usersettings", "description", Category.STATISTICS);
		addOptions(
			new LevelCardCommand()
		);

	}

}
