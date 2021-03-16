package de.kittybot.kittybot.commands.admin.ignore.channel;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionChannel;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;

public class RemoveCommand extends GuildSubCommand{

	public RemoveCommand(){
		super("remove", "Used to enable a channel");
		addOptions(
			new CommandOptionChannel("channel", "Channel to enable").required()
		);
	}

	@Override
	public void run(Options options, GuildInteraction ia){
		var channel = options.getTextChannel("channel");
		ia.get(SettingsModule.class).setBotDisabledInChannel(ia.getGuildId(), channel.getIdLong(), false);
		ia.reply("Enabled commands in: " + channel.getAsMention());
	}

}
