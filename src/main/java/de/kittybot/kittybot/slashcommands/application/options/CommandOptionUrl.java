package de.kittybot.kittybot.slashcommands.application.options;

import de.kittybot.kittybot.objects.exceptions.OptionParseException;
import de.kittybot.kittybot.slashcommands.application.CommandOption;
import de.kittybot.kittybot.slashcommands.application.CommandOptionType;

import java.net.MalformedURLException;
import java.net.URL;

public class CommandOptionUrl extends CommandOption<String>{

	public CommandOptionUrl(String name, String description){
		super(CommandOptionType.STRING, name, description);
	}

	@Override
	public String parseValue(Object value){
		try{
			var url = (String) value;
			new URL(url);
			return url;
		}
		catch(ClassCastException | MalformedURLException e){
			throw new OptionParseException("Failed to parse " + value + " as url");
		}
	}

}
