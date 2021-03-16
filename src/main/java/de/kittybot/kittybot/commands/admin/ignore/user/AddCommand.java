package de.kittybot.kittybot.commands.admin.ignore.user;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionUser;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;

import java.util.Collections;

public class AddCommand extends GuildSubCommand{

	public AddCommand(){
		super("add", "Used to disable commands for a users");
		addOptions(
			new CommandOptionUser("user", "User to disable commands").required()
		);
	}

	@Override
	public void run(Options options, GuildInteraction ia){
		var user = options.getUser("user");
		ia.get(SettingsModule.class).addBotIgnoredUsers(ia.getGuildId(), Collections.singleton(user.getIdLong()));
		ia.reply("Disabled commands for: " + user.getAsMention());
	}

}
