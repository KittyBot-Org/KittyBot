package de.kittybot.kittybot.commands.user.user.settings;

import de.kittybot.kittybot.commands.user.user.settings.level.card.*;
import de.kittybot.kittybot.slashcommands.application.options.SubCommandGroup;

public class LevelCardCommand extends SubCommandGroup{

	public LevelCardCommand(){
		super("level-card", "Lets you set the appearance of your level card");
		addOptions(
			new BackgroundUrlCommand(),
			new BackgroundColorCommand(),
			new PrimaryColorCommand(),
			new BorderColorCommand(),
			new FontColorCommand()
		);
	}

}
