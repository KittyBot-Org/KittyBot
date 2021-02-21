package de.kittybot.kittybot.slashcommands.application;

import de.kittybot.kittybot.slashcommands.interaction.Interaction;
import de.kittybot.kittybot.slashcommands.interaction.Options;

public interface RunnableCommand{

	void run(Options options, Interaction ia);

}
