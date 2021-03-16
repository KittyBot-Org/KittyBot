package de.kittybot.kittybot.slashcommands.application.options;

import de.kittybot.kittybot.objects.exceptions.OptionParseException;
import de.kittybot.kittybot.slashcommands.application.CommandOption;
import de.kittybot.kittybot.slashcommands.application.CommandOptionType;
import net.dv8tion.jda.api.entities.Command;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.exceptions.ParsingException;

public class CommandOptionBoolean extends CommandOption<Boolean>{

	public CommandOptionBoolean(String name, String description){
		super(Command.OptionType.BOOLEAN, name, description);
	}

	public Boolean parseValue(SlashCommandEvent.OptionData optionData){
		try{
			return optionData.getAsBoolean();
		}
		catch(ParsingException e){
			throw new OptionParseException(optionData, "true/false");
		}
	}

}
