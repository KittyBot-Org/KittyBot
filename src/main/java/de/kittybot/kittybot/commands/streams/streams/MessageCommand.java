package de.kittybot.kittybot.commands.streams.streams;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionString;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;

public class MessageCommand extends GuildSubCommand{

	public MessageCommand(){
		super("message", "Sets the stream announcement message template");
		addOptions(
			new CommandOptionString("message", "The message template").required()
		);
	}

	@Override
	public void run(Options options, GuildInteraction ia){
		var message = options.getString("message");
		ia.get(SettingsModule.class).setStreamAnnouncementMessage(ia.getGuildId(), message);
		ia.reply("Set stream announcements template to:\n" + message);
	}

}
