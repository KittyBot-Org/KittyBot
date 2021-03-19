package de.kittybot.kittybot.commands.admin.ignore.channel;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionGuildChannel;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;

public class AddCommand extends GuildSubCommand{

	public AddCommand(){
		super("add", "Used to disable a channel");
		addOptions(
			new CommandOptionGuildChannel("channel", "Channel to disable").required()
		);
	}

	@Override
	public void run(Options options, GuildCommandContext ctx){
		var channel = options.getTextChannel("channel");
		ctx.get(SettingsModule.class).setBotDisabledInChannel(ctx.getGuildId(), channel.getIdLong(), true);
		ctx.reply("Disabled commands in: " + channel.getAsMention());
	}

}
