package de.kittybot.kittybot.slashcommands.application.options;

import de.kittybot.kittybot.objects.exceptions.OptionParseException;
import de.kittybot.kittybot.slashcommands.application.CommandOption;
import de.kittybot.kittybot.slashcommands.application.CommandOptionType;
import net.dv8tion.jda.api.entities.Command;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.exceptions.ParsingException;

public class CommandOptionFloat extends CommandOption<Float>{

	public CommandOptionFloat(String name, String description){
		super(Command.OptionType.STRING, name, description);
	}

	@Override
	public Float parseValue(SlashCommandEvent.OptionData optionData){
		try{
			return Float.parseFloat(optionData.getAsString());
		}
		catch(ParsingException | NumberFormatException e){
			throw new OptionParseException(optionData, "float");
		}
	}

}
