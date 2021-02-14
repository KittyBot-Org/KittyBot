package de.kittybot.kittybot.slashcommands.application.options;

import de.kittybot.kittybot.objects.exceptions.OptionParseException;
import de.kittybot.kittybot.slashcommands.application.CommandOption;
import de.kittybot.kittybot.slashcommands.application.CommandOptionType;

public class CommandOptionInteger extends CommandOption<Integer>{

	public CommandOptionInteger(String name, String description){
		super(CommandOptionType.INTEGER, name, description);
	}

	@Override
	public Integer parseValue(Object value){
		try{
			return (int) value;
		}
		catch(ClassCastException e){
			throw new OptionParseException("Failed to parse " + value + " as integer");
		}
	}

}
