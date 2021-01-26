package de.kittybot.kittybot.slashcommands.application.options;

import de.kittybot.kittybot.objects.exceptions.OptionParseException;
import de.kittybot.kittybot.slashcommands.application.CommandOption;
import de.kittybot.kittybot.slashcommands.application.CommandOptionType;
import net.dv8tion.jda.api.entities.Message;

public class CommandOptionEmote extends CommandOption<String>{

	public CommandOptionEmote(String name, String description){
		super(CommandOptionType.STRING, name, description);
	}

	@Override
	public String parseValue(Object value){
		try{
			var emote = (String) value;
			var matcher = Message.MentionType.EMOTE.getPattern().matcher(emote);
			if(matcher.matches()){
				return emote;
			}
		}
		catch(ClassCastException | NumberFormatException ignored){
		}
		throw new OptionParseException("Failed to parse option as emote");
	}

}
