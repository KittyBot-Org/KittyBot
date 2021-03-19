package de.kittybot.kittybot.slashcommands.application.options;

import de.kittybot.kittybot.objects.exceptions.OptionParseException;
import de.kittybot.kittybot.slashcommands.application.CommandOption;
import de.kittybot.kittybot.slashcommands.application.CommandOptionType;
import net.dv8tion.jda.api.entities.Command;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.exceptions.ParsingException;

public class CommandOptionString extends CommandOption<String>{

	public CommandOptionString(String name, String description){
		super(Command.OptionType.STRING, name, description);
	}

	@Override
	public String parseValue(SlashCommandEvent.OptionData optionData){
		try{
			return optionData.getAsString();
		}
		catch(ParsingException e){
			throw new OptionParseException(optionData, "string");
		}
	}

}
