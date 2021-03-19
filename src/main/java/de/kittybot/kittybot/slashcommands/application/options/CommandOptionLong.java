package de.kittybot.kittybot.slashcommands.application.options;

import de.kittybot.kittybot.objects.exceptions.OptionParseException;
import de.kittybot.kittybot.slashcommands.application.CommandOption;
import de.kittybot.kittybot.slashcommands.application.CommandOptionType;
import net.dv8tion.jda.api.entities.Command;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.exceptions.ParsingException;

public class CommandOptionLong extends CommandOption<Long>{

	public CommandOptionLong(String name, String description){
		super(Command.OptionType.STRING, name, description);
	}

	@Override
	public Long parseValue(SlashCommandEvent.OptionData optionData){
		try{
			return optionData.getAsLong();
		}
		catch(ParsingException | ClassCastException e){
			throw new OptionParseException(optionData, "long number");
		}
	}

}
