package de.kittybot.kittybot.commands.admin.ignore.user;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.utils.MessageUtils;

import java.util.stream.Collectors;

public class ListCommand extends GuildSubCommand{

	public ListCommand(){
		super("list", "Used to list disabled a users");
	}

	@Override
	public void run(Options options, GuildCommandContext ctx){
		var users = ctx.get(SettingsModule.class).getBotIgnoredUsers(ctx.getGuildId());
		if(users.isEmpty()){
			ctx.reply("No disabled users configured yet");
		}
		ctx.reply("**Disabled following users:**\n" + users.stream().map(MessageUtils::getUserMention).collect(Collectors.joining(", ")));
	}

}
