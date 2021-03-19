package de.kittybot.kittybot.commands.admin.ignore.user;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionUser;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;

import java.util.Collections;

public class AddCommand extends GuildSubCommand{

	public AddCommand(){
		super("add", "Used to disable commands for a users");
		addOptions(
			new CommandOptionUser("user", "User to disable commands").required()
		);
	}

	@Override
	public void run(Options options, GuildCommandContext ctx){
		var user = options.getUser("user");
		ctx.get(SettingsModule.class).addBotIgnoredUsers(ctx.getGuildId(), Collections.singleton(user.getIdLong()));
		ctx.reply("Disabled commands for: " + user.getAsMention());
	}

}
