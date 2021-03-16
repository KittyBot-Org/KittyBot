package de.kittybot.kittybot.slashcommands.application.options;

import de.kittybot.kittybot.objects.exceptions.OptionParseException;
import de.kittybot.kittybot.slashcommands.application.CommandOption;
import de.kittybot.kittybot.slashcommands.application.CommandOptionType;
import net.dv8tion.jda.api.entities.Command;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.exceptions.ParsingException;

import java.net.MalformedURLException;

public class CommandOptionUser extends CommandOption<User>{

	public CommandOptionUser(String name, String description){
		super(Command.OptionType.USER, name, description);
	}

	@Override
	public User parseValue(SlashCommandEvent.OptionData optionData){
		try{
			return optionData.getAsUser();
		}
		catch(ParsingException e){
			throw new OptionParseException(optionData, "user");
		}
	}

}
