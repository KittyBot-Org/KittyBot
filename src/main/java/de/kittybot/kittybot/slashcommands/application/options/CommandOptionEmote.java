package de.kittybot.kittybot.slashcommands.application.options;

import de.kittybot.kittybot.objects.exceptions.OptionParseException;
import de.kittybot.kittybot.slashcommands.application.CommandOption;
import de.kittybot.kittybot.slashcommands.application.CommandOptionType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.MiscUtil;

public class CommandOptionEmote extends CommandOption<Long>{

	public CommandOptionEmote(String name, String description){
		super(CommandOptionType.STRING, name, description);
	}

	@Override
	public Long parseValue(Object value){
		try{
			var matcher = Message.MentionType.EMOTE.getPattern().matcher((String) value);
			if(matcher.matches()){
				return MiscUtil.parseSnowflake(matcher.group(2));
			}
		}
		catch(ClassCastException | NumberFormatException ignored){
		}
		throw new OptionParseException("Failed to parse option as emote");
	}

}
