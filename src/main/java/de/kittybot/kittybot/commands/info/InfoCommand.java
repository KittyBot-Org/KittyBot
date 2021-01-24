package de.kittybot.kittybot.commands.info;

import de.kittybot.kittybot.commands.info.info.*;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;

@SuppressWarnings("unused")
public class InfoCommand extends Command{

	public InfoCommand(){
		super("info", "Shows bot info", Category.INFORMATION);
		addOptions(
			new BotCommand(),
			new UptimeCommand(),
			new AvatarCommand(),
			new GuildIconCommand(),
			new GuildBannerCommand(),
			new DashboardCommand(),
			new PrivacyCommand()
		);
	}

}
