package de.kittybot.kittybot.slashcommands.application.options;

import de.kittybot.kittybot.objects.exceptions.OptionParseException;
import de.kittybot.kittybot.slashcommands.application.CommandOption;
import de.kittybot.kittybot.slashcommands.application.CommandOptionType;
import net.dv8tion.jda.api.entities.Command;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.exceptions.ParsingException;

public class CommandOptionGuildChannel extends CommandOption<GuildChannel>{

	public CommandOptionGuildChannel(String name, String description){
		super(Command.OptionType.CHANNEL, name, description);
	}

	@Override
	public GuildChannel parseValue(SlashCommandEvent.OptionData optionData){
		try{
			return optionData.getAsGuildChannel();
		}
		catch(ParsingException e){
			throw new OptionParseException(optionData, "server channel");
		}
	}

}
