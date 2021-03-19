package de.kittybot.kittybot.slashcommands.application;

import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;

public interface RunnableGuildCommand{

	void run(Options options, GuildCommandContext ctx);

}
