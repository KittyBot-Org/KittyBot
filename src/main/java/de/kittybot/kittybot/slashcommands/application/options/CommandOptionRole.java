package de.kittybot.kittybot.slashcommands.application.options;

import de.kittybot.kittybot.objects.exceptions.OptionParseException;
import de.kittybot.kittybot.slashcommands.application.CommandOption;
import de.kittybot.kittybot.slashcommands.application.CommandOptionType;
import net.dv8tion.jda.api.entities.Command;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.exceptions.ParsingException;

public class CommandOptionRole extends CommandOption<Role>{

	public CommandOptionRole(String name, String description){
		super(Command.OptionType.ROLE, name, description);
	}

	@Override
	public Role parseValue(SlashCommandEvent.OptionData optionData){
		try{
			return optionData.getAsRole();
		}
		catch(ParsingException e){
			throw new OptionParseException(optionData, "role");
		}
	}

}
