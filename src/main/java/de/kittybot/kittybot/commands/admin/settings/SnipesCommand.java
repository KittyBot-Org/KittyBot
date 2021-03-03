package de.kittybot.kittybot.commands.admin.settings;

import de.kittybot.kittybot.commands.admin.SettingsCommand;
import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionBoolean;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionChannel;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.application.options.SubCommandGroup;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;

public class SnipesCommand extends SubCommandGroup{

	public SnipesCommand(){
		super("snipes", "Used to disable snipes");
		addOptions(
			new ChannelCommand(),
			new EnableCommand()
		);
	}

	private static class ChannelCommand extends GuildSubCommand{

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

	private static class EnableCommand extends GuildSubCommand{

		public EnableCommand(){
			super("enable", "Used to globally disable snipes");
			addOptions(
				new CommandOptionBoolean("enabled", "Whether to enable/disable snipes globally")
			);
		}

		@Override
		public void run(Options options, GuildInteraction ia){
			var enabled = options.getBoolean("enabled");
			ia.get(SettingsModule.class).setSnipesEnabled(ia.getGuildId(), enabled);
			ia.reply("Snipes globally `" + (enabled ? "enabled" : "disabled") + "`");
		}

	}

}
