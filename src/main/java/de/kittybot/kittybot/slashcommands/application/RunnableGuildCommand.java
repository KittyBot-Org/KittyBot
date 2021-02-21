package de.kittybot.kittybot.slashcommands.application;

import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;

public interface RunnableGuildCommand{

	void run(Options options, GuildInteraction ia);

}
