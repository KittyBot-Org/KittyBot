package de.kittybot.kittybot.commands.admin.settings.snipes;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionBoolean;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionChannel;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;

public class ChannelCommand extends GuildSubCommand{

	public ChannelCommand(){
		super("channel", "Used to enable/disable snipes in a specific channel");
		addOptions(
			new CommandOptionChannel("channel", "The channel to enable/disable snipes").required(),
			new CommandOptionBoolean("enabled", "Whether to enable/disable snipes").required()
		);
	}

	@Override
	public void run(Options options, GuildInteraction ia){
		var channel = options.getTextChannel("channel");
		var enabled = options.getBoolean("enabled");
		ia.get(SettingsModule.class).setSnipesDisabledInChannel(ia.getGuildId(), channel.getIdLong(), !enabled);
		ia.reply("Snipes `" + (enabled ? "enabled" : "disabled") + "` in " + channel.getAsMention());
	}

}
