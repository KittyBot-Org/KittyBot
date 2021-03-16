package de.kittybot.kittybot.commands.admin.settings.snipes;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionBoolean;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;

public class EnableCommand extends GuildSubCommand{

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
