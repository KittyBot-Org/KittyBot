package de.kittybot.kittybot.commands.info;

import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.RunnableCommand;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.MessageUtils;

@SuppressWarnings("unused")
public class PrivacyCommand extends Command implements RunnableCommand{

	public PrivacyCommand(){
		super("privacy", "Gives you a link to our privacy policy", Category.INFORMATION);
	}

	@Override
	public void run(Options options, CommandContext ctx){
		ctx.reply("You can find our privacy policy " + MessageUtils.maskLink("here", Config.ORIGIN_URL + "/privacy"));
	}

}
