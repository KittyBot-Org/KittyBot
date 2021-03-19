package de.kittybot.kittybot.slashcommands.application.options;

import de.kittybot.kittybot.objects.exceptions.OptionParseException;
import de.kittybot.kittybot.slashcommands.application.CommandOption;
import de.kittybot.kittybot.slashcommands.application.CommandOptionType;
import net.dv8tion.jda.api.entities.Command;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.exceptions.ParsingException;

public class CommandOptionRawEmote extends CommandOption<String>{

	public CommandOptionRawEmote(String name, String description){
		super(Command.OptionType.STRING, name, description);
	}

	@Override
	public String parseValue(SlashCommandEvent.OptionData optionData){
		try{
			var rawEmote = optionData.getAsString();
			var matcher = Message.MentionType.EMOTE.getPattern().matcher(rawEmote);
			if(matcher.matches()){
				return rawEmote;
			}
		}
		catch(ParsingException ignored){
		}
		throw new OptionParseException(optionData, "emote");
	}

}
