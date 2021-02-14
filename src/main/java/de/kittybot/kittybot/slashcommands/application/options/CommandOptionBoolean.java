package de.kittybot.kittybot.slashcommands.application.options;

import de.kittybot.kittybot.objects.exceptions.OptionParseException;
import de.kittybot.kittybot.slashcommands.application.CommandOption;
import de.kittybot.kittybot.slashcommands.application.CommandOptionType;

public class CommandOptionBoolean extends CommandOption<Boolean>{

	public CommandOptionBoolean(String name, String description){
		super(CommandOptionType.BOOLEAN, name, description);
	}

	public Boolean parseValue(Object value){
		try{
			return (boolean) value;
		}
		catch(ClassCastException e){
			throw new OptionParseException("Failed to parse " + value + " as true/false");
		}
	}

}
