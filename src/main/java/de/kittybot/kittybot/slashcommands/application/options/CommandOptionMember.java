package de.kittybot.kittybot.slashcommands.application.options;

import de.kittybot.kittybot.objects.exceptions.OptionParseException;
import de.kittybot.kittybot.slashcommands.application.CommandOption;
import net.dv8tion.jda.api.entities.Command;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.exceptions.ParsingException;

public class CommandOptionMember extends CommandOption<Member>{

	public CommandOptionMember(String name, String description){
		super(Command.OptionType.USER, name, description);
	}

	@Override
	public Member parseValue(SlashCommandEvent.OptionData optionData){
		try{
			return optionData.getAsMember();
		}
		catch(ParsingException e){
			throw new OptionParseException(optionData, "member");
		}
	}

}
