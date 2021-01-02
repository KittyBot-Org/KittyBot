package de.kittybot.kittybot.commands.info;

import de.kittybot.kittybot.command.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.ctx.CommandContext;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.MessageUtils;

import java.util.List;

public class DashboardCommand extends Command{

	public DashboardCommand(){
		super("dashboard", "Shows you our dashboard", Category.INFORMATION);
	}

	@Override
	public void run(Args args, CommandContext ctx){
		ctx.sendSuccess("You can find our dashboard " + MessageUtils.maskLink("here", Config.ORIGIN_URL));
	}

}
