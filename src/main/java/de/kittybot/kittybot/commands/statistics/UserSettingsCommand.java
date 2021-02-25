package de.kittybot.kittybot.commands.statistics;

import de.kittybot.kittybot.commands.statistics.user.settings.LevelCardCommand;
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
