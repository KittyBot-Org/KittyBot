package de.kittybot.kittybot.slashcommands.application.options;

import de.kittybot.kittybot.objects.exceptions.OptionParseException;
import de.kittybot.kittybot.slashcommands.application.CommandOption;
import de.kittybot.kittybot.slashcommands.application.CommandOptionType;

public class CommandOptionString extends CommandOption<String>{

	public CommandOptionString(String name, String description){
		super(CommandOptionType.STRING, name, description);
	}

	@Override
	public String parseValue(Object value){
		try{
			return (String) value;
		}
		catch(ClassCastException e){
			throw new OptionParseException("Failed to parse option as string");
		}
	}

}
