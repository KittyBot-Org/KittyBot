package de.kittybot.kittybot.slashcommands.application.options;

import de.kittybot.kittybot.objects.exceptions.OptionParseException;
import de.kittybot.kittybot.slashcommands.application.CommandOption;
import de.kittybot.kittybot.slashcommands.application.CommandOptionType;
import net.dv8tion.jda.api.entities.Command;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.exceptions.ParsingException;

public class CommandOptionInteger extends CommandOption<Integer>{

	public CommandOptionInteger(String name, String description){
		super(Command.OptionType.INTEGER, name, description);
	}

	@Override
	public Integer parseValue(SlashCommandEvent.OptionData optionData){
		try{
			return (int) optionData.getAsLong();
		}
		catch(ParsingException | ClassCastException e){
			throw new OptionParseException(optionData, "number");
		}
	}

}
