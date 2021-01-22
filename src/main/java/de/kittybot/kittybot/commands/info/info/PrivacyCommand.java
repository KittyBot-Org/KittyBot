package de.kittybot.kittybot.commands.info.info;

import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.RunnableCommand;
import de.kittybot.kittybot.slashcommands.application.options.SubCommand;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.MessageUtils;

@SuppressWarnings("unused")
public class PrivacyCommand extends SubCommand{

	public PrivacyCommand(){
		super("privacy", "Gives you a link to our privacy policy");
	}

	@Override
	public void run(Options options, CommandContext ctx){
		ctx.reply("You can find our privacy policy " + MessageUtils.maskLink("here", Config.ORIGIN_URL + "/privacy"));
	}

}
