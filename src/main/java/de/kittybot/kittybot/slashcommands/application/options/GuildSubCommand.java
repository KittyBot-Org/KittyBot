package de.kittybot.kittybot.slashcommands.application.options;

import de.kittybot.kittybot.slashcommands.application.*;
import de.kittybot.kittybot.slashcommands.application.options.SubCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class GuildSubCommand extends SubBaseCommand implements RunnableGuildCommand{

	public GuildSubCommand(String name, String description){
		super(name, description);
	}

}
