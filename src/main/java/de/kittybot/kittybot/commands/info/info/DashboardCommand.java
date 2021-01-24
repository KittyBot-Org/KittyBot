package de.kittybot.kittybot.commands.info.info;

import de.kittybot.kittybot.slashcommands.application.options.SubCommand;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.MessageUtils;

@SuppressWarnings("unused")
public class DashboardCommand extends SubCommand{

	public DashboardCommand(){
		super("dashboard", "Shows you our dashboard");
	}

	@Override
	public void run(Options options, CommandContext ctx){
		ctx.reply("You can find our dashboard " + MessageUtils.maskLink("here", Config.ORIGIN_URL));
	}

}
