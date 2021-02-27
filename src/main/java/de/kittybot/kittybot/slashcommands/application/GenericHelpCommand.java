package de.kittybot.kittybot.slashcommands.application;

import de.kittybot.kittybot.slashcommands.application.options.SubCommand;
import de.kittybot.kittybot.slashcommands.interaction.Interaction;
import de.kittybot.kittybot.slashcommands.interaction.Options;

public class GenericHelpCommand extends SubCommand{

	private final String help;

	public GenericHelpCommand(String help){
		super("help", "Send you a detailed help messages");
		this.help = help;
	}

	@Override
	public void run(Options options, Interaction ia){
		ia.reply(this.help);
	}

}
