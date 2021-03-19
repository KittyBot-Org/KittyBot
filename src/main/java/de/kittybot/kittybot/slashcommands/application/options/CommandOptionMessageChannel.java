package de.kittybot.kittybot.slashcommands.application.options;

import de.kittybot.kittybot.objects.exceptions.OptionParseException;
import de.kittybot.kittybot.slashcommands.application.CommandOption;
import net.dv8tion.jda.api.entities.Command;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.exceptions.ParsingException;

public class CommandOptionMessageChannel extends CommandOption<MessageChannel>{

	public CommandOptionMessageChannel(String name, String description){
		super(Command.OptionType.CHANNEL, name, description);
	}

	@Override
	public MessageChannel parseValue(SlashCommandEvent.OptionData optionData){
		try{
			return optionData.getAsMessageChannel();
		}
		catch(ParsingException e){
			throw new OptionParseException(optionData, "message channel");
		}
	}

}
