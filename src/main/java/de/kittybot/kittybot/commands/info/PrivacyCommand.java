package de.kittybot.kittybot.commands.info;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.Command;
import de.kittybot.kittybot.command.ctx.CommandContext;
import de.kittybot.kittybot.utils.MessageUtils;

import java.util.List;

public class PrivacyCommand extends Command{

	public PrivacyCommand(){
		super("privacy", "Gives you a link to our privacy policy", Category.INFORMATION);
	}

	@Override
	public void run(List<String> args, CommandContext ctx){
		ctx.sendSuccess("You can find our privacy policy " + MessageUtils.maskLink("here", ctx.getConfig().getString("origin_url") + "/privacy"));
	}

}
