package de.kittybot.kittybot.commands.admin.ignore.channel;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.utils.MessageUtils;

import java.util.stream.Collectors;

public class ListCommand extends GuildSubCommand{

	public ListCommand(){
		super("list", "Used to list ignored a users");
	}

	@Override
	public void run(Options options, GuildInteraction ia){
		var channels = ia.get(SettingsModule.class).getBotDisabledChannels(ia.getGuildId());
		if(channels.isEmpty()){
			ia.reply("No disabled channels configured yet");
		}
		ia.reply("**Disabled following channels:**\n" + channels.stream().map(MessageUtils::getChannelMention).collect(Collectors.joining(", ")));
	}

}
