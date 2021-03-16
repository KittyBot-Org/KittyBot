package de.kittybot.kittybot.commands.admin.ignore.channel;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.utils.MessageUtils;

import java.util.stream.Collectors;

public class ListCommand extends GuildSubCommand{

	public ListCommand(){
		super("list", "Used to list ignored a users");
	}

	@Override
	public void run(Options options, GuildCommandContext ctx){
		var channels = ctx.get(SettingsModule.class).getBotDisabledChannels(ctx.getGuildId());
		if(channels.isEmpty()){
			ctx.reply("No disabled channels configured yet");
		}
		ctx.reply("**Disabled following channels:**\n" + channels.stream().map(MessageUtils::getChannelMention).collect(Collectors.joining(", ")));
	}

}
