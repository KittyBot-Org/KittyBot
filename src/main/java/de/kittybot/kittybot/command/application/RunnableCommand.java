package de.kittybot.kittybot.command.application;

import de.kittybot.kittybot.command.context.CommandContext;
import de.kittybot.kittybot.command.interaction.Options;

public interface RunnableCommand{

	void run(Options options, CommandContext ctx);

}
