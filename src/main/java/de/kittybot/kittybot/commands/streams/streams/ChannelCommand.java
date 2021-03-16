package de.kittybot.kittybot.commands.streams.streams;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionChannel;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;

public class ChannelCommand extends GuildSubCommand{

	public ChannelCommand(){
		super("channel", "Sets the stream announcement channel");
		addOptions(
			new CommandOptionChannel("channel", "The channel which stream announcements should get send to").required()
		);
	}

	@Override
	public void run(Options options, GuildInteraction ia){
		var channel = options.getTextChannel("channel");
		ia.get(SettingsModule.class).setStreamAnnouncementChannelId(ia.getGuildId(), channel.getIdLong());
		ia.reply("Stream announcements now get send to " + channel.getAsMention());
	}

}
