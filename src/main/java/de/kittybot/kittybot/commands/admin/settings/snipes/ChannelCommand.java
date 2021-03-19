package de.kittybot.kittybot.commands.admin.settings.snipes;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionBoolean;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionGuildChannel;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;

public class ChannelCommand extends GuildSubCommand{

	public ChannelCommand(){
		super("channel", "Used to enable/disable snipes in a specific channel");
		addOptions(
			new CommandOptionGuildChannel("channel", "The channel to enable/disable snipes").required(),
			new CommandOptionBoolean("enabled", "Whether to enable/disable snipes").required()
		);
	}

	@Override
	public void run(Options options, GuildCommandContext ctx){
		var channel = options.getTextChannel("channel");
		var enabled = options.getBoolean("enabled");
		ctx.get(SettingsModule.class).setSnipesDisabledInChannel(ctx.getGuildId(), channel.getIdLong(), !enabled);
		ctx.reply("Snipes `" + (enabled ? "enabled" : "disabled") + "` in " + channel.getAsMention());
	}

}
