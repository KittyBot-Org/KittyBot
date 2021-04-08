package de.kittybot.kittybot.commands.info;

import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.RunCommand;
import de.kittybot.kittybot.slashcommands.interaction.Interaction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.MessageUtils;

@SuppressWarnings("unused")
public class DashboardCommand extends RunCommand{

	public DashboardCommand(){
		super("dashboard", "Shows you our dashboard", Category.INFORMATION);
	}

	@Override
	public void run(Options options, Interaction ia){
		ia.reply("You can find our dashboard " + MessageUtils.maskLink("here", Config.ORIGIN_URL));
	}

}
