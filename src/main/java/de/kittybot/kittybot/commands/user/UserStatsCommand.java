package de.kittybot.kittybot.commands.user;

import de.kittybot.kittybot.commands.user.user.stats.CardCommand;
import de.kittybot.kittybot.commands.user.user.stats.TopCommand;
import de.kittybot.kittybot.commands.user.user.stats.UserCommand;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;

@SuppressWarnings("unused")
public class UserStatsCommand extends Command{

	public UserStatsCommand(){
		super("stats", "Shows Stats", Category.USER);
		addOptions(
			new UserCommand(),
			new TopCommand(),
			new CardCommand()
		);
	}







}
