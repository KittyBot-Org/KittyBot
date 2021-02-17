package de.kittybot.kittybot.slashcommands.application.options;

import de.kittybot.kittybot.objects.exceptions.OptionParseException;
import de.kittybot.kittybot.slashcommands.application.CommandOption;
import de.kittybot.kittybot.slashcommands.application.CommandOptionType;

public class CommandOptionFloat extends CommandOption<Float>{

	public CommandOptionFloat(String name, String description){
		super(CommandOptionType.STRING, name, description);
	}

	@Override
	public Float parseValue(Object value){
		try{
			return Float.parseFloat((String) value);
		}
		catch(ClassCastException | NumberFormatException e){
			throw new OptionParseException("Failed to parse " + value + " as float");
		}
	}

}
