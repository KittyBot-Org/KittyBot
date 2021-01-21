package de.kittybot.kittybot.slashcommands.application;

import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;

public interface RunnableCommand{

	void run(Options options, CommandContext ctx);

}
