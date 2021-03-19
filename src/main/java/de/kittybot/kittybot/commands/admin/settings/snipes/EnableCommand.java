package de.kittybot.kittybot.commands.admin.settings.snipes;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionBoolean;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;

public class EnableCommand extends GuildSubCommand{

	public EnableCommand(){
		super("enable", "Used to globally disable snipes");
		addOptions(
			new CommandOptionBoolean("enabled", "Whether to enable/disable snipes globally")
		);
	}

	@Override
	public void run(Options options, GuildCommandContext ctx){
		var enabled = options.getBoolean("enabled");
		ctx.get(SettingsModule.class).setSnipesEnabled(ctx.getGuildId(), enabled);
		ctx.reply("Snipes globally `" + (enabled ? "enabled" : "disabled") + "`");
	}

}
