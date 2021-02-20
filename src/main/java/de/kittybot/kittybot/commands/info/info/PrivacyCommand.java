package de.kittybot.kittybot.commands.info.info;

import de.kittybot.kittybot.slashcommands.application.options.SubCommand;
import de.kittybot.kittybot.slashcommands.interaction.Interaction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.MessageUtils;

@SuppressWarnings("unused")
public class PrivacyCommand extends SubCommand{

	public PrivacyCommand(){
		super("privacy", "Gives you a link to our privacy policy");
	}

	@Override
	public void run(Options options, Interaction ia){
		ia.reply("You can find our privacy policy " + MessageUtils.maskLink("here", Config.ORIGIN_URL + "/privacy"));
	}

}
