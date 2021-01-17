package de.kittybot.kittybot.commands.info;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.application.Command;
import de.kittybot.kittybot.command.application.RunnableCommand;
import de.kittybot.kittybot.command.context.CommandContext;
import de.kittybot.kittybot.command.interaction.Options;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.MessageUtils;

@SuppressWarnings("unused")
public class DashboardCommand extends Command implements RunnableCommand{

	public DashboardCommand(){
		super("dashboard", "Shows you our dashboard", Category.INFORMATION);
	}

	@Override
	public void run(Options options, CommandContext ctx){
		ctx.reply("You can find our dashboard " + MessageUtils.maskLink("here", Config.ORIGIN_URL));
	}

}
