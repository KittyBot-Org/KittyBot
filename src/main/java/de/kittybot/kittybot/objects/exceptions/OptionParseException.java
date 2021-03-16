package de.kittybot.kittybot.objects.exceptions;

import de.kittybot.kittybot.slashcommands.application.CommandOption;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class OptionParseException extends RuntimeException{

	public OptionParseException(String message){
		super(message, null, false, false);
	}

	public OptionParseException(SlashCommandEvent.OptionData optionData, String parseExample){
		super("Failed to parse " + (optionData == null ? "null" : optionData.getAsString()) + " as " + parseExample, null, false, false);
	}

}
