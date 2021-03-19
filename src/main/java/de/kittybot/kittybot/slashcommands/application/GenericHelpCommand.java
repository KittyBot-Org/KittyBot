package de.kittybot.kittybot.slashcommands.application;

import de.kittybot.kittybot.slashcommands.CommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.slashcommands.application.options.SubCommand;

public class GenericHelpCommand extends SubCommand{

	private final String help;

	public GenericHelpCommand(String help){
		super("help", "Send you a detailed help messages");
		this.help = help;
	}

	@Override
	public void run(Options options, CommandContext ctx){
		ctx.reply(this.help);
	}

}
