package de.kittybot.kittybot.slashcommands.application.options;

import de.kittybot.kittybot.objects.exceptions.OptionParseException;
import de.kittybot.kittybot.slashcommands.application.CommandOption;
import de.kittybot.kittybot.slashcommands.application.CommandOptionType;
import de.kittybot.kittybot.utils.TimeUtils;

import java.time.LocalDateTime;

public class CommandOptionTime extends CommandOption<LocalDateTime>{

	public CommandOptionTime(String name, String description){
		super(CommandOptionType.STRING, name, description);
	}

	@Override
	public LocalDateTime parseValue(Object value){
		try{
			return TimeUtils.parse((String) value);
		}
		catch(ClassCastException ignored){
		}
		throw new OptionParseException("Failed to parse " + value + " as time");
	}

}
