package de.kittybot.kittybot.command.application;

import de.kittybot.kittybot.command.context.CommandContext;
import de.kittybot.kittybot.command.interaction.InteractionDataOption;
import de.kittybot.kittybot.command.interaction.Options;

import java.util.Map;

public interface RunnableCommand{

	void run(Options options, CommandContext ctx);
}
