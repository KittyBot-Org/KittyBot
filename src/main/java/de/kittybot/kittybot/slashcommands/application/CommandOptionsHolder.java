package de.kittybot.kittybot.slashcommands.application;

import de.kittybot.kittybot.slashcommands.application.options.CommandOption;

import java.util.List;

public interface CommandOptionsHolder{

	List<CommandOption> getOptions();

}
