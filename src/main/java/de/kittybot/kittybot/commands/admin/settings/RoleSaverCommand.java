package de.kittybot.kittybot.commands.admin.settings;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionBoolean;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;

public class RoleSaverCommand extends GuildSubCommand{

	public RoleSaverCommand(){
		super("rolesaver", "Enables/disables saving of user roles on leave");
		addOptions(
			new CommandOptionBoolean("enabled", "Whether role saving is enabled or disabled").required()
		);
	}

	@Override
	public void run(Options options, GuildInteraction ia){
		var enabled = options.getBoolean("enabled");
		ia.get(SettingsModule.class).setRoleSaverEnabled(ia.getGuildId(), enabled);
		ia.reply((enabled ? "Enabled" : "Disabled") + " role saving");
	}

}
