package de.kittybot.kittybot.main.commands.info;

import de.kittybot.kittybot.command.old.Args;
import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.old.Command;
import de.kittybot.kittybot.command.old.CommandContext;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.MessageUtils;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class DashboardCommand extends Command{

	public DashboardCommand(){
		super("dashboard", "Shows you our dashboard", Category.INFORMATION);
		addPermissions(Permission.MESSAGE_MANAGE);
	}

	@Override
	public void run(Args args, CommandContext ctx){
		ctx.sendSuccess("You can find our dashboard " + MessageUtils.maskLink("here", Config.ORIGIN_URL));
	}

}
