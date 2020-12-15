package de.kittybot.kittybot.commands.info;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.ctx.CommandContext;
import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.utils.MessageUtils;

import java.util.List;

public class DashboardCommand extends Command{

	private final KittyBot main;

	public DashboardCommand(KittyBot main){
		super("dashboard", "Shows you our dashboard", Category.INFORMATION);
		this.main = main;
	}

	@Override
	public void run(List<String> args, CommandContext ctx){
		ctx.sendSuccess("You can find our dashboard " + MessageUtils.maskLink("here", this.main.getConfig().getString("origin_url")));
	}

}
