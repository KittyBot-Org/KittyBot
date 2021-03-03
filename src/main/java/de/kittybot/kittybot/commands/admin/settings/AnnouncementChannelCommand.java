package de.kittybot.kittybot.commands.admin.settings;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionChannel;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.slashcommands.interaction.response.InteractionResponse;

public class AnnouncementChannelCommand extends GuildSubCommand{

	public AnnouncementChannelCommand(){
		super("announcementchannel", "Sets the announcement channel");
		addOptions(
			new CommandOptionChannel("channel", "The new announcement channel").required()
		);
	}

	@Override
	public void run(Options options, GuildInteraction ia){
		var channel = options.getTextChannel("channel");
		ia.get(SettingsModule.class).setAnnouncementChannelId(ia.getGuildId(), channel.getIdLong());
		ia.reply(new InteractionResponse.Builder().setContent("Announcement channel set to: " + channel.getAsMention()).build());
	}

}
