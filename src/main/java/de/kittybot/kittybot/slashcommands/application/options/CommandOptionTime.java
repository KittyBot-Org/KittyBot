package de.kittybot.kittybot.slashcommands.application.options;

import de.kittybot.kittybot.objects.exceptions.OptionParseException;
import de.kittybot.kittybot.slashcommands.application.CommandOption;
import de.kittybot.kittybot.slashcommands.application.CommandOptionType;
import de.kittybot.kittybot.utils.TimeUtils;
import net.dv8tion.jda.api.entities.Command;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.exceptions.ParsingException;

import java.time.LocalDateTime;

public class CommandOptionTime extends CommandOption<LocalDateTime>{

	public CommandOptionTime(String name, String description){
		super(Command.OptionType.STRING, name, description);
	}

	@Override
	public LocalDateTime parseValue(SlashCommandEvent.OptionData optionData){
		try{
			return TimeUtils.parse(optionData.getAsString());
		}
		catch(ParsingException e){
			throw new OptionParseException(optionData, "true/false");
		}
	}

}
