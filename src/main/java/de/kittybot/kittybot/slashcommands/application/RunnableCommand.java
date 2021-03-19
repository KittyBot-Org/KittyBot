package de.kittybot.kittybot.slashcommands.application;

import de.kittybot.kittybot.slashcommands.CommandContext;
import de.kittybot.kittybot.slashcommands.Options;

public interface RunnableCommand{

	void run(Options options, CommandContext ctx);

}
