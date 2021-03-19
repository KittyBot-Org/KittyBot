package de.kittybot.kittybot.slashcommands.application.options;

import de.kittybot.kittybot.objects.exceptions.OptionParseException;
import de.kittybot.kittybot.slashcommands.application.CommandOption;
import de.kittybot.kittybot.slashcommands.application.CommandOptionType;
import net.dv8tion.jda.api.entities.Command;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.exceptions.ParsingException;

import java.net.MalformedURLException;
import java.net.URL;

public class CommandOptionUrl extends CommandOption<String>{

	public CommandOptionUrl(String name, String description){
		super(Command.OptionType.STRING, name, description);
	}

	@Override
	public String parseValue(SlashCommandEvent.OptionData optionData){
		try{
			var url = optionData.getAsString();
			new URL(url);
			return url;
		}
		catch(ParsingException | MalformedURLException e){
			throw new OptionParseException(optionData, "url");
		}
	}

}
