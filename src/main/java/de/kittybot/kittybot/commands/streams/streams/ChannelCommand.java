package de.kittybot.kittybot.commands.streams.streams;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionGuildChannel;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;

public class ChannelCommand extends GuildSubCommand{

	public ChannelCommand(){
		super("channel", "Sets the stream announcement channel");
		addOptions(
			new CommandOptionGuildChannel("channel", "The channel which stream announcements should get send to").required()
		);
	}

	@Override
	public void run(Options options, GuildCommandContext ctx){
		var channel = options.getTextChannel("channel");
		ctx.get(SettingsModule.class).setStreamAnnouncementChannelId(ctx.getGuildId(), channel.getIdLong());
		ctx.reply("Stream announcements now get send to " + channel.getAsMention());
	}

}
