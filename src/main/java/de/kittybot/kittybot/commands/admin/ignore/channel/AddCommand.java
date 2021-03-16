package de.kittybot.kittybot.commands.admin.ignore.channel;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionChannel;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;

public class AddCommand extends GuildSubCommand{

	public AddCommand(){
		super("add", "Used to disable a channel");
		addOptions(
			new CommandOptionChannel("channel", "Channel to disable").required()
		);
	}

	@Override
	public void run(Options options, GuildInteraction ia){
		var channel = options.getTextChannel("channel");
		ia.get(SettingsModule.class).setBotDisabledInChannel(ia.getGuildId(), channel.getIdLong(), true);
		ia.reply("Disabled commands in: " + channel.getAsMention());
	}

}
