package de.kittybot.kittybot.commands.admin.ignore.user;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.utils.MessageUtils;

import java.util.stream.Collectors;

public class ListCommand extends GuildSubCommand{

	public ListCommand(){
		super("list", "Used to list disabled a users");
	}

	@Override
	public void run(Options options, GuildInteraction ia){
		var users = ia.get(SettingsModule.class).getBotIgnoredUsers(ia.getGuildId());
		if(users.isEmpty()){
			ia.reply("No disabled users configured yet");
		}
		ia.reply("**Disabled following users:**\n" + users.stream().map(MessageUtils::getUserMention).collect(Collectors.joining(", ")));
	}

}
