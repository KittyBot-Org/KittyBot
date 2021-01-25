package de.kittybot.kittybot.slashcommands.application.options;

import de.kittybot.kittybot.objects.exceptions.OptionParseException;
import de.kittybot.kittybot.slashcommands.application.CommandOption;
import de.kittybot.kittybot.slashcommands.application.CommandOptionType;
import net.dv8tion.jda.api.entities.Message;

public class CommandOptionRawEmote extends CommandOption<String>{

	public CommandOptionRawEmote(String name, String description){
		super(CommandOptionType.STRING, name, description);
	}

	@Override
	public String parseValue(Object value){
		try{
			var rawEmote = (String) value;
			var matcher = Message.MentionType.EMOTE.getPattern().matcher(rawEmote);
			if(matcher.matches()){
				return rawEmote;
			}
		}
		catch(ClassCastException ignored){
		}
		throw new OptionParseException("Failed to parse option as emote");
	}

}
