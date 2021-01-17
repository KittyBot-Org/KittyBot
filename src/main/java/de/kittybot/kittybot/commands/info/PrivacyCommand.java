package de.kittybot.kittybot.commands.info;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.command.application.Command;
import de.kittybot.kittybot.command.application.RunnableCommand;
import de.kittybot.kittybot.command.context.CommandContext;
import de.kittybot.kittybot.command.interaction.Options;
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
