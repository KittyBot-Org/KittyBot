package de.kittybot.kittybot.commands.admin.ignore.user;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionUser;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;

import java.util.Collections;

public class RemoveCommand extends GuildSubCommand{

	public RemoveCommand(){
		super("remove", "Used to enable a user");
		addOptions(
			new CommandOptionUser("user", "User to enable").required()
		);
	}

	@Override
	public void run(Options options, GuildCommandContext ctx){
		var user = options.getUser("user");
		ctx.get(SettingsModule.class).removeBotIgnoredUsers(ctx.getGuildId(), Collections.singleton(user.getIdLong()));
		ctx.reply("Enabled commands for: " + user.getAsMention());
	}

}
