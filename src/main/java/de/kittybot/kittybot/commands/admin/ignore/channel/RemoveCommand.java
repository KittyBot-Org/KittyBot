package de.kittybot.kittybot.commands.admin.ignore.channel;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionGuildChannel;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;

public class RemoveCommand extends GuildSubCommand{

	public RemoveCommand(){
		super("remove", "Used to enable a channel");
		addOptions(
			new CommandOptionGuildChannel("channel", "Channel to enable").required()
		);
	}

	@Override
	public void run(Options options, GuildCommandContext ctx){
		var channel = options.getTextChannel("channel");
		ctx.get(SettingsModule.class).setBotDisabledInChannel(ctx.getGuildId(), channel.getIdLong(), false);
		ctx.reply("Enabled commands in: " + channel.getAsMention());
	}

}
