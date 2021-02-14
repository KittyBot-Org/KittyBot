package de.kittybot.kittybot.slashcommands.application.options;

import de.kittybot.kittybot.objects.exceptions.OptionParseException;
import de.kittybot.kittybot.slashcommands.application.CommandOption;
import de.kittybot.kittybot.slashcommands.application.CommandOptionType;

public class CommandOptionChannel extends CommandOption<Long>{

	public CommandOptionChannel(String name, String description){
		super(CommandOptionType.CHANNEL, name, description);
	}

	@Override
	public Long parseValue(Object value){
		try{
			return value instanceof Long ? (Long) value : Long.parseLong((String) value);
		}
		catch(ClassCastException | NumberFormatException e){
			throw new OptionParseException("Failed to parse " + value + " as channel");
		}
	}

}
