package de.kittybot.kittybot.commands.admin.ignore.user;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionUser;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;

import java.util.Collections;

public class RemoveCommand extends GuildSubCommand{

	public RemoveCommand(){
		super("remove", "Used to enable a user");
		addOptions(
			new CommandOptionUser("user", "User to enable").required()
		);
	}

	@Override
	public void run(Options options, GuildInteraction ia){
		var user = options.getUser("user");
		ia.get(SettingsModule.class).removeBotIgnoredUsers(ia.getGuildId(), Collections.singleton(user.getIdLong()));
		ia.reply("Enabled commands for: " + user.getAsMention());
	}

}
