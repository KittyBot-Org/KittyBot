package de.kittybot.kittybot.commands.admin.settings;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionBoolean;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;

public class RoleSaverCommand extends GuildSubCommand{

	public RoleSaverCommand(){
		super("rolesaver", "Enabled/Disables saving of user roles on leave");
		addOptions(
			new CommandOptionBoolean("enabled", "Whether role saving is enabled or disabled").required()
		);
	}

	@Override
	public void run(Options options, GuildCommandContext ctx){
		var enabled = options.getBoolean("enabled");
		ctx.get(SettingsModule.class).setRoleSaverEnabled(ctx.getGuildId(), enabled);
		ctx.reply((enabled ? "Enabled" : "Disabled") + " role saving");
	}

}
