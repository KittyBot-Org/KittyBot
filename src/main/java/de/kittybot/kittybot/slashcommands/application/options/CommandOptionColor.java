package de.kittybot.kittybot.slashcommands.application.options;

import de.kittybot.kittybot.objects.exceptions.OptionParseException;
import de.kittybot.kittybot.slashcommands.application.CommandOption;
import de.kittybot.kittybot.slashcommands.application.CommandOptionType;
import net.dv8tion.jda.api.entities.Message;

import java.awt.Color;
import java.util.regex.Pattern;

public class CommandOptionColor extends CommandOption<Color>{

	public CommandOptionColor(String name, String description){
		super(CommandOptionType.STRING, name, description);
	}

	@Override
	public Color parseValue(Object value){
		try{
			var rawColor = (String) value;
			return Color.decode(rawColor);
		}
		catch(ClassCastException | NumberFormatException ignored){
		}
		throw new OptionParseException("Failed to parse " + value + " as color");
	}

}
