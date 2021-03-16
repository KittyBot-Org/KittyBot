package de.kittybot.kittybot.commands.admin.settings;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionGuildChannel;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;

public class AnnouncementChannelCommand extends GuildSubCommand{

	public AnnouncementChannelCommand(){
		super("announcementchannel", "Sets the announcement channel");
		addOptions(
			new CommandOptionGuildChannel("channel", "The new announcement channel").required()
		);
	}

	@Override
	public void run(Options options, GuildCommandContext ctx){
		var channel = options.getTextChannel("channel");
		ctx.get(SettingsModule.class).setAnnouncementChannelId(ctx.getGuildId(), channel.getIdLong());
		ctx.reply("Announcement channel set to: " + channel.getAsMention());
	}

}
